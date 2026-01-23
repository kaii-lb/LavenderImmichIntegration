package com.kaii.lavender.immichintegration.serialization.albums

import com.kaii.lavender.immichintegration.serialization.UserResponse
import com.kaii.lavender.immichintegration.serialization.assets.AssetResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    @SerialName("albumName")
    val albumName: String,

    @SerialName("albumThumbnailAssetId")
    val albumThumbnailAssetId: String?,

    @SerialName("albumUsers")
    val albumUsers: List<AlbumUserResponse>,

    @SerialName("assetCount")
    val assetCount: Int,

    @SerialName("assets")
    val assets: List<AssetResponse>,

    @SerialName("contributorCounts")
    val contributorCounts: List<ContributorCountResponse>? = null,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("description")
    val description: String,

    @SerialName("endDate")
    val endDate: String? = null,

    @SerialName("hasSharedLink")
    val hasSharedLink: Boolean,
    @SerialName("id")

    val id: String,
    @SerialName("isActivityEnabled")
    val isActivityEnabled: Boolean,

    @SerialName("lastModifiedAssetTimestamp")
    val lastModifiedAssetTimestamp: String? = null,

    @SerialName("order")
    val order: AssetOrder? = null,

    @SerialName("owner")

    val owner: UserResponse,
    @SerialName("ownerId")

    val ownerId: String,
    @SerialName("shared")
    val shared: Boolean,

    @SerialName("startDate")
    val startDate: String? = null,

    @SerialName("updatedAt")
    val updatedAt: String,
)