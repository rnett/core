package com.rnett.core

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.rnett.launchpad.Launchpad
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.BadResponseStatusException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import java.util.zip.ZipException
import kotlin.coroutines.CoroutineContext

class WebQuery(val concurrency: Int = 100,
               val queryTries: Int = 10,
               val concurrencyStart: Int = 100,
               val concurrencyStep: Int = 10,
               val timeoutMultiplier: Int = 100,
               val gzip: Boolean = true,
               val errorDelay: Int = 0,
               val errorDelayMultiplier: Int = 200,
               val output: Boolean = true,
               val coroutineContext: CoroutineContext = Dispatchers.Default,
               val requestConfig: HttpRequestBuilder.() -> Unit = {}
) {
    companion object {
        fun decompressGzip(compressed: ByteArray): String {
            try {
                val bis = ByteArrayInputStream(compressed)

                if (String(compressed).let { it.isBlank() || it == "[]" })
                    return "[]"

                val gis = GZIPInputStream(bis)
                val br = BufferedReader(InputStreamReader(gis, "UTF-8"))
                val sb = StringBuilder()
                var line: String?
                while (true) {
                    line = br.readLine()

                    if (line == null)
                        break

                    sb.append(line)
                }
                br.close()
                gis.close()
                bis.close()
                return sb.toString()
            } catch (e: ZipException) {
                return String(compressed)
            }
        }
    }

    val client
        get() =
            HttpClient(Apache) {
                engine {
                    connectTimeout *= timeoutMultiplier
                    connectionRequestTimeout *= timeoutMultiplier
                    socketTimeout *= timeoutMultiplier
                }
            }

    suspend fun makeQueries(urls: List<String>): List<String?> {
        val launchpad = Launchpad<String?>(concurrency, concurrencyStart, concurrencyStep, coroutineContext)

        return urls.map {
            launchpad {
                makeQuery(it)
            }
        }.awaitAll()
    }

    suspend inline fun <reified T : Any> makeJsonQueries(urls: List<String>, onNull: T): List<T> {
        val launchpad = Launchpad<T>(concurrency, concurrencyStart, concurrencyStep, coroutineContext)

        return urls.map {
            launchpad {
                makeJsonQuery(it, onNull)
            }
        }.awaitAll()
    }

    suspend inline fun <reified T : Any> makeJsonQueries(urls: List<String>): List<T?> {
        val launchpad = Launchpad<T?>(concurrency, concurrencyStart, concurrencyStep, coroutineContext)

        return urls.map {
            launchpad {
                makeJsonQuery(it)
            }
        }.awaitAll()
    }

    suspend inline fun <reified T : Any, I, R> makeQueries(inputs: List<Pair<I, String>>, crossinline transform: suspend (I, T?) -> R): List<R?> {
        val launchpad = Launchpad<R?>(concurrency, concurrencyStart, concurrencyStep, coroutineContext)

        return inputs.map { (key, url) ->
            launchpad {
                makeJsonQuery<T>(url).let { transform(key, it) }
            }
        }.awaitAll()
    }

    suspend inline fun <reified T : Any, I, R> makeQueries(inputs: Map<I, String>, crossinline transform: suspend (I, T?) -> R): List<R?> = makeQueries(inputs.toList(), transform)

    suspend inline fun <reified T : Any, I, R> makeQueries(inputs: List<I>, toUrl: (I) -> String, crossinline transform: suspend (I, T?) -> R): List<R?> =
            makeQueries(inputs.associateWith { toUrl(it) }.toList(), transform)

    suspend inline fun <reified T : Any> makeJsonQuery(url: String) =
            makeQuery(url)?.let { Gson().fromJson<T>(it) }

    suspend inline fun <reified T : Any> makeJsonQuery(url: String, onNull: T) =
            makeQuery(url)?.let { Gson().fromJson<T>(it) } ?: onNull

    suspend fun makeQuery(url: String): String? {
        var tries = 0
        var exception: Exception? = null

        while (tries < queryTries) {
            try {
                val json =
                        client.use { client ->
                            client.get<ByteArray>(url) {
                                requestConfig()
                                if (gzip)
                                    header("Accept-Encoding", "gzip")
                            }
                        }

                return if (gzip)
                    decompressGzip(json)
                else
                    String(json)


            } catch (e: Exception) {

                if (e is BadResponseStatusException) {
                    if (e.statusCode == HttpStatusCode.NotFound)
                        return null
                }

                exception = e
                delay(errorDelay + errorDelayMultiplier * tries.toLong())

                if (output)
                    println("Retries: ${tries + 1}")
            }
            tries++
        }

        if (output)
            println("Failed on \"$url\"")

        exception?.printStackTrace()
        if (exception != null)
            throw exception

        throw IllegalStateException("Errored somewhere weird")
    }
}