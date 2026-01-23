package com.kaii.lavender.immichintegration.clients

import com.kaii.lavender.immichintegration.XApiKey
import com.kaii.lavender.immichintegration.serialization.albums.AddAssetInfo
import com.kaii.lavender.immichintegration.serialization.albums.AddAssetResponse
import com.kaii.lavender.immichintegration.serialization.albums.Album
import com.kaii.lavender.immichintegration.serialization.albums.AlbumCreationInfo
import com.kaii.lavender.immichintegration.serialization.albums.AlbumCreationState
import com.kaii.lavender.immichintegration.serialization.albums.AlbumGetState
import com.kaii.lavender.immichintegration.serialization.albums.AlbumsGetAllState
import io.ktor.client.call.body
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AlbumsClient(
    private val baseUrl: String,
    private val client: ApiClient
) {
    /** @param assetId Only returns albums that contain the asset Ignores the shared parameter undefined: get all albums*/
    suspend fun getAll(
        accessToken: String,
        assetId: String? = null,
        shared: Boolean? = null
    ): AlbumsGetAllState {
        val response = client.get(
            url = Url("$baseUrl/albums?assetId=$assetId&shared=$shared"),
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
            url = Url("$baseUrl/albums"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = Json.encodeToString(info)
        )?.body<Album>()

        return (response?.let { AlbumCreationState.Created(it) } ?: AlbumCreationState.Failed)
    }

    suspend fun addAssetsToAlbums(
        albumIds: List<Uuid>,
        assetIds: List<Uuid>,
        accessToken: String
    ): Boolean {
        val response = client.post(
            url = Url("$baseUrl/albums/assets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = Json.encodeToString(AddAssetInfo(albumIds, assetIds))
        )?.body<AddAssetResponse>()

        return response != null && response.error == null && response.success
    }

    suspend fun get(
        id: Uuid,
        accessToken: String,
        withoutAssets: Boolean = false
    ): AlbumGetState {
        val response = client.get(
            url = Url("$baseUrl/albums/{$id}?withoutAssets=$withoutAssets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<Album>()

        return (response?.let { AlbumGetState.Retrieved(it) } ?: AlbumGetState.Failed)
    }

    suspend fun delete(
        id: Uuid,
        accessToken: String
    ): Boolean {
        return client.delete(
            url = Url("$baseUrl/albums/{$id}"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        ) != null
    }
}