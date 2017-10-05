package edu.oc.droidmaze.loader.impl;

import com.google.common.base.MoreObjects;
import edu.oc.droidmaze.api.Direction;
import edu.oc.droidmaze.api.Level;
import edu.oc.droidmaze.api.Maze;
import edu.oc.droidmaze.api.exception.DroidNotFinishedException;
import edu.oc.droidmaze.common.MazeData;
import edu.oc.droidmaze.loader.util.Util;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class LoaderMaze implements Maze {

    private final Level level;

    private Direction direction;
    private int distance;

    private LoaderMaze(@NotNull final Level level) {
        this.level = level;
    }

    @NotNull
    public static Maze parse(@NotNull final MazeData data) {
        return new LoaderMaze(LoaderLevel.parse(data.getLevel()));
    }

    @Override
    @NotNull
    public Level getLevel() {
        Util.checkProcess();
        return level;
    }

    @NotNull
    public Direction getDirection() {
        return direction;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public void move(@NotNull Direction direction, int distance) {
        Util.checkProcess();
        this.direction = direction;
        this.distance = distance;
    }

    @Override
    public void finish() throws DroidNotFinishedException {
        Util.checkProcess();
        // TODO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LoaderMaze that = (LoaderMaze) o;
        return Objects.equals(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("level", level)
            .toString();
    }
}
