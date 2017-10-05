package edu.oc.droidmaze.api;

import java.util.UUID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This is the base interface that needs to be implemented. This is not implemented by the system, this is up to the
 * user to implement. If this implement class is implemented and does not properly satisfy the contract for each method,
 * the system may behave unpredictably.
 * <p>
 * To properly implement this class, there must exist at least one method annotated with {@link HandleTurn}. Optional
 * methods also include:
 * <ul>
 *     <li>{@link HandleFailure}</li>
 *     <li>{@link InitDroid}</li>
 *     <li>{@link ShutdownDroid}</li>
 * </ul>
 */
public abstract class Droid {

    /**
     * The id instance for this {@link Droid}.
     */
    private final UUID uuid;

    public Droid() {
        this(UUID.randomUUID());
    }

    protected Droid(@NotNull final UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * The unique id for this {@link Droid} in the system. This is method is already implemented by default
     *
     * @return The id which uniquely identifies this {@link Droid} to the system. Never null.
     */
    @NotNull
    @Contract(pure = true)
    public final UUID getId() {
        return uuid;
    }

    /**
     * This is a convenience method for {@link MessageChannel#sendMessage(Droid, String)}. This {@link Droid} is used
     * as the {@code toDroid} parameter.
     *
     * @param data The data to send to the other {@link Droid}. Must not be null.
     * @see MessageChannel#sendMessage(Droid, String)
     */
    public final void sendMessage(@NotNull final String data) {
        Server.getInstance().getMessageChannel().sendMessage(this, data);
    }

    /**
     * The human readable name of this {@link Droid}. This is only used for logging and UX purposes. For a unique
     * identifier, use {@link #getId()}.
     *
     * @return The human readablename of this {@link Droid}. Never null.
     */
    @NotNull
    @Contract(pure = true)
    public abstract String getName();
}
