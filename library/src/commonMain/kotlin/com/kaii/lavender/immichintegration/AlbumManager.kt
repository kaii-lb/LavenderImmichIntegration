package com.kaii.lavender.immichintegration

import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import com.kaii.lavender.immichintegration.serialization.Album
import com.kaii.lavender.immichintegration.serialization.AlbumAssetModificationResponse
import com.kaii.lavender.immichintegration.serialization.CreateAlbum
import com.kaii.lavender.immichintegration.serialization.ModifyAlbumAsset
import com.kaii.lavender.immichintegration.serialization.UpdateAlbumInfo
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

@Suppress("unused")
class AlbumManager(
    private val apiClient: ApiClient,
    private val endpointBase: String,
    private val bearerToken: String
) {
    suspend fun getAllAlbums(
        assetId: String? = null,
        shared: Boolean = false
    ): List<Album>? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/albums?shared=${shared}" + if (assetId != null) "&assetId=${assetId}" else ""),
            headers = mapOf(
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<List<Album>>()
    }

    suspend fun createAlbum(
        album: CreateAlbum
    ): Album? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/albums"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = album
        )

        return response?.body<Album>()
    }

    suspend fun addAssetToAlbum(
        albumId: String,
        assets: ModifyAlbumAsset
    ): List<AlbumAssetModificationResponse>? {
        val response = apiClient.put(
            url = Url("$endpointBase/api/albums/${albumId}/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = assets
        )

        return response?.body<List<AlbumAssetModificationResponse>>()
    }

    suspend fun removeAssetFromAlbum(
        albumId: String,
        assets: ModifyAlbumAsset
    ): List<AlbumAssetModificationResponse>? {
        val response = apiClient.delete(
            url = Url("$endpointBase/api/albums/${albumId}/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = assets
        )

        return response?.body<List<AlbumAssetModificationResponse>>()
    }

    suspend fun getAlbumInfo(
        albumId: String,
        withoutAssets: Boolean = false
    ): Album? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/albums/${albumId}?withoutAssets=${withoutAssets}"),
            headers = mapOf(
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        val json = response?.bodyAsText()

        if (json == null) return null

        return if (json.startsWith("[")) {
            Json.decodeFromString<List<Album>>(json).first()
        } else {
            Json.decodeFromString<Album>(json)
        }
    }

    suspend fun updateAlbumInfo(
        albumId: String,
        info: UpdateAlbumInfo
    ): Album? {
        val response = apiClient.patch(
            url = Url("$endpointBase/api/albums/${albumId}"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = info
        )

        return response?.body<Album>()
    }

    suspend fun deleteAlbum(
        albumId: String
    ): Boolean {
        val response = apiClient.delete(
            url = Url("$endpointBase/api/albums/${albumId}"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.status == HttpStatusCode.OK
    }
}