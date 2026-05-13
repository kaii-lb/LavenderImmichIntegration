package io.github.kaii_lb.lavender.immichintegration.state_managers

import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.clients.ApiClient
import io.github.kaii_lb.lavender.immichintegration.clients.LoginClient
import io.github.kaii_lb.lavender.immichintegration.serialization.FullUserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface LoginState {
    data class LoggedIn(val user: FullUserResponse) : LoginState

    object ServerUnreachable : LoginState

    object LoggedOut : LoginState
}

class LoginStateManager(apiClient: ApiClient) {
    private val loginClient = LoginClient(
        endpoint = "",
        auth = Auth.None,
        client = apiClient
    )

    fun setEndpoint(endpoint: String) {
        loginClient.setEndpoint(endpoint)
    }

    fun setAuth(auth: Auth) {
        loginClient.setAuth(auth)
    }

    suspend fun login(
        email: String,
        password: String,
        userAgent: String
    ) = withContext(Dispatchers.IO) {
        loginClient.login(email, password, userAgent)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        loginClient.logout()
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        if (!loginClient.ping()) {
            return@withContext LoginState.ServerUnreachable
        }

        val validated = loginClient.validate()

        if (!validated) {
            return@withContext LoginState.LoggedOut
        }

        loginClient.getMe()?.let {
            LoginState.LoggedIn(user = it)
        } ?: LoginState.LoggedOut
    }

    suspend fun uploadPfp(
        bytes: ByteArray,
        filename: String
    ) = withContext(Dispatchers.IO) {
        loginClient.uploadPfp(bytes, filename)
    }

    suspend fun downloadPfp(
        userId: String
    ) = withContext(Dispatchers.IO) {
        loginClient.downloadPfp(userId)
    }

    suspend fun updateInfo(
        name: String? = null,
        email: String? = null
    ) = withContext(Dispatchers.IO) {
        loginClient.updateInfo(name, email)
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        invalidateSessions: Boolean = false
    ) = withContext(Dispatchers.IO) {
        loginClient.changePassword(currentPassword, newPassword, invalidateSessions)
    }
}
