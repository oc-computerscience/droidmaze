module edu.oc.droidmaze.server {
    requires edu.oc.droidmaze.api;
    requires edu.oc.droidmaze.common;
    requires commons.compress;
    requires gson;
    requires guava;
    requires java.desktop;
    requires java.sql;
    requires jetty.http;
    requires kotlin.stdlib;
    requires log4j.api;
    requires log4j.iostreams;
    requires spark.core;
    requires spark.kotlin;
    requires terminalconsoleappender;
    requires websocket.api;

    opens edu.oc.droidmaze.server to gson;
    opens edu.oc.droidmaze.server.config to gson;
    opens edu.oc.droidmaze.server.socket to
        spark.core,
        websocket.common;
}
