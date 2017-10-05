package edu.oc.droidmaze.server

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import edu.oc.droidmaze.common.DroidData
import edu.oc.droidmaze.common.LoginResponseData
import edu.oc.droidmaze.server.config.Config
import edu.oc.droidmaze.server.socket.MessengerWebSocket
import edu.oc.droidmaze.server.socket.OverwatchWebSocket
import org.eclipse.jetty.http.HttpStatus
import spark.kotlin.before
import spark.kotlin.get
import spark.kotlin.halt
import spark.kotlin.port
import spark.kotlin.post

fun registerEndpoints(config: Config) {
    webSocket("/messenger", MessengerWebSocket::class.java)
    webSocket("/overwatch", OverwatchWebSocket::class.java)

    port(config.port)

    // Auth
    before {
        val path = request.pathInfo()
        if (!path.startsWith("/test") && !path.startsWith("/login") &&
            !path.startsWith("/overwatch") && !path.startsWith("/messenger")) {
            request.droidId ?: throw halt(HttpStatus.FORBIDDEN_403, "Valid token required")
        }

        response.type("application/json")
    }

    // No-auth endpoints
    get("/test") {
        gson.toJson(JsonObject().apply { add("testMode", JsonPrimitive(config.isTest)) })
    }

    post("/login") {
        val data = request.parse<DroidData>()
        LOGGER.info("Received login request from '{}' ({})", data.name, data.id)
        gson.toJson(LoginResponseData(droidMap.inverse().computeIfAbsent(data) {
            LOGGER.info("Generating token for '{}' ({})", data.name, data.id)
            generateToken()
        }))
    }

    // Auth endpoints
    get("/mazestate") {
        TODO()
    }

    post("/move") {
        TODO()
    }
}
