package edu.oc.droidmaze.api;

/**
 * This enum represents the possible reasons why a droid movement failed.
 */
public enum MoveFailure {
    /**
     * Another droid already occupies this slot. This can happen even if proper checks were done due to how the server
     * processes move requests on a first-come-first-serve basis.
     */
    SLOT_ALREADY_OCCUPIED,
    /**
     * The droid attempted to move into a slot which is a wall.
     */
    WALL,
    /**
     * The droid attempted to move {@link Direction#UP up} or {@link Direction#DOWN down} on a slot which is not a
     * portal, or a portal facing the wrong direction.
     */
    NOT_A_PORTAL,
    /**
     * A droid requested to move a distance which it was not allowed. This could be a distance of 0, a negative
     * distance, or a distance further than the droid is able to move for this turn.
     */
    DISTANCE_NOT_ALLOWED
}
