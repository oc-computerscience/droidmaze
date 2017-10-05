package edu.oc.droidmaze.loader.socket;

import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.api.Server;
import edu.oc.droidmaze.common.MessageData;
import edu.oc.droidmaze.loader.ExternalServer;
import edu.oc.droidmaze.loader.impl.LoaderMessageChannel;
import edu.oc.droidmaze.loader.util.Util;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jetbrains.annotations.NotNull;

@WebSocket(maxTextMessageSize = 1024 * 1024)
public final class Messenger extends PersistentConnection {

    private static final Logger LOGGER = LogManager.getLogger(Messenger.class);

    private BiConsumer<Droid, String> consumer;
    private Session session;
    private int errorCount = 0;

    public Messenger(@NotNull final String url) {
        super(url, "messenger");

        LoaderMessageChannel.getInstance().setMessenger(this);
    }

    @OnWebSocketConnect
    public void onConnect(final Session session) throws IOException {
        LOGGER.info("Connected to server: {}", session.getRemoteAddress());
        this.session = session;
        // Tell the server who we are
        // This will be done after we have logged in, so we will have a token
        session.getRemote().sendString(ExternalServer.getInstance().getToken());
    }

    @OnWebSocketClose
    public void onClose(final int statusCode, final String reason) {
        LOGGER.info("Connection close: {} - {}", statusCode, reason);
        session = null;
        latch.countDown();
        if (ExternalServer.getInstance().getOverwatch().getState() != Overwatch.State.FINISHED) {
            LOGGER.error("Disconnected from server, quitting");
            ExternalServer.getInstance().getOverwatch().kill();
            System.exit(1);
        }
    }

    @OnWebSocketMessage
    public void onMessage(final String text) throws IOException {
        LOGGER.info("Received message from server: {}", text);
        final MessageData data = Util.gson().fromJson(text, MessageData.class);

        final Optional<Droid> droidOpt = Server.getInstance().getDroids().stream()
            .filter(d -> d.getId().equals(data.getUuid()))
            .findFirst();

        if (droidOpt.isPresent()) {
            final Droid droid = droidOpt.get();
            LOGGER.info("Parsed message from server, message from droid: '{}' ({}), message: {}",
                droid.getName(), droid.getId(), data.getMessage());
            LoaderMessageChannel.getInstance().getExecutor().submit(() ->
                consumer.accept(droid, data.getMessage()));
        } else {
            LOGGER.warn("Parsed message from server, message from unknown droid: '{}', ignoring", data.getUuid());
        }
    }

    public void setConsumer(@NotNull final BiConsumer<Droid, String> consumer) {
        this.consumer = consumer;
    }

    public void sendMessage(@NotNull final Droid droid, @NotNull final String message) {
        LOGGER.info("Sending message: '{}' to droid: '{}' ({})", message, droid.getName(), droid.getId());
        session.getRemote().sendStringByFuture(Util.gson().toJson(new MessageData(droid.getId(), message)));
    }

    public void kill() {
        session = null;
        latch.countDown();
    }
}
