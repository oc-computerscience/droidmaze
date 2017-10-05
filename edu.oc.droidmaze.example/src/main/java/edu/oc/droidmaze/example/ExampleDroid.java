package edu.oc.droidmaze.example;

import com.google.inject.Inject;
import edu.oc.droidmaze.api.Direction;
import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.api.HandleFailure;
import edu.oc.droidmaze.api.HandleTurn;
import edu.oc.droidmaze.api.InitDroid;
import edu.oc.droidmaze.api.MessageChannel;
import edu.oc.droidmaze.api.MoveFailure;
import edu.oc.droidmaze.api.Schedule;
import edu.oc.droidmaze.api.Server;
import edu.oc.droidmaze.api.ShutdownDroid;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ExampleDroid extends Droid {

    private static final String QUESTION = "Who are you?";

    @Inject
    private Logger logger;

    @Inject
    private Server server;

    @Inject
    private MessageChannel channel;

    @Override
    @NotNull
    public String getName() {
        return "DroidMaze Example Droid";
    }

    @InitDroid
    protected void init() throws NoSuchFieldException {
        channel.registerListener((droid, s) -> {
            if (s.equals(QUESTION)) {
                Server.getInstance().runSync(() -> {
                    /* Do things on main thread */
                }, Schedule.PRE_COMMIT);
            }
        });

        for (final Droid droid : server.getDroids()) {
            server.getMessageChannel().sendMessage(droid, "");
            droid.sendMessage(QUESTION);
        }

        logger.info("Starting up!");
    }

    @ShutdownDroid
    protected void close() {
        logger.info("Shutting down!");
    }

    @HandleTurn
    protected void workTurn() {
        // This isn't a very smart droid...
        Server.getInstance().getMaze().move(Direction.NORTH, 1);
        Server.getInstance().commit();
    }

    @HandleFailure
    protected void handleFailure(@NotNull final MoveFailure failure) {
        logger.error("uh oh... {}", failure);
    }
}
