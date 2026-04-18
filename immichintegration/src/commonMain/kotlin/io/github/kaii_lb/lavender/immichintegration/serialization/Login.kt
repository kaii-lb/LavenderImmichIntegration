package io.github.kaii_lb.lavender.immichintegration.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentials(
    val email: String,
    val password: String
)

interface LoginStatus {
    @Serializable
    data class LoggedIn(
        val accessToken: String,
        val isAdmin: Boolean,
        val isOnboarded: Boolean,
        val name: String,
        val profileImagePath: String,
        val shouldChangePassword: Boolean,
        val userEmail: String,
        val userId: String
    ) : LoginStatus

    object Failed : LoginStatus
}

@Serializable
data class LogoutStatus(
    val redirectUri: String,
    val successful: Boolean
)

@Serializable
data class AuthStatus(
    @SerialName("authStatus")
    val valid: Boolean
)