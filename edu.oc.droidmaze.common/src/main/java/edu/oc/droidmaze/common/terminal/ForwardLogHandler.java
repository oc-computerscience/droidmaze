package edu.oc.droidmaze.common.terminal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForwardLogHandler extends ConsoleHandler {

    private Map<String, Logger> cachedLoggers = new ConcurrentHashMap<>();

    private Logger getLogger(final String name) {
        Logger logger = cachedLoggers.get(name);
        if (logger == null) {
            logger = LogManager.getLogger(name);
            cachedLoggers.put(name, logger);
        }

        return logger;
    }

    @Override
    public void publish(final LogRecord record) {
        final Logger logger = getLogger(String.valueOf(record.getLoggerName()));
        final Throwable thrown = record.getThrown();
        final Level level = record.getLevel();
        final String message = record.getMessage();

        if (level == Level.SEVERE) {
            logger.error(message, thrown);
        } else if (level == Level.WARNING) {
            logger.warn(message, thrown);
        } else if (level == Level.INFO) {
            logger.info(message, thrown);
        } else if (level == Level.CONFIG) {
            logger.debug(message, thrown);
        } else {
            logger.trace(message, thrown);
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}
