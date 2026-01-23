package com.kaii.lavender.immichintegration.serialization.assets

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class AssetBulkUploadCheckItem(
    val checksum: String,
    val id: String
)

@Serializable
data class AssetBulkUploadCheckResult(
    val action: String,
    val assetId: String? = null,
    val id: String,
    val isTrashed: Boolean = false,
    val reason: String? = null
)

@Serializable
data class AssetBulkUploadRequest(
    val assets: List<AssetBulkUploadCheckItem>
)

@Serializable
data class AssetBulkUploadResponse(
    val assets: List<AssetBulkUploadCheckResult>
)

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class UploadAssetRequest(
    val absolutePath: String,
    val filename: String,
    val id: Uuid,
    val size: Long,
    val dateTaken: Long,
    val dateModified: Long
)
