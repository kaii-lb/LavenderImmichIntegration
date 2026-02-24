package com.kaii.lavender.immichintegration.serialization.assets

import com.kaii.lavender.immichintegration.serialization.UserResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AssetResponse(
    val checksum: String,
    val createdAt: String,
    val deviceAssetId: String,
    val deviceId: String,
    val duplicateId: String? = null,
    val duration: String,
    val exifInfo: ExifInfo? = null,
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
    val people: List<PersonWithFace> = emptyList(),

    @Deprecated("")
    val resized: Boolean = false,

    val stack: AssetStack? = null,
    val tags: List<Tag> = emptyList(),

    val thumbhash: String?,
    val type: AssetType,
    val unassignedFaces: List<AssetFaceWithoutPerson> = emptyList(),
    val updatedAt: String,
    val visibility: AssetVisibility
)

@Serializable
data class ExifInfo(
    val city: String? = null,
    val country: String? = null,
    val dateTimeOriginal: String? = null,
    val description: String? = null,
    val exifImageHeight: Int? = null,
    val exifImageWidth: Int? = null,
    val exposureTime: String? = null,
    val fNumber: Double? = null,
    val fileSizeInByte: Long? = null,
    val focalLength: Double? = null,
    val iso: Double? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val lensModel: String? = null,
    val make: String? = null,
    val model: String? = null,
    val modifyDate: String? = null,
    val orientation: String? = null,
    val projectionType: String? = null,
    val rating: Int? = null,
    val state: String? = null,
    val timeZone: String? = null
)

@Serializable
data class AssetFaceWithoutPerson(
    val boundingBoxX1: Float,
    val boundingBoxX2: Float,
    val boundingBoxY1: Float,
    val boundingBoxY2: Float,
    val id: String,
    val imageHeight: Int,
    val imageWidth: Int,
    val sourceType: SourceType? = null
)

@Serializable
data class PersonWithFace(
    val birthDate: String? = null,
    val color: String? = null,
    val faces: List<AssetFaceWithoutPerson>,
    val id: String,
    val isFavorite: Boolean = false,
    val isHidden: Boolean,
    val name: String,
    val thumbnailPath: String,
    val updatedAt: String? = null
)

@Serializable
enum class SourceType {
    @SerialName("machine-learning")
    MachineLearning,

    @SerialName("exif")
    Exif,

    @SerialName("manual")
    Manual,
}

@Serializable
data class AssetStack(
    val assetCount: Int,
    val id: String,
    val primaryAssetId: String
)

@Serializable
data class Tag(
    val color: String? = null,
    val createdAt: String,
    val id: String,
    val name: String,
    val parentId: String? = null,
    val updatedAt: String,
    val value: String
)