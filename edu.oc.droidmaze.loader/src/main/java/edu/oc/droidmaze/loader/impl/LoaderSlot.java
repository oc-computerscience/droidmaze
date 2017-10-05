package edu.oc.droidmaze.loader.impl;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.api.Effect;
import edu.oc.droidmaze.api.Level;
import edu.oc.droidmaze.api.Location;
import edu.oc.droidmaze.api.Slot;
import edu.oc.droidmaze.api.SlotType;
import edu.oc.droidmaze.common.SlotData;
import edu.oc.droidmaze.loader.util.Util;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LoaderSlot implements Slot {

    private final Set<Effect> effects;
    private final SlotType type;
    private final Droid droid;
    private final Location location;
    private final Level level;

    private LoaderSlot(
        @NotNull final Set<Effect> effects,
        @NotNull final SlotType type,
        @Nullable final Droid droid,
        @NotNull final Location location,
        @NotNull final Level level
    ) {
        this.effects = effects;
        this.type = type;
        this.droid = droid;
        this.location = location;
        this.level = level;
    }

    @NotNull
    public static Slot parse(@NotNull final SlotData data, @NotNull final Level level) {
        final Set<Effect> effects = ImmutableSet.copyOf(data.getEffects());
        final Droid droid = VirtualDroid.parse(data.getDroid());
        final Location location = LoaderLocation.parse(data.getLocation(), level);

        return new LoaderSlot(effects, data.getType(), droid, location, level);
    }

    @Override
    @NotNull
    public Set<Effect> getEffects() {
        Util.checkProcess();
        return effects;
    }

    @Override
    @NotNull
    public SlotType getType() {
        Util.checkProcess();
        return type;
    }

    @Override
    public @NotNull Optional<Droid> getDroid() {
        Util.checkProcess();
        return Optional.ofNullable(droid);
    }

    @Override
    @NotNull
    public Location getLocation() {
        Util.checkProcess();
        return location;
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
        final LoaderSlot that = (LoaderSlot) o;
        return Objects.equals(effects, that.effects) &&
            type == that.type &&
            Objects.equals(droid, that.droid) &&
            Objects.equals(location, that.location) &&
            level.getZ() == that.level.getZ(); // Don't compare levels directly to prevent infinite recursion
    }

    @Override
    public int hashCode() {
        return Objects.hash(effects, type, droid, location, level.getZ());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("effects", effects)
            .add("type", type)
            .add("droid", droid)
            .add("location", location)
            .add("level-z", level.getZ())
            .toString();
    }
}
