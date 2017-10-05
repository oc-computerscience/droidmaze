package edu.oc.droidmaze.loader.impl;

import edu.oc.droidmaze.api.Server;
import edu.oc.droidmaze.api.impl.ServerProvider;

public class LoaderServerProvider implements ServerProvider {
    @Override
    public Server getServer() {
        return LoaderServer.getInstance();
    }
}
