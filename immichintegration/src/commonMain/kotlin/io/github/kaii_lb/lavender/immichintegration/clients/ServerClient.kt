package io.github.kaii_lb.lavender.immichintegration.clients

import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.serialization.ServerInfo
import io.github.kaii_lb.lavender.immichintegration.serialization.ServerPing
import io.github.kaii_lb.lavender.immichintegration.serialization.ServerStatistics
import io.github.kaii_lb.lavender.immichintegration.serialization.ServerStorage
import io.ktor.client.call.body
import io.ktor.http.Url

internal class ServerClient(
    private val client: ApiClient,
    endpoint: String,
    auth: Auth
) : BaseClient(endpoint, auth) {
    suspend fun getStorage(): ServerStorage? {
        if (endpoint.isBlank() || auth.asString().isBlank()) return null

        return client.get(
            url = Url("$endpoint/api/server/storage"),
            headers = auth.headers,
            body = null
        )?.body<ServerStorage>()
    }

    suspend fun ping(
        address: String? = null
    ): Boolean {
        if (endpoint.isBlank()) return false

        return client.get(
            url = Url("${address ?: endpoint}/api/server/ping"),
            headers = null,
            body = null
        )?.body<ServerPing>()?.response == "pong"
    }

    suspend fun getVersionInfo(): ServerInfo? {
        if (endpoint.isBlank() || auth.asString().isBlank()) return null

        return client.get(
            url = Url("$endpoint/api/server/about"),
            headers = auth.headers,
            body = null
        )?.body()
    }

    suspend fun getUsagePerUser(): ServerStatistics? {
        if (endpoint.isBlank() || auth.asString().isBlank()) return null

        return client.get(
            url = Url("$endpoint/api/server/statistics"),
            headers = auth.headers,
            body = null
        )?.body()
    }
}