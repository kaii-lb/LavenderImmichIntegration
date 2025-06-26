package serialization

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String,
    val isAdmin: Boolean,
    val isOnboarded: Boolean = false,
    val name: String,
    val profileImagePath: String,
    val shouldChangePassword: Boolean,
    val userEmail: String,
    val userId: String
)

@Serializable
data class LoginCredentials(
    val email: String,
    val password: String
)

@Serializable
data class LogoutResponse(
    val redirectUri: String,
    val successful: Boolean
)