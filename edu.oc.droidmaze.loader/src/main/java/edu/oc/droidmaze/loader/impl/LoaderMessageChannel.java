package edu.oc.droidmaze.loader.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.api.MessageChannel;
import edu.oc.droidmaze.loader.socket.Messenger;
import edu.oc.droidmaze.loader.util.Util;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class LoaderMessageChannel implements MessageChannel {

    private static final LoaderMessageChannel instance = new LoaderMessageChannel();

    private final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private Messenger messenger;

    @NotNull
    @Contract(pure = true)
    public static LoaderMessageChannel getInstance() {
        return instance;
    }

    @Override
    public void sendMessage(@NotNull final Droid toDroid, @NotNull final String data) {
        Util.checkProcess();
        executor.submit(() -> messenger.sendMessage(toDroid, data));
    }

    @Override
    public void registerListener(@NotNull BiConsumer<Droid, String> consumer) {
        Util.checkProcess();
        messenger.setConsumer(consumer);
    }

    @NotNull
    @Contract(pure = true)
    public ListeningExecutorService getExecutor() {
        return executor;
    }

    public void setMessenger(@NotNull Messenger messenger) {
        this.messenger = messenger;
    }
}
