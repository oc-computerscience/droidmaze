package edu.oc.droidmaze.api;

import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents one "slice" of the {@link Maze}. {@link Droid}s primarily navigate the {@link Maze} on this 2D
 * plane, with the exception of special {@link Slot}s which allow vertical travel. These special {@link Slot}s are
 * really moving the {@link Droid} from one {@link Level} to another. This class is an immutable, read-only class - it
 * has no actions. Movement actions should be preformed with {@link Maze#move(Direction)}.
 */
public interface Level {

    /**
     * Get the immutable set of droids currently running on the same level as the droid. This set will always include
     * caller. Any attempt to modify this set will result in a runtime exception. Unlike slots, any droid has full
     * visibility to all other droids in the system.
     *
     * @return The immutable set of droids currently on the same level as this droid. Never null.
     */
    @NotNull
    @Contract(pure = true)
    Set<Droid> getDroids();

    /**
     * Get the {@link Slot} the {@link Droid} is currently in. {@link Slot}s represent the primary method of interacting
     * with the {@link Maze}, and may effect things such as the {@link Droid}s current {@link View}.
     *
     * @return The {@link Slot} the {@link Droid} is currently in. Never null.
     */
    @NotNull
    @Contract(pure = true)
    Slot getCurrentSlot();

    /**
     * Get the {@link View} the {@link Droid} can currently see. This is the only part of the {@link Maze} visible to
     * the {@link Droid}, and may be modified based on whatever effects their current {@link Slot} is applied to them.
     *
     * @return The {@link View} the {@link Droid} is currently able to see. Never null.
     */
    @NotNull
    @Contract(pure = true)
    View getView();

    /**
     * Get the z-coordinate that represents this level in the {@link Maze}. 0 is the lowest, going up as it z increases.
     *
     * @return The z-cooridnate of this {@link Level}.
     */
    @Contract(pure = true)
    int getZ();
}
