package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public final class MazeData {

    private LevelData level;

    public MazeData() {}

    public MazeData(LevelData level) {
        this.level = level;
    }

    public LevelData getLevel() {
        return level;
    }

    public void setLevel(LevelData level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MazeData mazeData = (MazeData) o;
        return Objects.equals(level, mazeData.level);
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
