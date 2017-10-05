package edu.oc.droidmaze.server.config

data class Config(
    val isTest: Boolean = false,
    val port: Int,
    val maze: MazeConfig
)

data class MazeConfig(
    val width: Int,
    val height: Int,
    val levels: Int
)
