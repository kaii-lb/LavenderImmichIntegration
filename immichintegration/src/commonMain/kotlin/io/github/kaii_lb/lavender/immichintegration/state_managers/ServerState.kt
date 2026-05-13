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

class ServerState(apiClient: ApiClient) {
    private var serverClient = ServerClient(
        endpoint = "",
        auth = Auth.None,
        client = apiClient
    )

    fun setEndpoint(endpoint: String) {
        serverClient.setEndpoint(endpoint)
    }

    fun setAuth(auth: Auth) {
        serverClient.setAuth(auth)
    }

    suspend fun fetch(): ServerInfoState = withContext(Dispatchers.IO) {
        val online = serverClient.ping()
        val storage = serverClient.getStorage()
        val info = serverClient.getVersionInfo()
        val perUserStorage = serverClient.getUsagePerUser()

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
        serverClient.ping(address)
    }
}