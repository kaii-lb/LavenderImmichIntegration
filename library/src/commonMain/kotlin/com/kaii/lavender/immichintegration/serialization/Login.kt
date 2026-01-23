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
        public val accessToken: String,

        @SerialName("isAdmin")
        public val isAdmin: Boolean,

        @SerialName("isOnboarded")
        public val isOnboarded: Boolean,

        @SerialName("name")
        public val name: String,

        @SerialName("profileImagePath")
        public val profileImagePath: String,

        @SerialName("shouldChangePassword")
        public val shouldChangePassword: Boolean,

        @SerialName("userEmail")
        public val userEmail: String,

        @SerialName("userId")
        public val userId: String,
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
    val valid: Boolean
)