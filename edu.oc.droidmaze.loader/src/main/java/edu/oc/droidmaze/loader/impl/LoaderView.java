package edu.oc.droidmaze.loader.impl;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.oc.droidmaze.api.Level;
import edu.oc.droidmaze.api.Slot;
import edu.oc.droidmaze.api.View;
import edu.oc.droidmaze.common.ViewData;
import edu.oc.droidmaze.loader.util.Util;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class LoaderView implements View {

    private final List<Slot> northSlots;
    private final List<Slot> southSlots;
    private final List<Slot> eastSlots;
    private final List<Slot> westSlots;
    private final Set<Slot> allSlots;
    private final int viewDistance;
    private final Level level;

    private LoaderView(
        @NotNull final List<Slot> northSlots,
        @NotNull final List<Slot> southSlots,
        @NotNull final List<Slot> eastSlots,
        @NotNull final List<Slot> westSlots,
        final int viewDistance,
        @NotNull final Level level
    ) {
        this.northSlots = northSlots;
        this.southSlots = southSlots;
        this.eastSlots = eastSlots;
        this.westSlots = westSlots;

        this. allSlots = ImmutableSet.<Slot>builder()
            .addAll(northSlots)
            .addAll(southSlots)
            .addAll(eastSlots)
            .addAll(westSlots)
            .build();

        this.viewDistance = viewDistance;
        this.level = level;
    }

    @NotNull
    public static View parse(@NotNull final ViewData data, @NotNull final Level level) {

        final List<Slot> northSlots = ImmutableList.copyOf(
            data.getNorthSlots().stream().map(d -> LoaderSlot.parse(d, level)).collect(Collectors.toList())
        );

        final List<Slot> southSlots = ImmutableList.copyOf(
            data.getSouthSlots().stream().map(d -> LoaderSlot.parse(d, level)).collect(Collectors.toList())
        );

        final List<Slot> eastSlots = ImmutableList.copyOf(
            data.getEastSlots().stream().map(d -> LoaderSlot.parse(d, level)).collect(Collectors.toList())
        );

        final List<Slot> westSlots = ImmutableList.copyOf(
            data.getWestSlots().stream().map(d -> LoaderSlot.parse(d, level)).collect(Collectors.toList())
        );

        return new LoaderView(northSlots, southSlots, eastSlots, westSlots, data.getViewDistance(), level);
    }

    @NotNull
    @Override
    public List<Slot> getNorthSlots() {
        Util.checkProcess();
        return northSlots;
    }

    @NotNull
    @Override
    public List<Slot> getSouthSlots() {
        Util.checkProcess();
        return southSlots;
    }

    @NotNull
    @Override
    public List<Slot> getEastSlots() {
        Util.checkProcess();
        return eastSlots;
    }

    @NotNull
    @Override
    public List<Slot> getWestSlots() {
        Util.checkProcess();
        return westSlots;
    }

    @NotNull
    @Override
    public Set<Slot> getAllSlots() {
        Util.checkProcess();
        return allSlots;
    }

    @Override
    public int getViewDistance() {
        Util.checkProcess();
        return viewDistance;
    }

    @Override
    public @NotNull Level getLevel() {
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
        final LoaderView that = (LoaderView) o;
        return viewDistance == that.viewDistance &&
            Objects.equals(northSlots, that.northSlots) &&
            Objects.equals(southSlots, that.southSlots) &&
            Objects.equals(eastSlots, that.eastSlots) &&
            Objects.equals(westSlots, that.westSlots) &&
            Objects.equals(allSlots, that.allSlots) &&
            level.getZ() == that.level.getZ(); // Don't compare levels directly to prevent infinite recursion
    }

    @Override
    public int hashCode() {
        return Objects.hash(northSlots, southSlots, eastSlots, westSlots, allSlots, viewDistance, level.getZ());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("northSlots", northSlots)
            .add("southSlots", southSlots)
            .add("eastSlots", eastSlots)
            .add("westSlots", westSlots)
            .add("allSlots", allSlots)
            .add("viewDistance", viewDistance)
            .add("level-z", level.getZ())
            .toString();
    }
}
