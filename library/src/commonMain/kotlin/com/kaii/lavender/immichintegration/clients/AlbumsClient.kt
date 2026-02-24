package com.kaii.lavender.immichintegration.clients

import com.kaii.lavender.immichintegration.serialization.albums.Album
import com.kaii.lavender.immichintegration.serialization.albums.AlbumCreationInfo
import com.kaii.lavender.immichintegration.serialization.albums.AlbumCreationState
import com.kaii.lavender.immichintegration.serialization.albums.AlbumsGetAllState
import com.kaii.lavender.immichintegration.serialization.albums.ManageAssetsRequest
import com.kaii.lavender.immichintegration.serialization.albums.ManageAssetsResponse
import io.ktor.client.call.body
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class AlbumsClient(
    private val baseUrl: String,
    private val client: ApiClient
) {
    suspend fun getAll(
        accessToken: String
    ): AlbumsGetAllState {
        val response = client.get(
            url = Url("$baseUrl/api/albums"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<List<Album>>()

        return (response?.let { AlbumsGetAllState.Retrieved(it) } ?: AlbumsGetAllState.Failed)
    }

    suspend fun createAlbum(
        info: AlbumCreationInfo,
        accessToken: String
    ): AlbumCreationState {
        val response = client.post(
            url = Url("$baseUrl/api/albums"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = Json.encodeToString(info)
        )?.body<Album>()

        return (response?.let { AlbumCreationState.Created(it) } ?: AlbumCreationState.Failed)
    }

    suspend fun addAssets(
        albumId: Uuid,
        assetIds: List<Uuid>,
        accessToken: String
    ): Boolean {
        val response = client.post(
            url = Url("$baseUrl/api/albums/${albumId}/assets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = Json.encodeToString(ManageAssetsRequest(ids = assetIds))
        )?.body<ManageAssetsResponse>()

        return response != null && response.error == null && response.success
    }

    suspend fun removeAssets(
        albumId: Uuid,
        assetIds: List<Uuid>,
        accessToken: String
    ): Boolean {
        val response = client.post(
            url = Url("$baseUrl/api/albums/${albumId}/assets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = Json.encodeToString(ManageAssetsRequest(ids = assetIds))
        )?.body<ManageAssetsResponse>()

        return response != null && response.error == null && response.success
    }

    suspend fun get(
        id: Uuid,
        accessToken: String,
        withoutAssets: Boolean = false
    ): Album? {
        val response = client.get(
            url = Url("$baseUrl/api/albums/${id}?withoutAssets=$withoutAssets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<Album>()

        return response
    }

    suspend fun delete(
        id: Uuid,
        accessToken: String
    ): Boolean {
        return client.delete(
            url = Url("$baseUrl/api/albums/${id}"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        ) != null
    }
}