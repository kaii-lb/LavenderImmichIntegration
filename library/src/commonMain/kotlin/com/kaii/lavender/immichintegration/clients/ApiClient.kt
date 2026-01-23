package com.kaii.lavender.immichintegration.clients

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.serialization.json.Json

class ApiClient() {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 15000
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        expectSuccess = true
    }
    private val log = KtorSimpleLogger("ApiClient")

    suspend fun post(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.post(
            url = url,
        ) {
            headers?.let {
                headers {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                }
            }

            if (body != null) setBody(body) 
            contentType(ContentType.Application.Json)
        }
    } catch (e: Throwable) {
        log.error(e.message)
        e.printStackTrace()
        null
    }

    suspend fun delete(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.delete(url = url) {
            headers?.let {
                headers {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                }
            }

            if (body != null) setBody(body) 
            contentType(ContentType.Application.Json)
        }
    } catch (e: Throwable) {
        log.error(e.message)
        e.printStackTrace()
        null
    }

    suspend fun put(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.put(
            url = url,
        ) {
            headers?.let {
                headers {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                }
            }

            if (body != null) setBody(body) 
            contentType(ContentType.Application.Json)
        }
    } catch (e: Throwable) {
        log.error(e.message)
        e.printStackTrace()
        null
    }

    suspend fun get(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.get(
            url = url
        ) {
            headers?.let {
                headers {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                }
            }

            if (body != null) setBody(body) 
            contentType(ContentType.Application.Json)
        }
    } catch (e: Throwable) {
        log.error(e.message)
        e.printStackTrace()
        null
    }

    suspend fun patch(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.patch(
            url = url,
        ) {
            headers?.let {
                headers {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                }
            }

            if (body != null) setBody(body) 
            contentType(ContentType.Application.Json)
        }
    } catch (e: Throwable) {
        log.error(e.message)
        e.printStackTrace()
        null
    }
}