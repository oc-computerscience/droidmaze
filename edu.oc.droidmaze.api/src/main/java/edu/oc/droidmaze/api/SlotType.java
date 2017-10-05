package edu.oc.droidmaze.api;

/**
 * The enum which determines the type of a {@link SlotType}.
 */
public enum SlotType {
    /**
     * The slot all droids enter on. Other than that, this slot is always {@link #NORMAL}.
     */
    ENTRY_SLOT,
    /**
     * The goal slot of the maze. Once the droid has reached this slot, it can call
     */
    FINISH_SLOT,
    /**
     * A normal slot, no vertical movement is allowed.
     */
    NORMAL,
    /**
     * A slot which allows upward movement.
     */
    PORTAL_UP,
    /**
     * A slot which allows downward movement.
     */
    PORTAL_DOWN,
    /**
     * A slot which allows movement in both vertical directions.
     */
    PORTAL_BOTH,
    /**
     * A slot a {@link Droid} cannot sit on - this represents a boundary of the maze.
     */
    WALL
}
