package io.github.kaii_lb.lavender.immichintegration.state_managers

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

    fun setBaseUrl(baseUrl: String, apiClient: ApiClient) {
        if (loginClient?.baseUrl == baseUrl) return

        loginClient = LoginClient(
            baseUrl = baseUrl,
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

    suspend fun logout(accessToken: String) = withContext(Dispatchers.IO) {
        loginClient?.logout(accessToken) == true
    }

    suspend fun refresh(accessToken: String) = withContext(Dispatchers.IO) {
        if (loginClient == null) return@withContext LoginState.LoggedOut

        if (!loginClient!!.ping()) {
            return@withContext LoginState.ServerUnreachable
        }

        val validated = loginClient!!.validate(accessToken)

        if (!validated) {
            return@withContext LoginState.LoggedOut
        }

        loginClient!!.getMe(accessToken)?.let {
            LoginState.LoggedIn(user = it)
        } ?: LoginState.LoggedOut
    }

    suspend fun uploadPfp(
        bytes: ByteArray,
        filename: String,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        if (loginClient == null) return@withContext null

        loginClient!!.uploadPfp(bytes, filename, accessToken)
    }

    suspend fun downloadPfp(
        userId: String,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        loginClient?.downloadPfp(userId, accessToken)
    }

    suspend fun updateInfo(
        accessToken: String,
        name: String? = null,
        email: String? = null
    ) = withContext(Dispatchers.IO) {
        loginClient?.updateInfo(accessToken, name, email) == true
    }

    suspend fun changePassword(
        accessToken: String,
        currentPassword: String,
        newPassword: String,
        invalidateSessions: Boolean = false
    ) = withContext(Dispatchers.IO) {
        loginClient?.changePassword(accessToken, currentPassword, newPassword, invalidateSessions) == true
    }
}
