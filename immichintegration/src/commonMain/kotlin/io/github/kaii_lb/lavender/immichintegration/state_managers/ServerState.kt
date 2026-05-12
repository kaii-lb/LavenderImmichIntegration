package io.github.kaii_lb.lavender.immichintegration.state_managers

import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.clients.ApiClient
import io.github.kaii_lb.lavender.immichintegration.clients.ServerClient
import io.github.kaii_lb.lavender.immichintegration.serialization.UsageByUserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface ServerInfoState {
    object Unavailable : ServerInfoState

    data class Available(
        val version: String,
        val build: String?,
        val online: Boolean,
        val diskSize: String,
        val diskUsed: String,
        val diskUsedPercentage: Float,
        val perUserStorage: List<UsageByUserDto>
    ) : ServerInfoState
}

class ServerState {
    private var serverClient: ServerClient? = null

    fun setEndpoint(endpoint: String, apiClient: ApiClient) {
        if (serverClient?.getEndpoint() == endpoint) return

        serverClient = ServerClient(
            endpoint = endpoint,
            auth = serverClient?.getAuth() ?: Auth.None,
            client = apiClient
        )
    }

    suspend fun fetch(): ServerInfoState = withContext(Dispatchers.IO) {
        if (serverClient == null) return@withContext ServerInfoState.Unavailable

        val online = serverClient!!.ping()
        val storage = serverClient!!.getStorage()
        val info = serverClient!!.getVersionInfo()
        val perUserStorage = serverClient!!.getUsagePerUser()

        if (storage == null || info == null || perUserStorage == null) {
            return@withContext ServerInfoState.Unavailable
        }

        return@withContext ServerInfoState.Available(
            version = info.version,
            build = info.build,
            online = online,
            diskSize = storage.diskSize,
            diskUsed = storage.diskUse,
            diskUsedPercentage = storage.diskUsagePercentage / 100f,
            perUserStorage = perUserStorage.usageByUser
        )
    }

    suspend fun ping(
        address: String? = null
    ) = withContext(Dispatchers.IO) {
        if (serverClient == null) return@withContext false

        serverClient!!.ping(address)
    }
}