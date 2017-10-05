module edu.oc.droidmaze.loader {
    requires edu.oc.droidmaze.api;
    requires edu.oc.droidmaze.common;
    requires annotations;
    requires commons.io;
    requires gson;
    requires guava;
    requires guice;
    requires java.sql;
    requires log4j.api;
    requires slf4j.api;
    requires unirest.java;
    requires websocket.api;
    requires websocket.client;
    requires terminalconsoleappender;

    opens edu.oc.droidmaze.loader.config to gson;
    opens edu.oc.droidmaze.loader.socket to websocket.common;

    uses edu.oc.droidmaze.api.Droid;

    provides edu.oc.droidmaze.api.impl.ServerProvider with edu.oc.droidmaze.loader.impl.LoaderServerProvider;
}
