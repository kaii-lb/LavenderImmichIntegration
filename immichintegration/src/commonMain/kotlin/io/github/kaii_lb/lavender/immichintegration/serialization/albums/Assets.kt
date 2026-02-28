package io.github.kaii_lb.lavender.immichintegration.serialization.albums

import io.github.kaii_lb.lavender.immichintegration.serialization.BulkIdErrorReason
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
internal data class AddAssetResponse(
    val error: BulkIdErrorReason? = null,
    val success: Boolean
)

@OptIn(ExperimentalUuidApi::class)
@Serializable
internal data class ManageAssetsRequest(
    val ids: List<Uuid>
)

@OptIn(ExperimentalUuidApi::class)
@Serializable
internal data class ManageAssetsResponse(
    val id: String,
    val success: Boolean,
    val error: BulkIdErrorReason? = null
)

@Serializable
enum class AssetOrder {
    @SerialName("asc")
    Ascending,

    @SerialName("desc")
    Descending
}