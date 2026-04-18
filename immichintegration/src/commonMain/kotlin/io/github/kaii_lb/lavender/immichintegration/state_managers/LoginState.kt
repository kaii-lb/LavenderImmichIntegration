@file:Suppress("unused")

package io.github.kaii_lb.lavender.immichintegration.state_managers

import io.github.kaii_lb.lavender.immichintegration.clients.ApiClient
import io.github.kaii_lb.lavender.immichintegration.clients.LoginClient
import io.github.kaii_lb.lavender.immichintegration.serialization.LoginStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

interface LoginState {
    object LoggedOut : LoginState

    object ServerUnreachable : LoginState

    data class LoggedIn(
        val pfpUrl: String,
        val name: String,
        val accessToken: String,
        val email: String,
        val userId: String,
        val isAdmin: Boolean
    ) : LoginState
}

class LoginStateManager(
    private val coroutineScope: CoroutineScope
) {
    private var savePath = ""

    private var loginClient: LoginClient? = null

    fun setBaseUrl(baseUrl: String, apiClient: ApiClient) {
        if (baseUrl.isBlank()) return

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
        if (loginClient == null) return@withContext LoginState.LoggedOut

        val status = loginClient!!.login(email, password, userAgent)

        if (status is LoginStatus.LoggedIn) {
            LoginState.LoggedIn(
                pfpUrl = status.profileImagePath,
                name = status.name,
                accessToken = status.accessToken,
                email = email,
                userId = status.userId,
                isAdmin = status.isAdmin
            )
        } else {
            LoginState.LoggedOut
        }
    }

    suspend fun logout(accessToken: String) = withContext(Dispatchers.IO) {
        loginClient?.logout(accessToken) == true
    }

    suspend fun refresh(
        accessToken: String,
        pfpSavePath: String,
        previousPfpUrl: String
    ) = withContext(Dispatchers.IO) {
        if (loginClient == null) return@withContext LoginState.LoggedOut

        if (!loginClient!!.ping()) {
            return@withContext LoginState.ServerUnreachable
        }

        val validated = loginClient!!.validate(accessToken)

        if (!validated) {
            return@withContext LoginState.LoggedOut
        }

        val me = loginClient!!.getMe(accessToken)

        if (me != null) {
            if (pfpSavePath.isNotBlank() && previousPfpUrl != me.profileImagePath) {
                downloadPfp(userId = me.id, accessToken = accessToken)?.let { pfp ->
                    SystemFileSystem.sink(Path(pfpSavePath)).buffered().use {
                        it.write(pfp)
                        it.flush()
                    }

                    savePath = pfpSavePath
                }
            }

            LoginState.LoggedIn(
                pfpUrl = me.profileImagePath,
                name = me.name,
                accessToken = accessToken,
                email = me.email,
                userId = me.id,
                isAdmin = me.isAdmin
            )
        } else {
            LoginState.LoggedOut
        }
    }

    suspend fun uploadPfp(
        bytes: ByteArray,
        filename: String,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        if (loginClient == null) return@withContext null

        val res = loginClient!!.uploadPfp(bytes, filename, accessToken)
        refresh(accessToken, savePath, "")
        return@withContext res
    }

    suspend fun downloadPfp(
        userId: String,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        return@withContext loginClient?.downloadPfp(userId, accessToken)
    }

    suspend fun updateInfo(
        accessToken: String,
        name: String? = null,
        email: String? = null
    ) = withContext(Dispatchers.IO) {
        val success = loginClient?.updateInfo(accessToken, name, email) == true
        if (success) {
            refresh(accessToken, savePath, "")
        }

        success
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
