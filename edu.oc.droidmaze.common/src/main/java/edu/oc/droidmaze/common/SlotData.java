package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import edu.oc.droidmaze.api.Effect;
import edu.oc.droidmaze.api.SlotType;
import java.util.Objects;
import java.util.Set;

public final class SlotData {

    private LocationData location;
    private Set<Effect> effects;
    private DroidData droid;
    private SlotType type;

    public SlotData() {}

    public SlotData(LocationData location, Set<Effect> effects, DroidData droid, SlotType type) {
        this.location = location;
        this.effects = effects;
        this.droid = droid;
        this.type = type;
    }

    public LocationData getLocation() {
        return location;
    }

    public void setLocation(LocationData location) {
        this.location = location;
    }

    public Set<Effect> getEffects() {
        return effects;
    }

    public void setEffects(Set<Effect> effects) {
        this.effects = effects;
    }

    public DroidData getDroid() {
        return droid;
    }

    public void setDroid(DroidData droid) {
        this.droid = droid;
    }

    public SlotType getType() {
        return type;
    }

    public void setType(SlotType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SlotData slotData = (SlotData) o;
        return Objects.equals(location, slotData.location) &&
            Objects.equals(effects, slotData.effects) &&
            Objects.equals(droid, slotData.droid) &&
            type == slotData.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, effects, droid, type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("location", location)
            .add("effects", effects)
            .add("droid", droid)
            .add("type", type)
            .toString();
    }
}
