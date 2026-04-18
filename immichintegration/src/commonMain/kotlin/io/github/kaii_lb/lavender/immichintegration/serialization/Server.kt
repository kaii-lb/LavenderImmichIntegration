package io.github.kaii_lb.lavender.immichintegration.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerStorage(
    val diskAvailable: String,
    val diskAvailableRaw: Long,
    val diskSize: String,
    val diskSizeRaw: Long,
    val diskUse: String,
    val diskUseRaw: Long,
    val diskUsagePercentage: Float
)

@Serializable
data class ServerPing(
    @SerialName("res")
    val response: String
)

@Serializable
data class ServerInfo(
    val build: String? = null,
    val buildImage: String? = null,
    val buildImageUrl: String? = null,
    val buildUrl: String? = null,
    val exiftool: String? = null,
    val ffmpeg: String? = null,
    val imagemagick: String? = null,
    val libvips: String? = null,
    val licensed: Boolean,
    val nodejs: String? = null,
    val repository: String? = null,
    val repositoryUrl: String? = null,
    val sourceCommit: String? = null,
    val sourceRef: String? = null,
    val sourceUrl: String? = null,
    val thirdPartyBugFeatureUrl: String? = null,
    val thirdPartyDocumentationUrl: String? = null,
    val thirdPartySourceUrl: String? = null,
    val thirdPartySupportUrl: String? = null,
    val version: String,
    val versionUrl: String
)

@Serializable
data class ServerStatistics(
    val photos: Int,
    val videos: Int,
    val usage: Long,
    val usageByUser: List<UsageByUserDto>,
    val usagePhotos: Long,
    val usageVideos: Long
)