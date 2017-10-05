package edu.oc.droidmaze.loader.inject;

import com.google.inject.AbstractModule;
import edu.oc.droidmaze.api.MessageChannel;
import edu.oc.droidmaze.api.Server;
import edu.oc.droidmaze.loader.impl.LoaderMessageChannel;
import edu.oc.droidmaze.loader.impl.LoaderServer;

public class ApiModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Server.class).toInstance(LoaderServer.getInstance());
        bind(MessageChannel.class).toInstance(LoaderMessageChannel.getInstance());
    }
}
