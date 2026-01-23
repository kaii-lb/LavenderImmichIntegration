package com.kaii.lavender.immichintegration.state_managers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kaii.lavender.immichintegration.clients.AlbumsClient
import com.kaii.lavender.immichintegration.clients.ApiClient
import com.kaii.lavender.immichintegration.serialization.albums.AlbumsGetAllState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllAlbumsState(
    private val baseUrl: String,
    private val coroutineScope: CoroutineScope,
    apiClient: ApiClient
) {
    private val albumClient =
        AlbumsClient(
            baseUrl = baseUrl,
            client = apiClient
        )

    private val _state = MutableStateFlow<AlbumsGetAllState>(AlbumsGetAllState.Failed)
    val state = _state.asStateFlow()

    fun load(accessToken: String) = coroutineScope.launch(Dispatchers.IO) {
        _state.value = albumClient.getAll(accessToken)
    }
}

@Composable
fun rememberAllAlbumsState(baseUrl: String): AllAlbumsState {
    val apiClient = LocalApiClient.current
    val coroutineScope = rememberCoroutineScope()


    return remember(baseUrl) {
        AllAlbumsState(
            baseUrl = baseUrl,
            coroutineScope = coroutineScope,
            apiClient = apiClient
        )
    }
}