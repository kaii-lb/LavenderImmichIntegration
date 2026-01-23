package com.kaii.lavender.immichintegration.state_managers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kaii.lavender.immichintegration.clients.ApiClient
import com.kaii.lavender.immichintegration.clients.ServerClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface ServerInfoState {
    object Unavailable : ServerInfoState

    data class Available(
        val version: String,
        val build: String?,
        val online: Boolean,
        val diskSize: String,
        val diskUsed: String,
        val diskUsedPercentage: Float
    ) : ServerInfoState
}

class ServerState(
    private val baseUrl: String,
    private val coroutineScope: CoroutineScope,
    apiClient: ApiClient
) {
    private val serverClient =
        ServerClient(
            baseUrl = baseUrl,
            client = apiClient
        )

    private val _state = MutableStateFlow<ServerInfoState>(ServerInfoState.Unavailable)
    val state = _state.asStateFlow()

    fun fetch(apiKey: String) = coroutineScope.launch(Dispatchers.IO) {
        if (baseUrl.isBlank()) return@launch

        val online = serverClient.ping(apiKey)
        val storage = serverClient.getStorage(apiKey)
        val info = serverClient.getVersionInfo(apiKey)

        if (storage == null || info == null) {
            _state.value = ServerInfoState.Unavailable
            return@launch
        }

        _state.value = ServerInfoState.Available(
            version = info.version,
            build = info.build,
            online = online,
            diskSize = storage.diskSize,
            diskUsed = storage.diskUse,
            diskUsedPercentage = storage.diskUsagePercentage / 100f
        )
    }
}

@Composable
fun rememberServerState(baseUrl: String): ServerState {
    val apiClient = LocalApiClient.current
    val coroutineScope = rememberCoroutineScope()

    return remember(baseUrl) {
        ServerState(
            baseUrl = baseUrl,
            coroutineScope = coroutineScope,
            apiClient = apiClient
        )
    }
}