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
import java.io.File

interface LoginState {
    object LoggedOut : LoginState

    object ServerUnreachable : LoginState

    data class LoggedIn(
        val pfpUrl: String,
        val name: String,
        val accessToken: String,
        val email: String,
        val userId: String
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

    private var savePath = ""

    suspend fun login(
        email: String,
        password: String,
        userAgent: String
    ) = withContext(Dispatchers.IO) {
        val status = loginClient.login(email, password, userAgent)

        if (status is LoginStatus.LoggedIn) {
            _state.value = LoginState.LoggedIn(
                pfpUrl = status.profileImagePath,
                name = status.name,
                accessToken = status.accessToken,
                email = email,
                userId = status.userId
            )
        } else {
            _state.value = LoginState.LoggedOut
        }

        return@withContext _state.value
    }

    fun loginWithApiKey(
        apiKey: String
    ) = coroutineScope.launch(Dispatchers.IO) { TODO() }

    fun logout(accessToken: String) = coroutineScope.launch(Dispatchers.IO) {
        if (loginClient.logout(accessToken)) _state.value = LoginState.LoggedOut
    }

    fun refresh(
        accessToken: String,
        pfpSavePath: String,
        previousPfpUrl: String
    ) = coroutineScope.launch(Dispatchers.IO) {
        if (baseUrl.isBlank()) return@launch

        if (!loginClient.ping(accessToken)) {
            _state.value = LoginState.ServerUnreachable
            return@launch
        }

        val validated = loginClient.validate(accessToken)

        if (!validated) {
            _state.value = LoginState.LoggedOut
            return@launch
        }

        val me = loginClient.getMe(accessToken)

        if (me != null) {
            if (pfpSavePath.isNotBlank() && previousPfpUrl != me.profileImagePath) {
                downloadPfp(userId = me.id, accessToken = accessToken)?.let { pfp ->
                    File(pfpSavePath).writeBytes(pfp)
                    savePath = pfpSavePath
                }
            }

            _state.value = LoginState.LoggedIn(
                pfpUrl = me.profileImagePath,
                name = me.name,
                accessToken = accessToken,
                email = me.email,
                userId = me.id
            )
        } else {
            _state.value = LoginState.LoggedOut
        }
    }

    suspend fun uploadPfp(
        bytes: ByteArray,
        filename: String,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        val res = loginClient.uploadPfp(bytes, filename, accessToken)
        refresh(accessToken, savePath, "")
        return@withContext res
    }

    suspend fun downloadPfp(
        userId: String,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        return@withContext loginClient.downloadPfp(userId, accessToken)
    }

    fun updateUsername(
        name: String,
        accessToken: String
    ) = coroutineScope.launch(Dispatchers.IO) {
        if (loginClient.updateUsername(name, accessToken)) {
            refresh(accessToken, savePath, "")
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
