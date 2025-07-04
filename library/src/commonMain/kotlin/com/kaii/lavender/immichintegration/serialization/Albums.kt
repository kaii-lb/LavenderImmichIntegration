package com.kaii.lavender.immichintegration.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val albumName: String,
    val albumThumbnailAssetId: String? = null,
    val albumUsers: List<UserExtra>,
    val assetCount: Int,
    val assets: List<AssetInfo>,
    val createdAt: String,
    val description: String,
    val endDate: String? = null,
    val hasSharedLink: Boolean,
    val id: String,
    val isActivityEnabled: Boolean,
    val lastModifiedAssetTimestamp: String? = null,
    val order: AlbumOrder? = null,
    val owner: User,
    val ownerId: String,
    val shared: Boolean,
    val startDate: String? = null,
    val updatedAt: String
)

@Suppress("unused")
@Serializable
enum class AlbumOrder {
    @SerialName("asc")
    Ascending,

    @SerialName("desc")
    Descending
}

/** @param albumUsers should not contain the album owner, ie: should be empty if you're not sharing with anyone else */
@Serializable
data class CreateAlbum(
    val albumName: String,
    val albumUsers: List<UserMini>,
    val assetIds: List<String>,
    val description: String?
)

@Serializable
data class ModifyAlbumAsset(
    val ids: List<String>
)

@Serializable
data class AlbumAssetModificationResponse(
    val error: AlbumAssetModificationError? = null,
    val id: String,
    val success: Boolean
)

@Suppress("unused")
@Serializable
enum class AlbumAssetModificationError {
    @SerialName("duplicate")
    Duplicate,

    @SerialName("no_permission")
    NoPermission,

    @SerialName("not_found")
    NotFound,

    @SerialName("unknown")
    Unknown
}

@Serializable
data class UpdateAlbumInfo(
    val albumName: String,
    val albumThumbnailAssetId: String,
    val description: String,
    val isActivityEnabled: Boolean,
    val order: AlbumOrder
)