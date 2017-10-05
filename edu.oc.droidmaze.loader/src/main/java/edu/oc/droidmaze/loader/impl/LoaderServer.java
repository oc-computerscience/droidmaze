package edu.oc.droidmaze.loader.impl;

import com.google.common.base.MoreObjects;
import edu.oc.droidmaze.api.Direction;
import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.api.Maze;
import edu.oc.droidmaze.api.MessageChannel;
import edu.oc.droidmaze.api.Schedule;
import edu.oc.droidmaze.api.Server;
import edu.oc.droidmaze.common.MoveData;
import edu.oc.droidmaze.loader.Bootstrap;
import edu.oc.droidmaze.loader.util.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoaderServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoaderServer.class);

    private static LoaderServer instance = new LoaderServer();

    private final ConcurrentHashMap<Schedule, List<Runnable>> work = new ConcurrentHashMap<>();

    private AtomicReference<MoveData> nextMove = new AtomicReference<>();

    private Droid userDroid = null;
    private Maze maze;
    private Set<Droid> droids;
    private int turnCount;

    @NotNull
    @Contract(pure = true)
    public Droid getUserDroid() {
        return userDroid;
    }

    @NotNull
    @Contract(pure = true)
    public static LoaderServer getInstance() {
        return instance;
    }

    public void setUserDroid(@NotNull final Droid droid) {
        userDroid = droid;
    }

    public void acceptDroid(@NotNull final Droid droid, @NotNull final List<Method> initMethods) {
        for (Method method : initMethods) {
            try {
                method.invoke(droid);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("Error in droid init", e.getCause());
                Bootstrap.shutdown();
            }
        }
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public Maze getMaze() {
        Util.checkProcess();
        return maze;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public Set<Droid> getDroids() {
        Util.checkProcess();
        return droids;
    }

    public void setDroids(Set<Droid> droids) {
        this.droids = droids;
    }

    @Override
    @Contract(pure = true)
    public int getTurnCount() {
        Util.checkProcess();
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public MessageChannel getMessageChannel() {
        Util.checkProcess();
        return LoaderMessageChannel.getInstance();
    }

    @Override
    public void runSync(@NotNull Runnable runnable, @NotNull Schedule schedule) {
        work.compute(schedule, (s, r) -> {
            if (r == null) {
                final ArrayList<Runnable> r1 = new ArrayList<>();
                r1.add(runnable);
                return r1;
            } else {
                r.add(runnable);
                return r;
            }
        });
    }

    public void executeWork(@NotNull final Schedule schedule) {
        final List<Runnable> set = work.remove(schedule);
        if (set == null) {
            return;
        }

        for (Runnable r : set) {
            r.run();
        }
    }

    @Override
    public void commit() {
        Util.checkProcess();
        final Direction direction = ((LoaderMaze) maze).getDirection();
        final int distance = ((LoaderMaze) maze).getDistance();
        nextMove.set(new MoveData(direction, distance));
    }

    @Nullable
    public MoveData getNextMove() {
        return nextMove.getAndSet(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LoaderServer that = (LoaderServer) o;
        return turnCount == that.turnCount &&
            Objects.equals(userDroid, that.userDroid) &&
            Objects.equals(maze, that.maze) &&
            Objects.equals(droids, that.droids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDroid, maze, droids, turnCount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("userDroid", userDroid)
            .add("maze", maze)
            .add("droids", droids)
            .add("turnCount", turnCount)
            .toString();
    }
}
