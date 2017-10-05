package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.Set;

public final class ServerData {

    private MazeData maze;
    private int turnCount;
    private DroidData self;
    private Set<DroidData> droids;

    public ServerData() {}

    public ServerData(MazeData maze, int turnCount, DroidData self, Set<DroidData> droids) {
        this.maze = maze;
        this.turnCount = turnCount;
        this.self = self;
        this.droids = droids;
    }

    public MazeData getMaze() {
        return maze;
    }

    public void setMaze(MazeData maze) {
        this.maze = maze;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public DroidData getSelf() {
        return self;
    }

    public void setSelf(DroidData self) {
        this.self = self;
    }

    public Set<DroidData> getDroids() {
        return droids;
    }

    public void setDroids(Set<DroidData> droids) {
        this.droids = droids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ServerData that = (ServerData) o;
        return turnCount == that.turnCount &&
            Objects.equals(maze, that.maze) &&
            Objects.equals(self, that.self) &&
            Objects.equals(droids, that.droids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maze, turnCount, self, droids);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("maze", maze)
            .add("turnCount", turnCount)
            .add("self", self)
            .add("droids", droids)
            .toString();
    }
}
