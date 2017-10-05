package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import java.util.List;
import java.util.Objects;

public final class ViewData {

    private List<SlotData> northSlots;
    private List<SlotData> southSlots;
    private List<SlotData> eastSlots;
    private List<SlotData> westSlots;
    private int viewDistance;

    public ViewData() {}

    public ViewData(List<SlotData> northSlots, List<SlotData> southSlots, List<SlotData> eastSlots, List<SlotData> westSlots, int viewDistance) {
        this.northSlots = northSlots;
        this.southSlots = southSlots;
        this.eastSlots = eastSlots;
        this.westSlots = westSlots;
        this.viewDistance = viewDistance;
    }

    public List<SlotData> getNorthSlots() {
        return northSlots;
    }

    public void setNorthSlots(List<SlotData> northSlots) {
        this.northSlots = northSlots;
    }

    public List<SlotData> getSouthSlots() {
        return southSlots;
    }

    public void setSouthSlots(List<SlotData> southSlots) {
        this.southSlots = southSlots;
    }

    public List<SlotData> getEastSlots() {
        return eastSlots;
    }

    public void setEastSlots(List<SlotData> eastSlots) {
        this.eastSlots = eastSlots;
    }

    public List<SlotData> getWestSlots() {
        return westSlots;
    }

    public void setWestSlots(List<SlotData> westSlots) {
        this.westSlots = westSlots;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ViewData viewData = (ViewData) o;
        return viewDistance == viewData.viewDistance &&
            Objects.equals(northSlots, viewData.northSlots) &&
            Objects.equals(southSlots, viewData.southSlots) &&
            Objects.equals(eastSlots, viewData.eastSlots) &&
            Objects.equals(westSlots, viewData.westSlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(northSlots, southSlots, eastSlots, westSlots, viewDistance);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("northSlots", northSlots)
            .add("southSlots", southSlots)
            .add("eastSlots", eastSlots)
            .add("westSlots", westSlots)
            .add("viewDistance", viewDistance)
            .toString();
    }
}
