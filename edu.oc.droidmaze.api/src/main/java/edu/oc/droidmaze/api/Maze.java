package edu.oc.droidmaze.api;

import edu.oc.droidmaze.api.exception.DroidNotFinishedException;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the maze as the droid sees it. All information about the current state of the system
 * can be queried through this class. This is a singleton and should only be accessed through {@link Server#getMaze()}.
 */
public interface Maze {

    /**
     * Get the level this droid is currently on. The level holds information regarding what the droid can currently see.
     * Many of the methods on the Maze itself are actually convenience methods directing to this level.
     *
     * @return The level this droid is currently on. Never null.
     */
    @NotNull
    @Contract(pure = true)
    Level getLevel();

    /**
     * Get the view the droid can currently see. Nothing else is visible to the droid until the next turn, where the
     * view would presumably change after the droid has moved. The view may not necessarily change from one turn to
     * another if the droid decides not to move. This is simply a convenience method for calling
     * {@link Level#getView()}.
     *
     * @return The {@link View} currently visible to the droid. Never null.
     */
    @NotNull
    @Contract(pure = true)
    default View getView() {
        return getLevel().getView();
    }

    /**
     * Get the immutable set of droids currently running on the same maze as the droid. As mazes are singletons, this
     * represents all the droids in the system, and is simply a convenience method for calling
     * {@link Server#getDroids()}. This set will always include the caller. Any attempt to modify this set will result
     * in a runtime exception. Unlike slots, any droid has full visibility to all other droids in the system.
     *
     * @return The immutable set of droids currently on the same level as this droid. Never null.
     */
    @NotNull
    @Contract(pure = true)
    default Set<Droid> getDroids() {
        return Server.getInstance().getDroids();
    }

    /**
     * Move the droid. The droid will not actually move until the next turn. No changes will be sent to the system
     * until {@link Server#commit()} is called.
     * <p>
     * This method will throw an {@link edu.oc.droidmaze.api.exception.InvalidMoveException} if the {@link Maze} is not
     * able to complete this move (for example, if the {@link Droid} attempts to move into a wall, or move between
     * levels through a {@link Slot} which is not a portal.
     *
     * @param direction The direction to move the droid 1 slot over. Must not be null.
     * @throws edu.oc.droidmaze.api.exception.InvalidMoveException if the {@link Maze} is not able to successfully
     *         complete this move.
     */
    @Contract(value = "null -> fail")
    default void move(@NotNull final Direction direction) {
        move(direction, 1);
    }

    /**
     * Move the droid some distance. The droid will not actually move until the next turn. No changes will be sent to
     * the system until {@link Server#commit()} is called.
     * <p>
     * This method will throw an {@link edu.oc.droidmaze.api.exception.InvalidMoveException} if the {@link Maze} is not
     * able to complete this move (for example, if the {@link Droid} attempts to move into a wall, or move between
     * levels through a {@link Slot} which is not a portal, or attempts to move a distance that is not allowed (see
     * {@link Effect#SPRINT}).
     *
     * @param direction The direction to move the droid. Must not be null.
     * @param distance The number of slots to move.
     * @throws edu.oc.droidmaze.api.exception.InvalidMoveException if the {@link Maze} is not able to successfully
     *         complete this move.
     */
    @Contract(value = "null, _ -> fail")
    void move(@NotNull final Direction direction, final int distance);

    /**
     * Ask the server to check if the droid has finished the maze. If it has, the droid will be removed from the game
     * and will be marked as completed. This method can only successfully be called if the droid is currently sitting
     * on a slot of type {@link SlotType#FINISH_SLOT}. If it is not, a {@link DroidNotFinishedException} will be thrown.
     * It is up to the droid to call this method once it has reached this slot. If the droid does not call this method,
     * it will not be marked as finished.
     *
     * @throws DroidNotFinishedException If the droid that calls this method is not currently sitting on a slot of type
     *                                   {@link SlotType#FINISH_SLOT}
     */
    void finish() throws DroidNotFinishedException;
}
