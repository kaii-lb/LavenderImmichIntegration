package com.kaii.lavender.immichintegration.serialization

import kotlinx.serialization.SerialName
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

@Serializable
data class User(
    val avatarColor: UserAvatarColors,
    val email: String,
    val id: String,
    val name: String,
    val profileChangedAt: String,
    val profileImagePath: String
)

@Serializable
data class UserExtra(
    val role: UserRole,
    val user: User
)

@Serializable
data class UserMini(
    val role: UserRole,
    val userId: String
)

@Serializable
data class UserFull(
    val avatarColor: UserAvatarColors,
    val email: String,
    val id: String,
    val name: String,
    val isAdmin: Boolean,
    val shouldChangePassword: Boolean,
    val profileChangedAt: String,
    val profileImagePath: String,
    val createdAt: String,
    val deletedAt: String? = null
)

@Suppress("unused")
@Serializable
enum class UserRole {
    @SerialName("editor")
    Editor,
    @SerialName("viewer")
    Viewer
}

@Suppress("unused")
@Serializable
enum class UserAvatarColors {
    @SerialName("primary")
    Primary,
    @SerialName("pink")
    Pink,
    @SerialName("red")
    Red,
    @SerialName("yellow")
    Yellow,
    @SerialName("blue")
    Blue,
    @SerialName("green")
    Green,
    @SerialName("purple")
    Purple,
    @SerialName("orange")
    Orange,
    @SerialName("gray")
    Gray,
    @SerialName("amber")
    Amber
}

@Serializable
data class CreateProfilePicResponse(
    val profileChangedAt: String,
    val profileImagePath: String,
    val userId: String
)

@Serializable
data class UpdateUserInfo(
    val avatarColor: UserAvatarColors? = null,
    val email: String,
    val password: String? = null,
    val name: String
)