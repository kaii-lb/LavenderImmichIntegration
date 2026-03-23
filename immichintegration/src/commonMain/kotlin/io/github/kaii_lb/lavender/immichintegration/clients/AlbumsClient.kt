package io.github.kaii_lb.lavender.immichintegration.clients

import io.github.kaii_lb.lavender.immichintegration.serialization.albums.Album
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumCreationInfo
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumCreationState
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumRenameRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumsGetAllState
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.ManageAssetsRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.ManageAssetsResponse
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
        val response = client.put(
            url = Url("$baseUrl/api/albums/${albumId}/assets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = Json.encodeToString(ManageAssetsRequest(ids = assetIds))
        )?.body<List<ManageAssetsResponse>>()

        return response != null && response.all { it.error == null && it.success }
    }

    suspend fun removeAssets(
        albumId: Uuid,
        assetIds: List<Uuid>,
        accessToken: String
    ): Boolean {
        val response = client.delete(
            url = Url("$baseUrl/api/albums/${albumId}/assets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = Json.encodeToString(ManageAssetsRequest(ids = assetIds))
        )?.body<List<ManageAssetsResponse>>()

        return response != null && response.all { it.error == null && it.success }
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

    suspend fun rename(
        id: Uuid,
        newName: String,
        accessToken: String
    ): Boolean {
        return client.patch(
            url = Url("$baseUrl/api/albums/${id}"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = AlbumRenameRequest(newName)
        ) != null
    }
}