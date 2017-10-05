package edu.oc.droidmaze.server.socket

import org.apache.logging.log4j.LogManager
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.StatusCode
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket

@WebSocket(maxTextMessageSize = 1024 * 1024)
class OverwatchWebSocket {

    private val sessions = HashSet<Session>()

    init {
        INSTANCE = this
    }

    @OnWebSocketConnect
    fun connect(session: Session) {
        LOGGER.info("Connection opened: {}", session.remote.inetSocketAddress)
        sessions.add(session)
    }

    @OnWebSocketClose
    fun close(session: Session, statusCode: Int, reason: String) {
        sessions.remove(session)
    }

    @OnWebSocketMessage
    fun message(session: Session, text: String) {
        // noop
    }

    fun broadcastMessage(text: String) = sessions.forEach { it.remote?.sendString(text) }

    fun kill() {
        try {
            sessions.forEach { it.close(StatusCode.SHUTDOWN, "Server shutting down") }
            sessions.clear()
        } catch (t: Throwable) {
            LOGGER.error("Error occurred while shutting down Overwatch", t)
        }
    }

    companion object {
        private val LOGGER = LogManager.getLogger(OverwatchWebSocket::class.java)!!

        lateinit var INSTANCE: OverwatchWebSocket
            private set
    }
}
