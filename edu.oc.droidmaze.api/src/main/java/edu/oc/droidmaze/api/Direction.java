package edu.oc.droidmaze.api;

import org.jetbrains.annotations.Contract;

/**
 * The enum which determines which direction the {@link Droid} will move. Used when calling
 * {@link Maze#move(Direction)}.
 */
public enum Direction {
    /**
     * The negative-y direction.
     */
    NORTH(0, -1),
    /**
     * The positive-y direction.
     */
    SOUTH(0, 1),
    /**
     * The positive-x direction.
     */
    EAST(1, 0),
    /**
     * The negative-x direction.
     */
    WEST(-1, 0),
    /**
     * Move one level up. Can only be used when {@link SlotType} is either {@link SlotType#PORTAL_UP} or
     * {@link SlotType#PORTAL_BOTH}.
     */
    UP(0, 0),
    /**
     * Move one level down. Can only be used when {@link SlotType} is either {@link SlotType#PORTAL_DOWN} or
     * {@link SlotType#PORTAL_DOWN}.
     */
    DOWN(0, 0);

    private final int xModifier;
    private final int yModifier;

    Direction(final int xModifier, final int yModifier) {
        this.xModifier = xModifier;
        this.yModifier = yModifier;
    }

    /**
     * The modifier integer for the x-direction.
     *
     * @return The modifier integer for the x-direction.
     */
    @Contract(pure = true)
    public int getxModifier() {
        return xModifier;
    }

    /**
     * The modifier integer for the x-direction.
     *
     * @return The modifier integer for the x-direction.
     */
    @Contract(pure = true)
    public int getyModifier() {
        return yModifier;
    }
}
