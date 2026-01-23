package com.kaii.lavender.immichintegration.serialization.assets

import com.kaii.lavender.immichintegration.serialization.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class AssetResponse(
    val checksum: String,
    val createdAt: String,
    val deviceAssetId: String,
    val deviceId: String,
    val duplicateId: String? = null,
    val duration: String,
    val exifInfo: String? = null, // TODO: impl
    val fileCreatedAt: String,
    val fileModifiedAt: String,
    val hasMetadata: Boolean,
    val id: String,
    val isArchived: Boolean,
    val isFavorite: Boolean,
    val isOffline: Boolean,
    val isTrashed: Boolean,

    @Deprecated("")
    val libraryId: String? = null,

    val livePhotoVideoId: String? = null,
    val localDateTime: String,
    val originalFileName: String,
    val originalMimeType: String,
    val originalPath: String,
    val owner: UserResponse? = null,
    val ownerId: String,
    val people: String? = null, // TODO: impl

    @Deprecated("")
    val resized: Boolean = false,

    val stack: String? = null, // TODO: impl
    val tags: String? = null, // TODO: impl

    val thumbhash: String?,
    val type: AssetType,
    val unassignedFaces: String? = null, // TODO: impl
    val updatedAt: String,
    val visibility: AssetVisibility
)