package edu.oc.droidmaze.api;

import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the primary building block of a {@link Maze}. Slots can be interacted with, such as portals
 * between levels, or can provide effects to the {@link Droid} currently sitting on the slot.
 */
public interface Slot {

    /**
     * Get the immutable set of effects this {@link Slot} is applying to the {@link Droid} sitting on it. The set will
     * be empty if there are no effects.
     *
     * @return The set of effects this {@link Slot} is applying to the {@link Droid} sitting on it. Never null.
     */
    @NotNull
    Set<Effect> getEffects();

    /**
     * Get the type of this {@link Slot}. This determines whether certain directions are availabe to move in
     * {@link Maze#move(Direction)}. Vertical directions are only available when the {@link Droid} is sitting on a portal
     * of that direction.
     *
     * @return The type of this {@link Slot}. Never null.
     */
    @NotNull
    SlotType getType();

    /**
     * Get the {@link Droid} which currently occupies this {@link Slot}. If no {@link Droid} occupies this {@link Slot},
     * then an empty optional is returned.
     *
     * @return The {@link Droid} which currently occupies this {@link Slot}, or an empty optional if this {@link Slot}
     *         is empty. Never null.
     */
    @NotNull
    Optional<Droid> getDroid();

    /**
     * Get the immutable {@link Location} of this {@link Slot}.
     *
     * @return The {@link Location} of this {@link Slot}. Never null.
     */
    @NotNull
    Location getLocation();

    /**
     * Gets the {@link Level} this {@link Slot} is in.
     *
     * @return The {@link Level} of this {@link Slot}. Never null.
     */
    @NotNull
    Level getLevel();
}
