package edu.oc.droidmaze.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import spark.Request
import spark.Spark
import java.io.File
import java.io.InputStream
import java.util.Random

val gson = Gson()

// Use TypeToken so we can support any arbitrary type
inline fun <reified T> Gson.fromJson(file: File): T = this.fromJson<T>(file.readText(), object : TypeToken<T>() {}.type)
inline fun <reified T> Gson.fromJson(stream: InputStream): T = this.fromJson<T>(stream.reader(), object : TypeToken<T>() {}.type)
inline fun <reified T> Gson.fromJson(text: String): T = this.fromJson<T>(text, object : TypeToken<T>() {}.type)

fun webSocket(path: String, type: Class<*>) = Spark.webSocket(path, type)

val Request.droidId
    get() = droidMap[this.queryParams("token")]

inline fun <reified T> Request.parse(): T = gson.fromJson(this.body())

private const val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-"
private const val size = 64
private val rand = Random()
fun generateToken() = buildString(size) {
    for (i in 1..size) {
        append(alphabet[rand.nextInt(alphabet.length)])
    }
}
