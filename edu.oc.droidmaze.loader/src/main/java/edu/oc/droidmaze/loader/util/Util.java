package edu.oc.droidmaze.loader.util;

import com.google.gson.Gson;
import edu.oc.droidmaze.loader.ProcessCancelledException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Util {
    private Util() {}

    public static boolean CHECK_INTERRUPT = true;

    private static final AtomicLong time = new AtomicLong(0L);
    private static final AtomicBoolean interrupt = new AtomicBoolean(false);

    private static final Gson gson = new Gson();

    @NotNull
    @Contract(pure = true)
    public static AtomicLong getTime() {
        return time;
    }

    public static void setInterrupt() {
        interrupt.set(true);
    }

    public static void resetInterrupt() {
        interrupt.set(false);
    }

    public static void checkProcess() {
        if (CHECK_INTERRUPT && interrupt.compareAndSet(true, false)) {
            throw new ProcessCancelledException("Time elapsed: " + time.getAndSet(0L));
        }
    }

    @NotNull
    public static Gson gson() {
        return gson;
    }

    public static RuntimeException sneakyThrow(final Throwable t) {
        //noinspection RedundantTypeArguments
        throw Util.<RuntimeException>superSneakyThrow(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T superSneakyThrow(final Throwable t) throws T {
        throw (T) t;
    }
}
