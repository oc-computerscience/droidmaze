package edu.oc.droidmaze.api;

import edu.oc.droidmaze.api.impl.ServerProviderFactory;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class is the core of the droidmaze system. Nothing will change until {@link #commit()} is called. This is a
 * singleton class, and should be accessed through {@link #getInstance()}.
 * <p>
 * It is very important to understand the terminology of turn: A turn in this system is one "move" in the maze. In
 * every turn each droid has the opportunity to make at most one move. A turn will always last 500 ms, if a
 * {@link Droid} has not completed processing by this point it will be interrupted and reset for the next turn, and
 * all changes that {@link Droid} made before calling {@link #commit()} will be lost.
 */
public interface Server {

    /**
     * The static, immutable, and final instance of the {@link Server} object. This should be used for all accesses
     * of this class. This is a constant, single-return method - it is not expensive.
     *
     * @return The system-wide instance of the {@link Server} object. Never null.
     */
    @NotNull
    @Contract(pure = true)
    static Server getInstance() {
        return ServerProviderFactory.getProvider().getServer();
    }

    /**
     * Get the single {@link Maze} object which corresponds to this system.
     *
     * @return The single {@link Maze} object which corresponds to this system. Never null.
     */
    @NotNull
    @Contract(pure = true)
    Maze getMaze();

    /**
     * Get the immutable set of droids currently running on the same maze as the droid. As mazes are singletons, this
     * represents all the droids in the system. This set will always include the caller. Any attempt to modify this set
     * will result in a runtime exception. Unlike slots, any droid has full visibility to all other droids in the
     * system.
     *
     * @return The immutable set of droids currently on the same level as this droid. Never null.
     */
    @NotNull
    @Contract(pure = true)
    default Set<Droid> getDroids() {
        return getMaze().getDroids();
    }

    /**
     * Get the count of the current turn. The first turn will have a turn count of 1. This has no effect on the
     * system, it is only for reference purposes.
     *
     * @return The count of the current turn.
     */
    @Contract(pure = true)
    int getTurnCount();

    /**
     * Get the {@link MessageChannel} instance. This is what should be used for inter-droid communication.
     *
     * @return The {@link MessageChannel} instance. Never null.
     */
    @NotNull
    @Contract(pure = true)
    MessageChannel getMessageChannel();

    /**
     * This is a helper method to run some function back on the main maze thread. This will execute on the main maze
     * thread at the time indicated by the {@link Schedule} parameter. The three options are as folows:
     *
     * <ul>
     *     <li>{@link Schedule#START} to run the task at the beginning of the next turn</li>
     *     <li>{@link Schedule#PRE_COMMIT} to run the task immediately before the move is to be sent to the server</li>
     *     <li>{@link Schedule#POST_COMMIT} to run the task immediately after the move was sent to the server</li>
     * </ul>
     *
     * <p>
     * As this method is simply a scheduling method, it will return immediately when called. When the tasks are
     * executed, they will be executed in the order they were scheduled.
     *
     * @param runnable The task to run. Must not be null.
     * @param schedule When to run the task. Must not be null.
     */
    void runSync(@NotNull Runnable runnable, @NotNull Schedule schedule);

    /**
     * Send all actions performed by the {@link Droid} back to the server. If this method is not called by the end of
     * the turn, the {@link Droid}'s move for this turn is revoked. A turn will strictly last at most 500 ms. By the
     * end of 500 ms, if this method has not yet been called, the {@link Droid} will be forcibly interrupted, all
     * changes will be dropped, and the system will reset for the next turn.
     * <p>
     * This method can only be called once per turn. If it is called a second time, a
     * {@link edu.oc.droidmaze.api.exception.DroidAlreadyCommittedException} will be thrown.
     *
     * @throws edu.oc.droidmaze.api.exception.DroidAlreadyCommittedException if called more than once in one turn.
     */
    void commit();
}
