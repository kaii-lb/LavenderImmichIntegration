package io.github.kaii_lb.lavender.immichintegration.serialization

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

private interface User {
    val avatarColor: UserAvatarColor
    val email: String
    val id: String
    val name: String
    val profileChangedAt: String
    val profileImagePath: String
}

@Serializable
data class UserResponse(
    override val avatarColor: UserAvatarColor,
    override val email: String,
    override val id: String,
    override val name: String,
    override val profileChangedAt: String,
    override val profileImagePath: String
) : User

@Serializable
data class FullUserResponse(
    override val avatarColor: UserAvatarColor,
    override val email: String,
    override val id: String,
    override val name: String,
    override val profileChangedAt: String,
    override val profileImagePath: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val isAdmin: Boolean,
    val license: UserLicense?,
    val oauthId: String,
    val quotaSizeInBytes: Long?,
    val quotaUsageInBytes: Long?,
    val shouldChangePassword: Boolean,
    val status: UserStatus,
    val storageLabel: String?
) : User

@Serializable
data class UserLicense(
    val activatedAt: String,
    val activationKey: String,
    val licenseKey: String
)

@Serializable
enum class UserStatus {
    @SerialName("active")
    Active,

    @SerialName("removing")
    Removing,

    @SerialName("deleted")
    Deleted,
}

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

@Serializable
data class UsageByUserDto(
    val photos: Int,
    val videos: Int,
    val quotaSizeInBytes: Long?,
    val usage: Long,
    val usagePhotos: Long,
    val userId: String,
    val userName: String
)

@Serializable
data class ChangePasswordRequest(
    val invalidateSessions: Boolean,

    @SerialName("password")
    val currentPassword: String,

    val newPassword: String
)