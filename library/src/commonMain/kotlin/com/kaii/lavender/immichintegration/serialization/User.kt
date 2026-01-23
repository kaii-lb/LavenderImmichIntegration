package com.kaii.lavender.immichintegration.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserAvatarColor {
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

    @SerialName("ambe")
    Amber
}

@Serializable
data class UserResponse(
    val avatarColor: UserAvatarColor,
    val email: String,
    val id: String,
    val name: String,
    val profileChangedAt: String,
    val profileImagePath: String
)