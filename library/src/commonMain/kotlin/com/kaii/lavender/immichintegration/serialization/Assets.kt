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
    val duplicateId: String? = null,
    val duration: String,
    val hasMetadata: Boolean,
    val exifInfo: AssetExifInfo? = null,
    val fileCreatedAt: String,
    val fileModifiedAt: String,
    val id: String,
    val isArchived: Boolean,
    val isFavorite: Boolean? = null,
    val isOffline: Boolean,
    val isTrashed: Boolean,

    @Deprecated("This property was deprecated in v1.106.0")
    val libraryId: String? = null,

    val livePhotoVideoId: String? = null,
    val localDateTime: String,
    val originalFileName: String,
    val originalMimeType: String? = null,
    val originalPath: String,
    val owner: User? = null,
    val ownerId: String,

    @Deprecated("This property was deprecated in v1.113.0")
    val resized: Boolean? = null,

    val thumbhash: String,
    val type: AssetType,
    val updatedAt: String,
    val visibility: FileVisibility,
    val tags: List<AssetTags>? = null,

    val unassignedFaces: List<Face>? = null,
    val stack: Stack? = null,
    val people: List<Person>? = null,
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
    val city: String? = null,
    val country: String? = null,
    val state: String? = null,
    val dateTimeOriginal: String? = null,
    val description: String? = null,
    val exifImageHeight: Int? = null,
    val exifImageWidth: Int? = null,
    val exposureTime: String? = null,
    val fNumber: String? = null,
    val fileSizeInByte: Long? = null,
    val focalLength: Float? = null,
    val iso: Float? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
    val lensModel: String? = null,
    val make: String? = null,
    val model: String? = null,
    val modifyDate: String? = null,
    val orientation: String? = null,
    val projectionType: String? = null,
    val rating: Int? = null,
    val timeZone: String? = null
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
    val sourceType: FaceSourceType? = null
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
    val color: String? = null,
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

@Serializable
data class DuplicateAsset(
    val duplicateId: String,
    val assets: List<AssetInfo>
)

@Serializable
data class CheckBulkUpload(
    val assets: List<CheckBulkUploadAsset>
)

@Serializable
data class CheckBulkUploadAsset(
    val checksum: String,
    @SerialName("id") val filename: String
)

@Serializable
data class CheckBulkUploadResponse(
    val results: List<CheckBulkUploadResponseInfo>
)

@Serializable
data class CheckBulkUploadResponseInfo(
    val action: CheckBulkUploadAction,
    val assetId: String? = null,
    @SerialName("id") val filename: String,
    val isTrashed: Boolean = false,
    val reason: CheckBulkUploadReason? = null
)

@Serializable
enum class CheckBulkUploadAction {
    @SerialName("accept")
    Accept,
    @SerialName("reject")
    Reject
}

@Serializable
enum class CheckBulkUploadReason {
    @SerialName("duplicate")
    Duplicate,
    @SerialName("unsupported-format")
    UnsupportedFormat
}