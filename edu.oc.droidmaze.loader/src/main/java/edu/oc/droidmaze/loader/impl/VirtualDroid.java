package edu.oc.droidmaze.loader.impl;

import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.common.DroidData;
import edu.oc.droidmaze.loader.util.Util;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class VirtualDroid extends Droid {

    private final String name;

    @NotNull
    public static Droid parse(@NotNull final DroidData data) {
        return new VirtualDroid(data.getName(), data.getId());
    }

    private VirtualDroid(@NotNull final String name, @NotNull final UUID id) {
        super(id);
        this.name = name;
    }

    @NotNull
    @Override
    public String getName() {
        Util.checkProcess();
        return name;
    }
}
