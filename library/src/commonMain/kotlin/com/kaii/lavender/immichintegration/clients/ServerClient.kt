package com.kaii.lavender.immichintegration.clients

import com.kaii.lavender.immichintegration.serialization.ServerInfo
import com.kaii.lavender.immichintegration.serialization.ServerPing
import com.kaii.lavender.immichintegration.serialization.ServerStorage
import io.ktor.client.call.body
import io.ktor.http.HttpHeaders
import io.ktor.http.Url

internal class ServerClient(
    private val baseUrl: String,
    private val client: ApiClient
) {
    suspend fun getStorage(accessToken: String): ServerStorage? =
        client.get(
            url = Url("$baseUrl/api/server/storage"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<ServerStorage>()

    suspend fun ping(
        address: String? = null
    ): Boolean =
        client.get(
            url = Url("${address ?: baseUrl}/api/server/ping"),
            headers = null,
            body = null
        )?.body<ServerPing>()?.response == "pong"

    suspend fun getVersionInfo(accessToken: String): ServerInfo? =
        client.get(
            url = Url("$baseUrl/api/server/about"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<ServerInfo>()
}