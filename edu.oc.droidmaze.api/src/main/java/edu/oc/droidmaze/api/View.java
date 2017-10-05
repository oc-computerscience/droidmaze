package edu.oc.droidmaze.api;

import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents what the {@link Droid} is currently able to see. What this class contains may change depending
 * on the effects applied to the {@link Droid} by the {@link Slot} it is sitting on.
 */
public interface View {

    /**
     * The {@link Slot}s visible to the {@link Droid} to the {@link Direction#NORTH}, going in order away from the
     * {@link Droid}. If a {@link Slot} of type {@link SlotType#WALL}, or a {@link Slot} holding a {@link Droid} is ever
     * reached, the list will end, even if the {@link Droid} can see farther.
     * <p>
     * The list returned by this method is immutable, any changes attempted to be made on this list will result in a
     * runtime exception.
     *
     * @return The list of {@link Slot}s visible to the {@link Droid} to the {@link Direction#NORTH}. Never null.
     */
    @NotNull
    @Contract(pure = true)
    List<Slot> getNorthSlots();

    /**
     * The {@link Slot}s visible to the {@link Droid} to the {@link Direction#SOUTH}, going in order away from the
     * {@link Droid}. If a {@link Slot} of type {@link SlotType#WALL}, or a {@link Slot} holding a {@link Droid} is ever
     * reached, the list will end, even if the {@link Droid} can see farther.
     * <p>
     * The list returned by this method is immutable, any changes attempted to be made on this list will result in a
     * runtime exception.
     *
     * @return The list of {@link Slot}s visible to the {@link Droid} to the {@link Direction#SOUTH}. Never null.
     */
    @NotNull
    @Contract(pure = true)
    List<Slot> getSouthSlots();

    /**
     * The {@link Slot}s visible to the {@link Droid} to the {@link Direction#EAST}, going in order away from the
     * {@link Droid}. If a {@link Slot} of type {@link SlotType#WALL}, or a {@link Slot} holding a {@link Droid} is ever
     * reached, the list will end, even if the {@link Droid} can see farther.
     * <p>
     * The list returned by this method is immutable, any changes attempted to be made on this list will result in a
     * runtime exception.
     *
     * @return The list of {@link Slot}s visible to the {@link Droid} to the {@link Direction#EAST}. Never null.
     */
    @NotNull
    @Contract(pure = true)
    List<Slot> getEastSlots();

    /**
     * The {@link Slot}s visible to the {@link Droid} to the {@link Direction#WEST}, going in order away from the
     * {@link Droid}. If a {@link Slot} of type {@link SlotType#WALL}, or a {@link Slot} holding a {@link Droid} is ever
     * reached, the list will end, even if the {@link Droid} can see farther.
     * <p>
     * The list returned by this method is immutable, any changes attempted to be made on this list will result in a
     * runtime exception.
     *
     * @return The list of {@link Slot}s visible to the {@link Droid} to the {@link Direction#WEST}. Never null.
     */
    @NotNull
    @Contract(pure = true)
    List<Slot> getWestSlots();

    /**
     * An immutable, unordered set of all the {@link Slot}s visible to the {@link Droid}.
     *
     * @return An immutable, unordered set of {@link Slot}s visible to the {@link Droid}.
     */
    @NotNull
    @Contract(pure = true)
    Set<Slot> getAllSlots();

    /**
     * The current view distance the {@link Droid} may be able to see in any direction.
     *
     * @return The current view distance the {@link Droid} may be able to see in any direction.
     */
    @Contract(pure = true)
    int getViewDistance();

    /**
     * Get the {@link Level} this {@link View} is in.
     *
     * @return The {@link Level} this {@link View} is in. Never null.
     */
    @NotNull
    @Contract(pure = true)
    Level getLevel();
}
