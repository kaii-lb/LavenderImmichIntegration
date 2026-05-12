package io.github.kaii_lb.lavender.immichintegration.state_managers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.clients.AlbumsClient
import io.github.kaii_lb.lavender.immichintegration.clients.ApiClient
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumsGetAllState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllAlbumsState(
    private val coroutineScope: CoroutineScope,
    endpoint: String,
    auth: Auth,
    apiClient: ApiClient
) {
    private val albumClient =
        AlbumsClient(
            endpoint = endpoint,
            auth = auth,
            client = apiClient
        )

    private val _state = MutableStateFlow<AlbumsGetAllState>(AlbumsGetAllState.Failed)
    val state = _state.asStateFlow()

    fun load() = coroutineScope.launch(Dispatchers.IO) {
        _state.value = albumClient.getAll()
    }

    internal fun setEndpoint(endpoint: String) {
        albumClient.setEndpoint(endpoint)
    }

    internal fun setAuth(auth: Auth) {
        albumClient.setAuth(auth)
    }
}

@Composable
fun rememberAllAlbumsState(endpoint: String, auth: Auth): AllAlbumsState {
    val apiClient = LocalApiClient.current
    val coroutineScope = rememberCoroutineScope()

    val state = remember {
        AllAlbumsState(
            endpoint = endpoint,
            auth = auth,
            coroutineScope = coroutineScope,
            apiClient = apiClient
        )
    }

    LaunchedEffect(endpoint, auth) {
        state.setEndpoint(endpoint)
        state.setAuth(auth)
    }

    return state
}