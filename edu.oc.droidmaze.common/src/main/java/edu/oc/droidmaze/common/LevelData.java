package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.Set;

public final class LevelData {

    private ViewData view;
    private SlotData currentSlot;
    private Set<DroidData> droids;
    private int z;

    public LevelData() {}

    public LevelData(ViewData view, SlotData currentSlot, Set<DroidData> droids) {
        this.view = view;
        this.currentSlot = currentSlot;
        this.droids = droids;
    }

    public ViewData getView() {
        return view;
    }

    public void setView(ViewData view) {
        this.view = view;
    }

    public SlotData getCurrentSlot() {
        return currentSlot;
    }

    public void setCurrentSlot(SlotData currentSlot) {
        this.currentSlot = currentSlot;
    }

    public Set<DroidData> getDroids() {
        return droids;
    }

    public void setDroids(Set<DroidData> droids) {
        this.droids = droids;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LevelData levelData = (LevelData) o;
        return Objects.equals(view, levelData.view) &&
            Objects.equals(currentSlot, levelData.currentSlot) &&
            Objects.equals(droids, levelData.droids) &&
            z == levelData.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(view, currentSlot, droids, z);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("view", view)
            .add("currentSlot", currentSlot)
            .add("droids", droids)
            .add("z", z)
            .toString();
    }
}
