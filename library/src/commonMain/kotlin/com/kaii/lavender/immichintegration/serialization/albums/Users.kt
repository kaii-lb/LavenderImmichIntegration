package com.kaii.lavender.immichintegration.serialization.albums

import com.kaii.lavender.immichintegration.serialization.UserResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
enum class AlbumUserRole {
    @SerialName("editor")
    Editor,

    @SerialName("viewer")
    Viewer
}

@Serializable
data class AlbumUserResponse(
    val role: AlbumUserRole,
    val user: UserResponse,
)

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class AlbumUserCreationInfo(
    val role: AlbumUserRole,
    val userId: Uuid,
)

@Serializable
data class ContributorCountResponse(
    val assetCount: Int,
    val userId: String
)