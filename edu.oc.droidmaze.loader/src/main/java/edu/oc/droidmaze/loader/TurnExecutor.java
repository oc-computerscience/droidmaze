package edu.oc.droidmaze.loader;

import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.api.Schedule;
import edu.oc.droidmaze.loader.impl.LoaderServer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

public final class TurnExecutor implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(TurnExecutor.class);

    private final List<Method> turnExecuteMethods = new ArrayList<>();

    public TurnExecutor(final List<Method> methods) {
        this.turnExecuteMethods.addAll(methods);
    }

    private enum State {
        IN_TURN,
        END_TURN,
        START_TURN,
        WAIT_TURN,
        STOP
    }

    // The number of milliseconds in a turn
    // TODO Make this a server-side value?
    private static final long TURN_LENGTH = 500L;

    // Turn status data
    private static final AtomicLong turnCount = new AtomicLong(0);
    private static final AtomicReference<State> currentState = new AtomicReference<>();

    private void executeTurn() throws Throwable {
        LOGGER.info("Getting turn state from server");
        ExternalServer.getInstance().updateState();
        Throwable thrown = null;
        LOGGER.info("Executing droid turn handling logic");
        final Droid droid = LoaderServer.getInstance().getUserDroid();
        for (Method method : turnExecuteMethods) {
            try {
                method.invoke(droid);
            } catch (final Throwable t) {
                if (thrown == null) {
                    thrown = t;
                } else {
                    thrown.addSuppressed(t);
                }
            }
        }

        if (thrown != null) {
            throw thrown;
        }
    }

    @Contract(pure = true)
    private boolean isFinished() {
        return currentState.get() == State.STOP;
    }

    /**
     * This method is called when the server has instructed us that it has completed processing a turn.
     */
    public void endTurn() {
        currentState.set(State.END_TURN);
    }

    /**
     * This method is called when the sever has instructed us that it is starting a new turn.
     */
    public void startTurn(final long newCount) {
        turnCount.set(newCount);
        currentState.set(State.START_TURN);
    }

    public void stop() {
        currentState.set(State.STOP);
    }

    @Override
    public void run() {
        // We attempt to keep track of turns ourselves, but the server overrules and we will reset
        // back to the server's timing on every turn
        while (true) {
            // Wait for the server to tell use to start the next turn
            while (!currentState.compareAndSet(State.START_TURN, State.IN_TURN)) {
                Thread.onSpinWait();
            }
            LOGGER.info("Turn starting");

            final long start = System.currentTimeMillis();

            LOGGER.info("Executing start work");
            LoaderServer.getInstance().executeWork(Schedule.START);

            try {
                executeTurn();

                if (isFinished()) {
                    break;
                }
            } catch (final Throwable t) {
                LOGGER.error("Exception thrown by droid executing turn", t);
            }

            LOGGER.info("Executing pre-commit work");
            LoaderServer.getInstance().executeWork(Schedule.PRE_COMMIT);
            LOGGER.info("Committing new state to server");
            ExternalServer.getInstance().pushNewState();
            LOGGER.info("Executing post-commit work");
            LoaderServer.getInstance().executeWork(Schedule.POST_COMMIT);

            final long end = System.currentTimeMillis();
            final long time = end - start;

            if (time > TURN_LENGTH) {
                LOGGER.warn("System running behind: {} ms behind!", TURN_LENGTH - time);
            }

            LOGGER.info("Waiting for next turn");
            // Wait for the server to end the turn (or continue if we're already into the next turn)
            while (!currentState.compareAndSet(State.END_TURN, State.WAIT_TURN) && currentState.get() != State.START_TURN) {
                Thread.onSpinWait();
            }
        }
    }
}
