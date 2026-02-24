package com.kaii.lavender.immichintegration.serialization.assets

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
enum class AssetType {
    @SerialName("IMAGE")
    Image,

    @SerialName("VIDEO")
    Video,

    @SerialName("AUDIO")
    Audio,

    @SerialName("OTHER")
    Other
}

@Serializable
enum class AssetVisibility {
    @SerialName("archive")
    Archive,

    @SerialName("timeline")
    Timeline,

    @SerialName("hidden")
    Hidden,

    @SerialName("locked")
    Locked
}

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class AssetUploadRequest(
    val assetData: ByteArray,
    val deviceAssetId: String,
    val deviceId: String,
    val duration: String? = null,
    val fileCreatedAt: String,
    val fileModifiedAt: String,
    val filename: String,
    val isFavorite: Boolean = false,
    val livePhotoVideoId: Uuid? = null,
    val metadata: List<AssetMetaDataUpsertItem> = emptyList(),
    val sidecarData: ByteArray? = null,
    val visibility: AssetVisibility? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AssetUploadRequest

        if (isFavorite != other.isFavorite) return false
        if (!assetData.contentEquals(other.assetData)) return false
        if (deviceAssetId != other.deviceAssetId) return false
        if (deviceId != other.deviceId) return false
        if (duration != other.duration) return false
        if (fileCreatedAt != other.fileCreatedAt) return false
        if (fileModifiedAt != other.fileModifiedAt) return false
        if (filename != other.filename) return false
        if (livePhotoVideoId != other.livePhotoVideoId) return false
        if (metadata != other.metadata) return false
        if (!sidecarData.contentEquals(other.sidecarData)) return false
        if (visibility != other.visibility) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isFavorite.hashCode()
        result = 31 * result + assetData.contentHashCode()
        result = 31 * result + deviceAssetId.hashCode()
        result = 31 * result + deviceId.hashCode()
        result = 31 * result + (duration?.hashCode() ?: 0)
        result = 31 * result + fileCreatedAt.hashCode()
        result = 31 * result + fileModifiedAt.hashCode()
        result = 31 * result + filename.hashCode()
        result = 31 * result + (livePhotoVideoId?.hashCode() ?: 0)
        result = 31 * result + metadata.hashCode()
        result = 31 * result + (sidecarData?.contentHashCode() ?: 0)
        result = 31 * result + (visibility?.hashCode() ?: 0)
        return result
    }

    internal fun toFormData() = MultiPartFormDataContent(
        formData {
            append("assetData", assetData, Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
            })

            append("deviceAssetId", deviceAssetId)
            append("deviceId", deviceId)
            append("filename", filename)
            append("fileModifiedAt", fileModifiedAt)
            append("fileCreatedAt", fileCreatedAt)
            append("isFavorite", isFavorite)

            if (livePhotoVideoId != null) append("livePhotoVideoId", livePhotoVideoId.toString())
            if (metadata.isNotEmpty()) append("metadata", Json.encodeToString(metadata))
            if (sidecarData != null) append("sidecarData", sidecarData)
            if (visibility != null) append("visibility", Json.encodeToString(visibility))
        }
    )
}

@Serializable
data class AssetUploadResponse(
    val id: String,
    val status: AssetMediaStatus
)

@Serializable
data class AssetMetaDataUpsertItem(
    val key: AssetMetadataKey,
    val value: String
)

@Serializable
enum class AssetMetadataKey {
    @SerialName("mobile-app")
    MobileApp
}

@Serializable
enum class AssetMediaStatus {
    @SerialName("created")
    Created,

    @SerialName("replaced")
    Replaced,

    @SerialName("duplicate")
    Duplicate
}

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class AssetDeleteRequest(
    val ids: List<Uuid>,
    val force: Boolean
)