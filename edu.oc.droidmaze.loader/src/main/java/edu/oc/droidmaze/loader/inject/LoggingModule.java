package edu.oc.droidmaze.loader.inject;

import com.google.inject.AbstractModule;
import edu.oc.droidmaze.api.Droid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingModule extends AbstractModule {

    private final Class<? extends Droid> droid;

    public LoggingModule(final Class<? extends Droid> droid) {
        this.droid = droid;
    }

    @Override
    protected void configure() {
        bind(Logger.class).toInstance(LogManager.getLogger(droid));
        bind(org.slf4j.Logger.class).toInstance(LoggerFactory.getLogger(droid));
        // java.util.logging.Logger is actually handled by default
    }
}
