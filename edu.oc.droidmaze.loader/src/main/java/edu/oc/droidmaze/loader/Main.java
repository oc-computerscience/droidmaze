package edu.oc.droidmaze.loader;

import edu.oc.droidmaze.common.terminal.DroidTerminal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(final String[] args) {
        DroidTerminal.start();

        DroidTerminal.registerCommand("stop", s -> Bootstrap.shutdown());
        DroidTerminal.registerShutdownHook(Bootstrap::shutdown);

        try {
            Bootstrap.init(args);
        } catch (final Throwable e) {
            LOGGER.error("Error attempting to run", e);
        }
    }
}
