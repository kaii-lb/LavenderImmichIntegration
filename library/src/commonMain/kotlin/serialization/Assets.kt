package serialization

import kotlinx.serialization.Serializable

@Serializable
data class UploadedAssetResponse(
    val id: String,
    val status: UploadStatus
)