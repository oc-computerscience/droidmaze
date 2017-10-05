package edu.oc.droidmaze.common.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

public class DroidTerminal {

    private static final Logger LOGGER = LogManager.getLogger(DroidTerminal.class);

    private static final Map<String, Command> commandMap = new ConcurrentHashMap<>();

    private static Runnable shutdownHook = null;

    public static void registerCommand(final String name, final Command command) {
        commandMap.putIfAbsent(name, command);
    }

    public static void registerShutdownHook(final Runnable hook) {
        shutdownHook = hook;
    }

    public static void start() {
        final Thread thread = new Thread(() -> {
            final Terminal terminal = TerminalConsoleAppender.getTerminal();
            if (terminal != null) {
                final LineReader reader = LineReaderBuilder.builder()
                    .appName("Droid Maze")
                    .terminal(terminal)
                    .build();

                TerminalConsoleAppender.setReader(reader);

                try {
                    String line;
                    while (true) {
                        try {
                            line = reader.readLine("> ");
                        } catch (final EndOfFileException ignored) {
                            // This is thrown when the user indicates end of input using CTRL + D
                            continue;
                        }

                        if (StringUtils.isBlank(line)) {
                            break;
                        }

                        handleInput(line);
                    }
                } catch (final UserInterruptException e)  {
                    // Called when CTRL + C happens
                    shutdownHook.run();
                }
            } else {
                // something is wrong with jline apparently
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        handleInput(line);
                    }
                } catch (final IOException e) {
                    LOGGER.error("Error reading console", e);
                }
            }
        }, "Droid Maze Console Thread");

        java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
        global.setUseParentHandlers(false);
        for (Handler handler : global.getHandlers()) {
            global.removeHandler(handler);
        }
        global.addHandler(new ForwardLogHandler());

        final Logger out = LogManager.getLogger("System.out");
        final Logger err = LogManager.getLogger("System.err");
        System.setOut(IoBuilder.forLogger(out).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(err).setLevel(Level.WARN).buildPrintStream());

        thread.setDaemon(true);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                TerminalConsoleAppender.close();
            } catch (final Throwable t) {
                System.err.println("Error occurred while shutting down TerminalConsoleAppender");
                t.printStackTrace();
            }
        }));
    }

    private static void handleInput(final String line) {
        final String word = line.split("\\s+")[0];
        final Command command = commandMap.get(word);
        if (command == null) {
            return;
        }

        try {
            command.workCommand(line);
        } catch (final Throwable t) {
            LOGGER.error("Error while processing command", t);
        }
    }
}
