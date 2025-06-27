package serialization

import kotlinx.serialization.Serializable

@Serializable
data class UploadedAssetResponse(
    val id: String,
    val status: UploadStatus
)

@Serializable
data class DeleteAssets(
    val force: Boolean,
    val ids: List<String>
)

/** @param rating is: -1 <= rating <= 5 */
@Serializable
data class UpdateAssets(
    val ids: List<String>,
    val isFavourite: Boolean,
    val latitude: Long,
    val longitude: Long,
    val rating: Int,
    val visibility: FileVisibility,
    val description: String,
    val dateTimeOriginal: Long,
    val duplicateId: String
)