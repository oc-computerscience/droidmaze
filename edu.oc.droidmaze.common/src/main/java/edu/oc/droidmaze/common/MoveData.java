package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import edu.oc.droidmaze.api.Direction;
import java.util.Objects;

public final class MoveData {

    private Direction direction;
    private int distance;

    public MoveData() {}

    public MoveData(final Direction direction, final int distance) {
        this.direction = direction;
        this.distance = distance;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MoveData moveData = (MoveData) o;
        return distance == moveData.distance &&
            direction == moveData.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, distance);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("direction", direction)
            .add("distance", distance)
            .toString();
    }
}
