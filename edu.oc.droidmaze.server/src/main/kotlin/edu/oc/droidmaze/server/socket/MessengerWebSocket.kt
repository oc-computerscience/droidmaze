package edu.oc.droidmaze.server.socket

import com.google.common.collect.HashBiMap
import edu.oc.droidmaze.server.droidMap
import org.apache.logging.log4j.LogManager
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.StatusCode
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import java.util.UUID

@WebSocket(maxTextMessageSize = 1024 * 1024)
class MessengerWebSocket {

    init {
        INSTANCE = this
    }

    private val sessionMap = HashBiMap.create<Session, UUID>()

    @OnWebSocketConnect
    fun connect(session: Session) {
        LOGGER.info("Connection opened: {}", session.remote.inetSocketAddress)
    }

    @OnWebSocketClose
    fun close(session: Session, statusCode: Int, reason: String) {
        val uuid = sessionMap[session] ?: return
        LOGGER.info("Connection closed: {}, {} - {}", uuid, statusCode, reason)
        sessionMap.remove(session)
    }

    @OnWebSocketMessage
    fun message(session: Session, text: String) {
        if (!sessionMap.contains(session)) {
            val droid = droidMap[text] ?: return session.close(StatusCode.POLICY_VIOLATION, "Not a valid login UUID")
            sessionMap.put(session, droid.id)
            LOGGER.info("Registered '{}' ({}) to session '{}'", droid.name, droid.id, session.remote.inetSocketAddress)
        }
    }

    fun sendMessage(uuid: UUID, text: String) = sessionMap.inverse()[uuid]?.remote?.sendStringByFuture(text)

    fun kill() {
        try {
            sessionMap.forEach { k, _ -> k.close(StatusCode.SHUTDOWN, "Server shutting down") }
            sessionMap.clear()
        } catch (t: Throwable) {
            LOGGER.error("Error occurred while shutting down Messenger", t)
        }
    }

    companion object {
        private val LOGGER = LogManager.getLogger(MessengerWebSocket::class.java)!!

        lateinit var INSTANCE: MessengerWebSocket
            private set
    }
}
