package edu.oc.droidmaze.api.exception;

/**
 * Exception thrown when a {@link edu.oc.droidmaze.api.Droid} attempts to make an invalid move.
 */
public class InvalidMoveException extends RuntimeException {

    private final Reason reason;

    public InvalidMoveException(final Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

    /**
     * An enum which represents the possible reasons why an {@link InvalidMoveException} would be thrown. The reason
     * can be retrieved with {@link InvalidMoveException#getReason()}.
     */
    public enum Reason {
        /**
         * The {@link edu.oc.droidmaze.api.Droid} attempted to move into a wall.
         */
        WALL,
        /**
         * The {@link edu.oc.droidmaze.api.Droid} attempted to move into a slot already occupied by another
         * {@link edu.oc.droidmaze.api.Droid}.
         */
        DROID,
        /**
         * The {@link edu.oc.droidmaze.api.Droid} attempted to move up through a non-existent portal.
         */
        NO_PORTAL_UP,
        /**
         * The {@link edu.oc.droidmaze.api.Droid} attempted to move down through a non-existent portal.
         */
        NO_PORTAL_DOWN
    }
}
