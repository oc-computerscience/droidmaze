package edu.oc.droidmaze.api.impl;

import java.util.ServiceLoader;

public class ServerProviderFactory {

    private static final Object lock = new Object();

    private static ServerProvider provider = null;

    public static ServerProvider getProvider() {
        if (provider != null) {
            return provider;
        }

        synchronized (lock) {
            if (provider != null) {
                return provider;
            }

            final ServiceLoader<ServerProvider> load = ServiceLoader.load(ServerProvider.class);
            if (load.stream().count() > 1) {
                throw new IllegalStateException("More than one implementation for " + ServerProvider.class.getName() + " found.");
            }
            provider = load
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find implementation for " +
                                                                    ServerProvider.class.getName() +
                                                                    ". Did you start this from the loader?"));
        }

        return provider;
    }
}
