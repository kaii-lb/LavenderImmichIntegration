package com.kaii.lavender.immichintegration.serialization

import kotlinx.serialization.Serializable

@Serializable
data class ServerInfo(
    val build: String?,
    val version: String?
)

@Serializable
data class ServerStorage(
    val diskAvailable: String,
    val diskAvailableRaw: Long,
    val diskSize: String,
    val diskSizeRaw: Long,
    val diskUsagePercentage: Double,
    val diskUse: String,
    val diskUseRaw: Long
)