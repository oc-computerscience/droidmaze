package edu.oc.droidmaze.loader.impl;

import com.google.common.base.MoreObjects;
import edu.oc.droidmaze.api.Level;
import edu.oc.droidmaze.api.Location;
import edu.oc.droidmaze.common.LocationData;
import edu.oc.droidmaze.loader.util.Util;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class LoaderLocation implements Location {

    private final int x;
    private final int y;
    private final Level level;

    private LoaderLocation(final int x, final int y, @NotNull final Level level) {
        this.x = x;
        this.y = y;
        this.level = level;
    }

    @NotNull
    public static Location parse(@NotNull final LocationData data, @NotNull final Level level) {
        return new LoaderLocation(data.getX(), data.getY(), level);
    }

    @Override
    public int getX() {
        Util.checkProcess();
        return x;
    }

    @Override
    public int getY() {
        Util.checkProcess();
        return y;
    }

    @Override
    @NotNull
    public Level getLevel() {
        Util.checkProcess();
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LoaderLocation that = (LoaderLocation) o;
        return x == that.x &&
            y == that.y &&
            level.getZ() == that.level.getZ(); // Don't compare levels directly to prevent infinite recursion
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, level.getZ());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("x", x)
            .add("y", y)
            .add("level-z", level.getZ())
            .toString();
    }
}
