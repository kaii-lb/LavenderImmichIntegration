package io.github.kaii_lb.lavender.immichintegration.clients

import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.Album
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumCreationInfo
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumCreationState
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumRenameRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.AlbumsGetAllState
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.ManageAssetsRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.albums.ManageAssetsResponse
import io.ktor.client.call.body
import io.ktor.http.Url
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class AlbumsClient(
    private val client: ApiClient,
    endpoint: String,
    auth: Auth
) : BaseClient(endpoint, auth) {
    suspend fun getAll(): AlbumsGetAllState {
        if (endpoint.isBlank() || auth.asString().isBlank()) return AlbumsGetAllState.Failed

        val response = client.get(
            url = Url("$endpoint/api/albums"),
            headers = auth.headers,
            body = null
        )?.body<List<Album>>()

        return (response?.let { AlbumsGetAllState.Retrieved(it) } ?: AlbumsGetAllState.Failed)
    }

    suspend fun createAlbum(
        info: AlbumCreationInfo
    ): AlbumCreationState {
        if (endpoint.isBlank() || auth.asString().isBlank()) return AlbumCreationState.Failed

        val response = client.post(
            url = Url("$endpoint/api/albums"),
            headers = auth.headers,
            body = info
        )?.body<Album>()

        return (response?.let { AlbumCreationState.Created(it) } ?: AlbumCreationState.Failed)
    }

    suspend fun addAssets(
        albumId: Uuid,
        assetIds: List<Uuid>
    ): Boolean {
        if (endpoint.isBlank() || auth.asString().isBlank()) return false

        val response = client.put(
            url = Url("$endpoint/api/albums/${albumId}/assets"),
            headers = auth.headers,
            body = ManageAssetsRequest(ids = assetIds)
        )?.body<List<ManageAssetsResponse>>()

        return response != null && response.all { it.error == null && it.success }
    }

    suspend fun removeAssets(
        albumId: Uuid,
        assetIds: List<Uuid>
    ): Boolean {
        if (endpoint.isBlank() || auth.asString().isBlank()) return false

        val response = client.delete(
            url = Url("$endpoint/api/albums/${albumId}/assets"),
            headers = auth.headers,
            body = ManageAssetsRequest(ids = assetIds)
        )?.body<List<ManageAssetsResponse>>()

        return response != null && response.all { it.error == null && it.success }
    }

    suspend fun get(
        id: Uuid,
        withoutAssets: Boolean = false
    ): Album? {
        if (endpoint.isBlank() || auth.asString().isBlank()) return null

        val response = client.get(
            url = Url("$endpoint/api/albums/${id}?withoutAssets=$withoutAssets"),
            headers = auth.headers,
            body = null
        )?.body<Album>()

        return response
    }

    suspend fun delete(
        id: Uuid
    ): Boolean {
        if (endpoint.isBlank() || auth.asString().isBlank()) return false

        return client.delete(
            url = Url("$endpoint/api/albums/${id}"),
            headers = auth.headers,
            body = null
        ) != null
    }

    suspend fun rename(
        id: Uuid,
        newName: String
    ): Boolean {
        if (endpoint.isBlank() || auth.asString().isBlank()) return false

        return client.patch(
            url = Url("$endpoint/api/albums/${id}"),
            headers = auth.headers,
            body = AlbumRenameRequest(newName)
        ) != null
    }
}