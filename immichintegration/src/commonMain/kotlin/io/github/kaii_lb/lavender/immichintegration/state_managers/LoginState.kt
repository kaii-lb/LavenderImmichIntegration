package io.github.kaii_lb.lavender.immichintegration.state_managers

import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.clients.ApiClient
import io.github.kaii_lb.lavender.immichintegration.clients.LoginClient
import io.github.kaii_lb.lavender.immichintegration.serialization.FullUserResponse
import io.github.kaii_lb.lavender.immichintegration.serialization.LoginStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface LoginState {
    data class LoggedIn(val user: FullUserResponse) : LoginState

    object ServerUnreachable : LoginState

    object LoggedOut : LoginState
}

class LoginStateManager {
    private var loginClient: LoginClient? = null

    fun setEndpoint(endpoint: String, apiClient: ApiClient) {
        if (loginClient?.getEndpoint() == endpoint) return

        loginClient = LoginClient(
            endpoint = endpoint,
            auth = loginClient?.getAuth() ?: Auth.None,
            client = apiClient
        )
    }

    suspend fun login(
        email: String,
        password: String,
        userAgent: String
    ) = withContext(Dispatchers.IO) {
        if (loginClient == null) return@withContext LoginStatus.Failed

        loginClient!!.login(email, password, userAgent)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        loginClient?.logout() == true
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        if (loginClient == null) return@withContext LoginState.LoggedOut

        if (!loginClient!!.ping()) {
            return@withContext LoginState.ServerUnreachable
        }

        val validated = loginClient!!.validate()

        if (!validated) {
            return@withContext LoginState.LoggedOut
        }

        loginClient!!.getMe()?.let {
            LoginState.LoggedIn(user = it)
        } ?: LoginState.LoggedOut
    }

    suspend fun uploadPfp(
        bytes: ByteArray,
        filename: String
    ) = withContext(Dispatchers.IO) {
        if (loginClient == null) return@withContext null

        loginClient!!.uploadPfp(bytes, filename)
    }

    suspend fun downloadPfp(
        userId: String
    ) = withContext(Dispatchers.IO) {
        loginClient?.downloadPfp(userId)
    }

    suspend fun updateInfo(
        name: String? = null,
        email: String? = null
    ) = withContext(Dispatchers.IO) {
        loginClient?.updateInfo(name, email) == true
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        invalidateSessions: Boolean = false
    ) = withContext(Dispatchers.IO) {
        loginClient?.changePassword(currentPassword, newPassword, invalidateSessions) == true
    }
}
