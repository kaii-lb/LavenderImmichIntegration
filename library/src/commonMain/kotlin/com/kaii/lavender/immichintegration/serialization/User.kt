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

@Serializable
data class ProfilePictureUpload(
    val file: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ProfilePictureUpload

        return file.contentEquals(other.file)
    }

    override fun hashCode(): Int {
        return file.contentHashCode()
    }
}

@Serializable
data class ProfilePictureResponse(
    val profileChangedAt: String,
    val profileImagePath: String,
    val userId: String
)

@Serializable
data class UserDetails(
    val avatarColor: UserAvatarColor? = null,
    val email: String? = null,
    val name: String? = null,
    val password: String? = null
)