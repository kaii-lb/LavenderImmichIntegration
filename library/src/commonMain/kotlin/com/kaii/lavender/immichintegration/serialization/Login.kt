package com.kaii.lavender.immichintegration.serialization

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
        @SerialName("accessToken")
        val accessToken: String,

        @SerialName("isAdmin")
        val isAdmin: Boolean,

        @SerialName("isOnboarded")
        val isOnboarded: Boolean,

        @SerialName("name")
        val name: String,

        @SerialName("profileImagePath")
        val profileImagePath: String,

        @SerialName("shouldChangePassword")
        val shouldChangePassword: Boolean,

        @SerialName("userEmail")
        val userEmail: String,

        @SerialName("userId")
        val userId: String,
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