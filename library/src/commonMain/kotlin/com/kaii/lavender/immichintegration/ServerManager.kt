package com.kaii.lavender.immichintegration

import com.kaii.lavender.immichintegration.serialization.ServerInfo
import com.kaii.lavender.immichintegration.serialization.ServerStorage
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.serialization.json.Json

@Suppress("unused")
class ServerManager(
    private val apiClient: ApiClient,
    private val endpointBase: String,
    private val bearerToken: String
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getAboutInfo(): ServerInfo? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/server/about"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Any,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.bodyAsText()?.let { json.decodeFromString<ServerInfo>(it) }
    }

    suspend fun getStorage(): ServerStorage? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/server/storage"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Any,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<ServerStorage>()
    }
}