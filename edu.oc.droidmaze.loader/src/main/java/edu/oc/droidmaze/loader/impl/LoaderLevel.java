package edu.oc.droidmaze.loader.impl;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.api.Level;
import edu.oc.droidmaze.api.Slot;
import edu.oc.droidmaze.api.View;
import edu.oc.droidmaze.common.LevelData;
import edu.oc.droidmaze.loader.util.Util;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class LoaderLevel implements Level {

    private final Set<Droid> droids;
    private final int z;
    private Slot currentSlot;
    private View view;

    private LoaderLevel(@NotNull final Set<Droid> droids, final int z) {
        this.droids = droids;
        this.z = z;
    }

    @NotNull
    public static Level parse(@NotNull final LevelData data) {
        final Set<Droid> droids = ImmutableSet.<Droid>builder()
            .addAll(data.getDroids().stream().map(VirtualDroid::parse).collect(Collectors.toList()))
            .build();

        final LoaderLevel level = new LoaderLevel(droids, data.getZ());

        level.currentSlot = LoaderSlot.parse(data.getCurrentSlot(), level);
        level.view = LoaderView.parse(data.getView(), level);

        return level;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public Set<Droid> getDroids() {
        Util.checkProcess();
        return droids;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public Slot getCurrentSlot() {
        Util.checkProcess();
        return currentSlot;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public View getView() {
        Util.checkProcess();
        return view;
    }

    @Override
    public int getZ() {
        Util.checkProcess();
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LoaderLevel that = (LoaderLevel) o;
        return Objects.equals(droids, that.droids) &&
            Objects.equals(currentSlot, that.currentSlot) &&
            Objects.equals(view, that.view) &&
            z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(droids, currentSlot, view, z);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("droids", droids)
            .add("currentSlot", currentSlot)
            .add("view", view)
            .add("Z", z)
            .toString();
    }
}
