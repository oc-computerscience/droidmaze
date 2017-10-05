package edu.oc.droidmaze.loader.socket;

import com.google.gson.reflect.TypeToken;
import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.loader.Bootstrap;
import edu.oc.droidmaze.loader.ExternalServer;
import edu.oc.droidmaze.loader.TurnExecutor;
import edu.oc.droidmaze.loader.impl.LoaderServer;
import edu.oc.droidmaze.loader.util.Util;
import java.io.IOException;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jetbrains.annotations.NotNull;

/**
 * Overwatch attempts to interrupt the running process at the end of the turn -- this helps keep things moving
 * smoothly and in-sync with the server.
 */
@WebSocket(maxTextMessageSize = 1024 * 1024)
public final class Overwatch extends PersistentConnection {

    private static final Logger LOGGER = LogManager.getLogger(Overwatch.class);

    private final TurnExecutor executor;

    private State state = State.AWAITING_READY;

    private long start = 0;

    public enum State {
        AWAITING_READY,
        BEFORE_INIT,
        BEFORE_START,
        START,
        AFTER_START,
        FINISHED
    }

    public Overwatch(@NotNull final String url, @NotNull final TurnExecutor executor) {
        super(url, "overwatch");
        this.executor = executor;
    }

    public State getState() {
        return state;
    }

    public boolean hasStarted() {
        return state != State.BEFORE_INIT && state != State.AWAITING_READY;
    }

    @OnWebSocketConnect
    public void onConnect(final Session session) {
        LOGGER.info("Connected to server: {}", session.getRemoteAddress());
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOGGER.info("Connection closed: {} - {}", statusCode, reason);
        executor.stop();
        latch.countDown();
        if (state != State.FINISHED) {
            LOGGER.error("Disconnected from server, quitting");
            ExternalServer.getInstance().getMessenger().kill();
            System.exit(1);
        }
    }

    @OnWebSocketMessage
    public void onMessage(final String text) throws IOException {
        LOGGER.info("Received message from server: {}", text);
        switch (state) {
            case AWAITING_READY:
                handleReady(text);
                break;
            case BEFORE_INIT:
                handleBeforeInit(text);
                break;
            case BEFORE_START:
                handleBeforeStart(text);
                break;
            case START:
                executor.startTurn(handleStart(text));
                break;
            case AFTER_START:
                handleAfterStart(text);
                break;
        }
    }

    private void handleReady(final String text) {
        if (!text.equals("ready")) {
            if (text.startsWith("init")) {
                // Looks like we connected after the server sent out the ready broadcast
                state = State.BEFORE_INIT;
                handleBeforeInit(text);
                return;
            }

            LOGGER.error("In state '{}', expected server response: READY", state);
            LOGGER.error("UNEXPECTED SERVER RESPONSE: {}", text);
            Bootstrap.shutdown();
        }

        LOGGER.info("Server has completed initial startup, waiting for game to start");

        state = State.BEFORE_INIT;
    }

    private void handleBeforeInit(final String text) {
        if (!text.startsWith("init")) {
            LOGGER.error("In state '{}', expected server response to start with 'init'", state);
            LOGGER.error("UNEXPECTED SERVER RESPONSE: {}", text);
            Bootstrap.shutdown();
        }

        final String data = text.substring(4);
        LoaderServer.getInstance().setDroids(Util.gson().fromJson(data, new TypeToken<Set<Droid>>() {}.getType()));

        state = State.BEFORE_START;
    }

    private void handleBeforeStart(final String text) throws IOException {
        start = System.currentTimeMillis();
        Util.resetInterrupt();

        if (!text.equals("start turn")) {
            LOGGER.error("In state '{}', expected server response of 'start turn'", state);
            LOGGER.error("UNEXPECTED SERVER RESPONSE: {}", text);
            Bootstrap.shutdown();
        }

        state = State.START;
    }

    private long handleStart(final String text) {
        final long newCount;
        try {
            newCount = Long.parseLong(text);
        } catch (final NumberFormatException e) {
            LOGGER.error("In state '{}', expected integer server response", state);
            LOGGER.error("UNEXPECTED SERVER RESPONSE: {}", text);
            Bootstrap.shutdown();
            // This return is only here to make the compiler happy
            // It will never actually be reached
            return 0;
        }

        state = State.AFTER_START;
        return newCount;
    }

    private void handleAfterStart(final String text) {
        if (!text.equals("end turn")) {
            LOGGER.error("In state '{}', expected server response of 'end turn'", state);
            LOGGER.error("UNEXPECTED SERVER RESPONSE: {}", text);
            Bootstrap.shutdown();
        }

        final long end = System.currentTimeMillis();

        executor.endTurn();

        Util.getTime().set(end - start);
        Util.setInterrupt();

        state = State.BEFORE_START;
    }

    public void kill() {
        executor.stop();
        latch.countDown();
    }
}
