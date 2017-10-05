package edu.oc.droidmaze.loader;

import com.google.common.io.Closeables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.api.HandleFailure;
import edu.oc.droidmaze.api.HandleTurn;
import edu.oc.droidmaze.api.InitDroid;
import edu.oc.droidmaze.api.MoveFailure;
import edu.oc.droidmaze.api.ShutdownDroid;
import edu.oc.droidmaze.loader.config.Config;
import edu.oc.droidmaze.loader.config.DroidConfig;
import edu.oc.droidmaze.loader.impl.LoaderServer;
import edu.oc.droidmaze.loader.inject.ApiModule;
import edu.oc.droidmaze.loader.inject.LoggingModule;
import edu.oc.droidmaze.loader.socket.Messenger;
import edu.oc.droidmaze.loader.socket.Overwatch;
import edu.oc.droidmaze.loader.util.Util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Bootstrap {

    private static final Logger LOGGER = LogManager.getLogger(Bootstrap.class);

    private static final ArrayList<Method> shutdownMethods = new ArrayList<>();
    private static final ArrayList<Method> initMethods = new ArrayList<>();
    private static final ArrayList<Method> execMethods = new ArrayList<>();
    private static final ArrayList<Method> failureMethods = new ArrayList<>();

    public static void init(final String[] args) throws FileNotFoundException {
        LOGGER.info("Configuring loader");
        // Find the config file
        InputStream configStream = null;
        final Config config;
        try {
            if (args.length == 0) {
                // Default to standard name
                configStream = new FileInputStream(new File("config.json"));
            } else if ("internal".equals(args[0])) {
                configStream = Bootstrap.class.getResourceAsStream("/config.json");
            } else {
                // They can specify their own path to the config with a command line argument
                configStream = new FileInputStream(new File(args[0]));
            }

            // Read the config file
            try (final InputStreamReader reader = new InputStreamReader(configStream)) {
                config = Util.gson().fromJson(reader, Config.class);
            } catch (final IOException e) {
                LOGGER.error("Error reading config file", e);
                System.exit(1);
                return;
            }
        } finally {
            if (configStream != null) {
                Closeables.closeQuietly(configStream);
            }
        }

        // Create ExternalServer singleton, this will be the channel which talks to the server
        final ExternalServer server = new ExternalServer(config.getServerUrl());

        final Droid droid = loadDroid();
        assert droid != null;

        server.registerFailureHandlerMethods(failureMethods);
        failureMethods.clear();

        LoaderServer.getInstance().setUserDroid(droid);

        LOGGER.info("Logging into server");
        // Log into the server
        ExternalServer.getInstance().init();
        LOGGER.info("Logged into server successfully");

        final Messenger messenger = new Messenger(config.getServerUrl());
        final TurnExecutor executor = new TurnExecutor(execMethods);
        execMethods.clear();
        final Overwatch overwatch = new Overwatch(config.getServerUrl(), executor);
        ExternalServer.getInstance().setOverwatch(overwatch);
        ExternalServer.getInstance().setMessenger(messenger);

        final Thread overwatchExecutor = new Thread(overwatch, "Overwatch");
        final Thread messengerExecutor = new Thread(messenger, "Messenger");

        overwatchExecutor.start();
        messengerExecutor.start();

        // Wait for the server to inform us the game is about to start
        LOGGER.info("Waiting for server to start game");
        while (!overwatch.hasStarted()) {
            Thread.onSpinWait();
        }

        LOGGER.info("Game starting, initializing droid");
        // This is when Droid.init() will be called
        LoaderServer.getInstance().acceptDroid(droid, initMethods);
        initMethods.clear();

        LOGGER.info("Droid initialized, waiting for server to start first turn");
        // Start maze executor thread
        final Thread mazeExecutor = new Thread(executor, "Maze Executor");
        mazeExecutor.start();

        try {
            overwatchExecutor.join();
            mazeExecutor.join();
            messengerExecutor.join();
        } catch (final InterruptedException ignored) {}

        for (Method method : shutdownMethods) {
            try {
                method.invoke(droid);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw Util.sneakyThrow(e.getCause());
            }
        }
    }

    private static String extractZip(final File droidArchive, final File outDir) {
        final ZipFile zipFile;
        try {
            zipFile = new ZipFile(droidArchive);
        } catch (final IOException e) {
            LOGGER.error("Unable to read zip file " + droidArchive.getAbsolutePath(), e);
            System.exit(1);
            return null;
        }
        try (final ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(droidArchive)))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                final File newFile = new File(outDir, entry.getName());
                FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry), newFile);
            }
        } catch (final IOException e) {
            LOGGER.error("Unable to extract droid", e);
            System.exit(1);
            return null;
        }

        try {
            final String droidConfigText = FileUtils.readFileToString(new File(outDir, "config.json"), StandardCharsets.UTF_8);
            final DroidConfig droidConfig = Util.gson().fromJson(droidConfigText, DroidConfig.class);
            return droidConfig.getModuleName();
        } catch (final IOException e) {
            LOGGER.error("Error reading droid config file", e);
            System.exit(1);
            return null;
        }
    }

    private static Droid loadDroid() {
        LOGGER.info("Loading droid");
        final File droidDir = new File("droid");
        final File[] files = droidDir.listFiles((dir, name) -> name.endsWith(".droid"));
        if (files == null || files.length == 0) {
            LOGGER.error("No droid files founds in " + droidDir.getAbsolutePath());
            System.exit(1);
            return null;
        }
        final File droidArchive = files[0];
        LOGGER.info("Found droid: " + droidArchive.getAbsolutePath());
        if (files.length > 1) {
            LOGGER.warn("Found multiple droid files, only loading " + droidArchive.getAbsolutePath());
        }

        final File outDir = new File(droidDir, droidArchive.getName().substring(0, droidArchive.getName().length() - 6));
        if (outDir.exists()) {
            LOGGER.info("Droid output directory '" + outDir.getAbsolutePath() + "' exists, deleting");
            try {
                FileUtils.deleteDirectory(outDir);
            } catch (final IOException e) {
                LOGGER.error("Unable to delete output directory", e);
                System.exit(1);
            }
        }

        final String moduleName = extractZip(droidArchive, outDir);
        assert moduleName != null;

        final File[] outputFiles = outDir.listFiles();
        if (outputFiles == null || outputFiles.length == 0) {
            LOGGER.error("No modules found in droid file", new IOException());
            System.exit(1);
            return null;
        }

        final ModuleLayer parentLayer = Bootstrap.class.getModule().getLayer();
        final Configuration conf = parentLayer
            .configuration()
            .resolveAndBind(
                ModuleFinder.of(outDir.toPath()),
                ModuleFinder.of(),
                Set.of(moduleName)
            );

        final ModuleLayer layer;
        layer = parentLayer.defineModulesWithOneLoader(
            conf,
            new URLClassLoader("Droid Loader",
                               Arrays.stream(outputFiles).map(f -> {
                                   try {
                                       return f.toURI().toURL();
                                   } catch (final MalformedURLException e) {
                                       LOGGER.error("Unable to create Droid Loader", e);
                                       System.exit(1);
                                       return null;
                                   }
                               }).toArray(URL[]::new),
                               Bootstrap.class.getClassLoader()
            )
        );


        final Droid droid = ServiceLoader.load(layer, Droid.class).findFirst().orElse(null);
        if (droid == null) {
            LOGGER.error("Could not find droid");
            System.exit(1);
            return null;
        }

        // Inject deps
        final Injector injector = Guice.createInjector(new LoggingModule(droid.getClass()), new ApiModule());
        injector.injectMembers(droid);

        checkDroidClass(droid);

        LOGGER.info("Droid (" + droid.getClass().getName() + ") loaded successfully");
        return droid;
    }

    @SuppressWarnings("unchecked")
    private static void checkDroidClass(final Droid droid) {
        LOGGER.info("Checking validity of " + droid.getClass().getName());
        final Class<? extends Droid> clazz = droid.getClass();
        boolean goodClass = false;

        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            final HandleTurn turnAnnotation = method.getAnnotation(HandleTurn.class);
            final HandleFailure failureAnnotation = method.getAnnotation(HandleFailure.class);
            final InitDroid initAnnotation = method.getAnnotation(InitDroid.class);
            final ShutdownDroid shutdownAnnotation = method.getAnnotation(ShutdownDroid.class);

            final int length = method.getParameters().length;
            final Class<?>[] types = method.getParameterTypes();

            // Required handle turn method
            if (turnAnnotation != null) {
                if (length != 0) {
                    throw new RuntimeException("All classes implementing " + Droid.class.getName() +
                                                   " must have at least one no-parameter method annotated with " +
                                                   HandleTurn.class.getName());
                }

                execMethods.add(method);
                goodClass = true;
            }

            // Optional handle failure method
            if (failureAnnotation != null) {
                switch (length) {
                    case 0:
                        break;
                    case 1:
                        if (!types[0].isAssignableFrom(MoveFailure.class)) {
                            throw new RuntimeException("All classes implementing " +
                                                           Droid.class.getName() + " with a method annotated with " +
                                                           HandleFailure.class.getName() +
                                                           " must either have only one parameter of type " +
                                                           MoveFailure.class.getName() + " or no parameters.");
                        }
                        break;
                    default:
                        throw new RuntimeException("All classes implementing " + Droid.class.getName() +
                                                       " with a method annotated with " + HandleFailure.class.getName() +
                                                       " must either have only one parameter of type " +
                                                       MoveFailure.class.getName() + " or no parameters.");
                }

                failureMethods.add(method);
            }

            // Optional init method
            if (initAnnotation != null) {
                if (length != 0) {
                    throw new RuntimeException("All classes implementing " + Droid.class.getName() +
                                                   " with a method annotated with " + InitDroid.class.getName() +
                                                   " must have no parameters.");
                }

                initMethods.add(method);
            }

            // Optional shutdown method
            if (shutdownAnnotation != null) {
                if (length != 0) {
                    throw new RuntimeException("All classes implementing " + Droid.class.getName() +
                                                   " with a method annotated with " + InitDroid.class.getName() +
                                                   " must have no parameters.");
                }

                shutdownMethods.add(method);
            }
        }

        if (!goodClass) {
            throw new RuntimeException("All classes implementing " + Droid.class.getName() +
                                           " must have at least one no-parameter method annotated with " +
                                           HandleTurn.class.getName() + ".");
        }

        LOGGER.info(droid.getClass().getName() + " is valid");
    }

    public static void shutdown() {
        TerminalConsoleAppender.setReader(null);
        LOGGER.warn("Shutting down loader");
        ExternalServer.getInstance().getMessenger().kill();
        ExternalServer.getInstance().getOverwatch().kill();
        System.exit(0);
    }
}
