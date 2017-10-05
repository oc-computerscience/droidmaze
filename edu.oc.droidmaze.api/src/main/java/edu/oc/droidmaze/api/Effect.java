package edu.oc.droidmaze.api;

/**
 * This enum represents the effects which a {@link SlotType} can apply to a {@link Droid}.
 */
public enum Effect {
    /**
     * The {@link Droid} can only see 1 {@link Slot} in all directions, rather than the standard 2.
     */
    FOG,
    /**
     * The {@link Droid} can see 3 {@link Slot}s in all directions, rather than the standard 2.
     */
    TELESCOPE,
    /**
     * The {@link Droid} can move 1 or 2 {@link Slot}s in any direction, rather than the standard 1.
     */
    SPRINT
}
