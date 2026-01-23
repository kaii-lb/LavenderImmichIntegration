package com.kaii.lavender.immichintegration.state_managers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kaii.lavender.immichintegration.clients.ApiClient
import com.kaii.lavender.immichintegration.clients.LoginClient
import com.kaii.lavender.immichintegration.serialization.LoginStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface LoginState {
    object LoggedOut : LoginState

    data class LoggedIn(
        val pfpUrl: String,
        val name: String,
        val accessToken: String,
        val email: String
    ) : LoginState
}

class LoginStateManager(
    private val baseUrl: String,
    private val coroutineScope: CoroutineScope,
    apiClient: ApiClient
) {
    private val loginClient =
        LoginClient(
            baseUrl = baseUrl,
            client = apiClient
        )

    private val _state = MutableStateFlow<LoginState>(LoginState.LoggedOut)
    val state = _state.asStateFlow()

    suspend fun login(
        email: String,
        password: String
    ) = withContext(Dispatchers.IO) {
        val status = loginClient.login(email, password)

        if (status is LoginStatus.LoggedIn) {
            _state.value = LoginState.LoggedIn(
                pfpUrl = status.profileImagePath,
                name = status.name,
                accessToken = status.accessToken,
                email = email
            )
        } else {
            _state.value = LoginState.LoggedOut
        }

        return@withContext _state.value is LoginState.LoggedIn
    }

    fun loginWithApiKey(
        apiKey: String
    ) = coroutineScope.launch(Dispatchers.IO) { TODO() }

    fun logout(accessToken: String) = coroutineScope.launch(Dispatchers.IO) {
        loginClient.logout(accessToken)
        _state.value = LoginState.LoggedOut
    }

    fun refresh(accessToken: String) = coroutineScope.launch(Dispatchers.IO) {
        if (baseUrl.isBlank()) return@launch

        val validated = loginClient.validate(accessToken)

        if (!validated) {
            _state.value = LoginState.LoggedOut
            return@launch
        }

        val me = loginClient.getMe(accessToken)

        if (me != null) {
            _state.value = LoginState.LoggedIn(
                pfpUrl = me.profileImagePath,
                name = me.name,
                accessToken = accessToken,
                email = me.email
            )
        } else {
            _state.value = LoginState.LoggedOut
        }
    }
}

@Composable
fun rememberLoginState(baseUrl: String): LoginStateManager {
    val apiClient = LocalApiClient.current
    val coroutineScope = rememberCoroutineScope()

    return remember(baseUrl) {
        LoginStateManager(
            baseUrl = baseUrl,
            coroutineScope = coroutineScope,
            apiClient = apiClient
        )
    }
}
