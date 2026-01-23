package com.kaii.lavender.immichintegration.serialization.albums

interface AlbumsGetAllState {
    data class Retrieved(val albums: List<Album>) : AlbumsGetAllState

    object Failed : AlbumsGetAllState
}

interface AlbumGetState {
    data class Retrieved(val album: Album) : AlbumGetState

    object Failed : AlbumGetState
}