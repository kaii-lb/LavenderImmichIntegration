package com.kaii.lavender.immichintegration.clients

import com.kaii.lavender.immichintegration.serialization.assets.AssetBulkUploadCheckResult
import com.kaii.lavender.immichintegration.serialization.assets.AssetBulkUploadRequest
import com.kaii.lavender.immichintegration.serialization.assets.AssetBulkUploadResponse
import com.kaii.lavender.immichintegration.serialization.assets.AssetDeleteRequest
import com.kaii.lavender.immichintegration.serialization.assets.AssetResponse
import com.kaii.lavender.immichintegration.serialization.assets.AssetUploadRequest
import com.kaii.lavender.immichintegration.serialization.assets.AssetUploadResponse
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class AssetsClient(
    private val baseUrl: String,
    private val client: ApiClient
) {
    suspend fun upload(
        asset: AssetUploadRequest,
        accessToken: String
    ): AssetUploadResponse? {
        val response = client.post(
            url = Url("$baseUrl/api/assets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken",
                HttpHeaders.ContentType to ContentType.MultiPart.FormData
            ),
            body = asset.toFormData()
        )?.body<AssetUploadResponse>()

        return response
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(
        ids: List<Uuid>,
        accessToken: String,
        force: Boolean = false
    ): Boolean {
        return client.delete(
            url = Url("$baseUrl/api/assets"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = AssetDeleteRequest(ids, force)
        )?.status == HttpStatusCode.NoContent
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun get(
        id: Uuid,
        accessToken: String
    ): AssetResponse? {
        val response = client.get(
            url = Url("$baseUrl/api/assets/${id}"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<AssetResponse>()

        return response
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun download(
        id: Uuid,
        accessToken: String
    ): ByteArray? {
        val response = client.get(
            url = Url("$baseUrl/api/assets/${id}/original"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<ByteArray>()

        return response
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun check(
        assets: AssetBulkUploadRequest,
        accessToken: String
    ): List<AssetBulkUploadCheckResult>? {
        val response = client.get(
            url = Url("$baseUrl/api/assets/bulk-upload-check"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = assets
        )?.body<AssetBulkUploadResponse>()

        return response?.assets
    }
}