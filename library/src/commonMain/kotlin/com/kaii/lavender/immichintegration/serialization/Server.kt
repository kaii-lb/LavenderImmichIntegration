package com.kaii.lavender.immichintegration.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerStorage(
    val diskAvailable: String,
    val diskAvailableRaw: Float,
    val diskSize: String,
    val diskSizeRaw: Float,
    val diskUse: String,
    val diskUseRaw: Float,
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