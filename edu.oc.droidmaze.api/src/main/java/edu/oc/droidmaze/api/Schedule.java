package edu.oc.droidmaze.api;

/**
 * This enum defines when work will be executed back on the main maze thread for the
 * {@link Server#runSync(Runnable, Schedule)} helper method.
 */
public enum Schedule {
    /**
     * Run the task at the beginning of the next turn.
     */
    START,
    /**
     * Run the task at the end of the current turn, immediately before the move will be sent to the server.
     */
    PRE_COMMIT,
    /**
     * Run the task at the end of the current turn, immediately after the move has been sent to the server.
     */
    POST_COMMIT
}
