package edu.oc.droidmaze.server

import com.google.common.collect.HashBiMap
import edu.oc.droidmaze.common.DroidData
import edu.oc.droidmaze.common.terminal.DroidTerminal
import edu.oc.droidmaze.server.config.Config
import edu.oc.droidmaze.server.socket.MessengerWebSocket
import edu.oc.droidmaze.server.socket.OverwatchWebSocket
import net.minecrell.terminalconsole.TerminalConsoleAppender
import org.apache.logging.log4j.LogManager
import spark.Spark
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO
import kotlin.system.exitProcess

val LOGGER = LogManager.getLogger("Main Server")!!

val droidMap = HashBiMap.create<String, DroidData>()!!

val ready = AtomicBoolean(false)

fun main(args: Array<String>) {
    registerCommands()
    DroidTerminal.start()

    val config = when {
        args.isEmpty() -> gson.fromJson(File("config.json"))
        args[0] == "internal" -> ClassLoader.getSystemClassLoader().getResourceAsStream("config.json").use {
            gson.fromJson<Config>(it)
        }
        else -> gson.fromJson(File(args[0]))
    }

    registerEndpoints(config)

    startup(config)
}

private fun registerCommands() {
    DroidTerminal.registerCommand("start") {
        startGame()
    }

    DroidTerminal.registerCommand("stop") {
        shutdown()
    }

    DroidTerminal.registerShutdownHook(::shutdown)
}

fun startup(config: Config) {
    LOGGER.info("Path registration complete, generating maze and waiting for droids to connect")

    val maze = MazeGenerator.generateMaze(config)
    Drawing.draw(maze)
    maze.write(File("maze.dmz"))

    LOGGER.info("Maze generation complete")
    OverwatchWebSocket.INSTANCE.broadcastMessage("ready")
}

fun shutdown() {
    TerminalConsoleAppender.setReader(null)
    LOGGER.warn("Shutting down the server")
    OverwatchWebSocket.INSTANCE.kill()
    MessengerWebSocket.INSTANCE.kill()
    Server.kill()
    try {
        Spark.stop()
    } catch (t: Throwable) {
        LOGGER.error("Error occurred while shutting down Spark", t)
    }
    Thread({
        // Give jetty enough time to shut down cleanly
        try {
            Thread.sleep(50)
        } catch (ignored: Throwable) {}
        exitProcess(0)
    }).start()
}

fun startGame() {
    if (!ready.get()) {
        LOGGER.warn("The server has not completed initial startup yet, please wait")
        return
    }

    Server.start()
}

object Drawing {
    private const val FACTOR = 4
    private const val MARGIN = 2

    private var x = FACTOR / 2
    private var y = MARGIN

    private lateinit var graphics: Graphics2D

    fun draw(maze: Maze) {
        val image = BufferedImage((MARGIN * 2) + FACTOR * maze.width,
                                  MARGIN + (FACTOR * (maze.depth * maze.height)) + (MARGIN * maze.depth), BufferedImage.TYPE_INT_RGB)
        graphics = image.createGraphics()

        drawLevel(maze.slots)

        file.delete()
        ImageIO.write(image, "png", file)
    }

    private fun drawLevel(list: List<List<List<Slot>>>) {
        for (level in list) {
            for (row in level) {
                for (slot in row) {
                    if (slot.isWall) {
                        draw(State.WALL)
                    } else {
                        when {
                            slot.up != null && slot.down != null -> draw(State.BOTH)
                            slot.up != null -> draw(State.UP)
                            slot.down != null -> draw(State.DOWN)
                            else -> draw(State.NORMAL)
                        }
                    }
                }
                newLine()
            }
            newLevel(list.first().first().size)
        }
    }

    private val file = File("maze.png")

    private enum class State {
        WALL, NORMAL, UP, DOWN, BOTH
    }

    private fun draw(state: State) {
        graphics.color = when (state) {
            State.WALL -> Color.GRAY
            State.NORMAL -> Color.WHITE
            State.UP, State.BOTH -> Color.GREEN
            State.DOWN -> Color.BLUE
        }
        if (state == State.BOTH) {
            graphics.fill(Rectangle(x, y, FACTOR, FACTOR / 2))
            graphics.color = Color.BLUE
            graphics.fill(Rectangle(x, y + FACTOR / 2, FACTOR, FACTOR / 2))
        } else {
            graphics.fill(Rectangle(x, y, FACTOR, FACTOR))
        }
        x += FACTOR
    }

    private fun newLine() {
        x = MARGIN
        y += FACTOR
    }

    private fun newLevel(width: Int) {
        graphics.color = Color.CYAN
        graphics.fill(Rectangle(x, y, width * FACTOR, 2))
        y += MARGIN
    }
}

