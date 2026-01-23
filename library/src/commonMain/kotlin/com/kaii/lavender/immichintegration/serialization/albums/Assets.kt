package com.kaii.lavender.immichintegration.serialization.albums

import com.kaii.lavender.immichintegration.serialization.BulkIdErrorReason
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
internal data class AddAssetInfo(
    val albumIds: List<Uuid>,
    val assetIds: List<Uuid>
)

@Serializable
enum class AssetOrder {
    @SerialName("asc")
    Ascending,

    @SerialName("desc")
    Descending
}