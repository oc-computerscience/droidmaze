package edu.oc.droidmaze.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents an x-y location in the {@link Level} of the {@link Maze}. This class is
 * immutable, any changes to the droid's location must be made with {@link Maze#move(Direction)}.
 */
public interface Location {

    /**
     * The x-coordinate of the location. Starts at 0 on the left of the maze and moves left as x grows larger.
     *
     * @return The x-coordinate of the slot's location.
     */
    @Contract(pure = true)
    int getX();

    /**
     * The y-coordinate of the location. Starts at 0 on the top of the maze and moves down as y grows larger.
     *
     * @return The y-coorindate of the slot's location.
     */
    @Contract(pure = true)
    int getY();

    /**
     * The {@link Level} the location is currently on.
     *
     * @return The {@link Level} the location is currently on. Never null.
     */
    @NotNull
    @Contract(pure = true)
    Level getLevel();
}
