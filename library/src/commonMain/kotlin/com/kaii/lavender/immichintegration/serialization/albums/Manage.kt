package com.kaii.lavender.immichintegration.serialization.albums

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class AlbumCreationInfo(
    val albumName: String,
    val albumUsers: AlbumUserCreationInfo,
    val assetIds: List<Uuid>,
    val description: String
)

interface AlbumCreationState {
    data class Created(val album: Album) : AlbumCreationState

    object Failed : AlbumCreationState
}