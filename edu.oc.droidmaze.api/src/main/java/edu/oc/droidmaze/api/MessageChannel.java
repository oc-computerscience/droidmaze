package edu.oc.droidmaze.api;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents how {@link Droid}s communicate with each other. This is a singleton class in the droid maze
 * system. It should be accessed with {@link Server#getMessageChannel()}. {@link #sendMessage(Droid, String)}
 * is how data is transferred from one class to another.
 * <p>
 * Actions of this class are asynchronous from the rest of the system and can be used in-between turns.
 */
public interface MessageChannel {

    /**
     * Send the given data to {@link Droid}. The {@link Droid} will get the message as quickly as this method can be
     * called - this method can be used to send messages back-and-forth between droids in a single turn.
     * <p>
     * Due to this method executing async, this method will return immediately.
     *
     * @param toDroid The {@link Droid} to send the message to. Must not be null.
     * @param data The data to send to the {@link Droid}. Must not be null.
     * @see Droid#sendMessage(String)
     */
    void sendMessage(@NotNull final Droid toDroid, @NotNull final String data);

    /**
     * Register to the MessageChannel the listener to receive data from other {@link Droid}s. If a {@link Droid} does
     * not register a listener with the {@link MessageChannel} then that {@link Droid} will ignore all messages.]
     * <p>
     * If this method is called more than once for each {@link Droid}, it will simnply overwrite the last listener set.
     * This means a single {@link Droid} can only have one listener registered at any given time.
     * <p>
     * The {@link BiConsumer}'s types are the {@link Droid} that sent the message, and the message itself.
     * <p>
     * <b>PLEASE NOTE: </b> THE BICONSUMER PROVIDED TO THIS METHOD WILL BE RUN ON AN ASYNCHRONOUS THREAD FROM THE REST
     * OF THE APPLICATION. MUCH CARE MUST BE GIVEN TO ENSURE PROPER THREAD SAFETY.
     *
     * @param consumer The consumer to use when handling messages. Must not be null.
     */
    void registerListener(@NotNull final BiConsumer<Droid, String> consumer);
}
