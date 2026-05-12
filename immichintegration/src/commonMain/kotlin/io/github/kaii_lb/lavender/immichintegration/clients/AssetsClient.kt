package io.github.kaii_lb.lavender.immichintegration.clients

import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetBulkUploadCheckResult
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetBulkUploadRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetBulkUploadResponse
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetDeleteRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetFavouriteRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetResponse
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetRestoreRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetRestoreResponse
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetUploadRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.assets.AssetUploadResponse
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AssetsClient(
    endpoint: String,
    auth: Auth,
    private val client: ApiClient
) : BaseClient(endpoint, auth) {
    suspend fun upload(
        asset: AssetUploadRequest
    ): AssetUploadResponse? {
        val response = client.post(
            url = Url("$endpoint/api/assets"),
            headers = auth.headers + mapOf(
                HttpHeaders.ContentType to ContentType.MultiPart.FormData
            ),
            body = asset.toFormData()
        )?.body<AssetUploadResponse>()

        return response
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(
        ids: List<Uuid>,
        force: Boolean = false
    ): Boolean {
        return client.delete(
            url = Url("$endpoint/api/assets"),
            headers = auth.headers,
            body = AssetDeleteRequest(ids, force)
        )?.status == HttpStatusCode.NoContent
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun get(
        id: Uuid
    ): AssetResponse? {
        val response = client.get(
            url = Url("$endpoint/api/assets/${id}"),
            headers = auth.headers,
            body = null
        )?.body<AssetResponse>()

        return response
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun download(
        id: Uuid
    ): ByteArray? {
        val response = client.get(
            url = Url("$endpoint/api/assets/${id}/original"),
            headers = auth.headers,
            body = null
        )?.body<ByteArray>()

        return response
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun check(
        assets: AssetBulkUploadRequest
    ): List<AssetBulkUploadCheckResult>? {
        val response = client.post(
            url = Url("$endpoint/api/assets/bulk-upload-check"),
            headers = auth.headers,
            body = assets
        )?.body<AssetBulkUploadResponse>()

        return response?.results
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun favourite(
        request: AssetFavouriteRequest
    ): Boolean {
        val response = client.put(
            url = Url("$endpoint/api/assets"),
            headers = auth.headers,
            body = request
        )?.status

        return response == HttpStatusCode.NoContent
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun restore(
        ids: List<Uuid>
    ): Int? {
        val response = client.post(
            url = Url("$endpoint/api/trash/restore/assets"),
            headers = auth.headers,
            body = AssetRestoreRequest(ids)
        )?.body<AssetRestoreResponse>()

        return response?.count
    }
}