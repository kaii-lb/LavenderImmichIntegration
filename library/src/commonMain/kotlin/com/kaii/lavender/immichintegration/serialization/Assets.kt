package com.kaii.lavender.immichintegration.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class UploadedAssetResponse(
    val id: String,
    val status: UploadStatus
)

@Serializable
data class DeleteAssets(
    val ids: List<String>,
    val force: Boolean
)

/** @param rating is: -1 <= rating <= 5 */
@Serializable
data class UpdateAssets(
    val ids: List<String>,
    val isFavourite: Boolean,
    val latitude: Float,
    val longitude: Float,
    val rating: Int,
    val visibility: FileVisibility,
    val description: String,
    val dateTimeOriginal: Long,
    val duplicateId: String
)

@Suppress("unused")
/** @param checksum is sha1 hash*/
@Serializable
data class AssetInfo(
    val checksum: String,
    val deviceAssetId: String,
    val deviceId: String,
    val duplicateId: String?,
    val duration: String,
    val hasMetadata: Boolean,
    val exifInfo: AssetExifInfo?,
    val fileCreatedAt: String,
    val fileModifiedAt: String,
    val id: String,
    val isArchived: Boolean,
    val isFavorite: Boolean?,
    val isOffline: Boolean,
    val isTrashed: Boolean,

    @Deprecated("This property was deprecated in v1.106.0")
    val libraryId: String?,

    val livePhotoVideoId: String?,
    val localDateTime: String,
    val originalFileName: String,
    val originalMimeType: String?,
    val originalPath: String,
    val owner: User?,
    val ownerId: String,

    @Deprecated("This property was deprecated in v1.113.0")
    val resized: Boolean?,

    val thumbhash: String,
    val type: AssetType,
    val updatedAt: String,
    val visibility: FileVisibility,
    val tags: List<AssetTags>?,

    val unassignedFaces: List<Face>?,
    val stack: Stack?,
    val people: List<Person>?,
) {
    /** Note that the returned file's path is just the file name*/
    @OptIn(ExperimentalTime::class)
    val file: File
        get() = File(
            path = originalFileName,
            size = exifInfo?.fileSizeInByte ?: 0,
            dateCreated = Instant.parse(fileCreatedAt).toEpochMilliseconds(),
            lastModified = Instant.parse(fileModifiedAt).toEpochMilliseconds()
        )

    @OptIn(ExperimentalTime::class)
    val updateAtMillis: Long
        get() = Instant.parse(updatedAt).toEpochMilliseconds()
}

@Serializable
data class AssetExifInfo(
    val city: String?,
    val country: String?,
    val dateTimeOriginal: String?,
    val description: String?,
    val exifImageHeight: Int?,
    val exifImageWidth: Int?,
    val exposureTime: String?,
    val fNumber: String?,
    val fileSizeInByte: Long?,
    val focalLength: Float?,
    val iso: Float?,
    val latitude: Float?,
    val longitude: Float?,
    val lensModel: String?,
    val make: String?,
    val model: String?,
    val modifyDate: String?,
    val orientation: String?,
    val projectionType: String?,
    val rating: Int?,
    val state: UploadStatus?,
    val timeZone: String?
)

@Suppress("unused")
@Serializable
enum class AssetType {
    IMAGE,
    VIDEO,
    AUDIO,
    OTHER
}

@Serializable
data class AssetTags(
    val color: String,
    val createdAt: String,
    val id: String,
    val name: String,
    val parentId: String,
    val updatedAt: String,
    val value: String
)

@Serializable
data class Face(
    val boundingBoxX1: Int,
    val boundingBoxX2: Int,
    val boundingBoxY1: Int,
    val boundingBoxY2: Int,
    val id: String,
    val imageHeight: String,
    val imageWidth: String,
    val sourceType: FaceSourceType?
)

@Suppress("unused")
@Serializable
enum class FaceSourceType {
    @SerialName("machine-learning")
    MachineLearning,
    @SerialName("exif")
    EXIF,
    @SerialName("manual")
    Manual
}

@Serializable
data class Stack(
    val assetCount: Int,
    val id: String,
    val primaryAssetId: String
)

@Suppress("unused")
@Serializable
data class Person(
    val birthdate: String,
    val color: String?,
    val faces: List<Face>
)

@Serializable
data class ExistingAssets(
    val deviceAssetIds: List<String>,
    val deviceId: String
)

@Serializable
data class ExistingAssetsResponse(
    val existingIds: List<String>
)

@Serializable
data class DownloadAssetsZip(
    val assetIds: List<String>
)