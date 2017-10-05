module edu.oc.droidmaze.common {
    requires edu.oc.droidmaze.api;
    requires guava;
    requires java.logging;
    requires jline.reader;
    requires jline.terminal;
    requires log4j.api;
    requires log4j.iostreams;
    requires org.apache.commons.lang3;
    requires terminalconsoleappender;

    exports edu.oc.droidmaze.common to
        edu.oc.droidmaze.loader,
        edu.oc.droidmaze.server;

    exports edu.oc.droidmaze.common.terminal to
        edu.oc.droidmaze.loader,
        edu.oc.droidmaze.server;

    opens edu.oc.droidmaze.common to gson;
}
