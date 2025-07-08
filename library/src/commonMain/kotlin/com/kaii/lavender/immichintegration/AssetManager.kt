package com.kaii.lavender.immichintegration

import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.append
import io.ktor.utils.io.InternalAPI
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import com.kaii.lavender.immichintegration.serialization.AssetInfo
import com.kaii.lavender.immichintegration.serialization.CheckBulkUpload
import com.kaii.lavender.immichintegration.serialization.CheckBulkUploadAsset
import com.kaii.lavender.immichintegration.serialization.CheckBulkUploadResponse
import com.kaii.lavender.immichintegration.serialization.DeleteAssets
import com.kaii.lavender.immichintegration.serialization.DownloadAssetsZip
import com.kaii.lavender.immichintegration.serialization.DuplicateAsset
import com.kaii.lavender.immichintegration.serialization.ExistingAssets
import com.kaii.lavender.immichintegration.serialization.ExistingAssetsResponse
import com.kaii.lavender.immichintegration.serialization.File
import com.kaii.lavender.immichintegration.serialization.FileVisibility
import com.kaii.lavender.immichintegration.serialization.UpdateAssets
import com.kaii.lavender.immichintegration.serialization.UploadedAssetResponse
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Suppress("unused")
class AssetManager(
    private val apiClient: ApiClient,
    private val endpointBase: String,
    private val bearerToken: String
) {
    /** @param duration in millis */
    @OptIn(ExperimentalTime::class)
    suspend fun uploadAsset(
        file: File,
        deviceId: String,
        visibility: FileVisibility? = null,
        duration: Long? = null,
        livePhotoVideoId: String? = null,
        sidecarData: ByteArray? = null,
        isFavorite: Boolean? = null,
    ): UploadedAssetResponse? {
        val deviceAssetId = "${file.name}-${file.size}".trim()
        val assetData = SystemFileSystem.source(Path(file.path)).buffered().readByteArray()

        val data = formData {
            append("assetData", assetData, Headers.build {
                append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
            })
            append("deviceAssetId", deviceAssetId)
            append("deviceId", deviceId)
            append("fileCreatedAt", Instant.fromEpochMilliseconds(file.dateCreated).toString())
            append("fileModifiedAt", Instant.fromEpochMilliseconds(file.lastModified).toString())
            append("filename", file.name)

            if (isFavorite != null) {
                append("isFavorite", isFavorite.toString())
            }
            if (livePhotoVideoId != null) {
                append("livePhotoVideoId", livePhotoVideoId.toString())
            }
            if (sidecarData != null) {
                append("sidecarData", sidecarData.toString())
            }
            if (visibility != null) {
                append("visibility", visibility.name.length)
            }
            if (duration != null) {
                val formatted =
                    duration.milliseconds.toComponents { hours, minutes, seconds, nanos ->
                        "${hours}:${minutes}:${seconds}.${nanos}"
                    }
                append("duration", formatted)
            }
        }

        val response = apiClient.post(
            url = Url("$endpointBase/api/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.MultiPart.FormData,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = MultiPartFormDataContent(
                parts = data
            )
        )

        return response?.body<UploadedAssetResponse>()
    }

    @OptIn(InternalAPI::class)
    suspend fun deleteAssets(
        assets: DeleteAssets
    ): String? {
        val response = apiClient.delete(
            url = Url("$endpointBase/api/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Any,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = assets
        )

        return response?.toString()
    }

    suspend fun updateAssets(
        assets: UpdateAssets
    ): String? {
        val response = apiClient.put(
            url = Url("$endpointBase/api/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = assets
        )

        return response?.toString()
    }

    suspend fun downloadAsset(
        id: String
    ): ByteArray? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/assets/$id/original"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.bodyAsBytes()
    }

    @OptIn(ExperimentalTime::class)
    suspend fun replaceAsset(
        id: String,
        file: File,
        deviceId: String,
        duration: Int? = null
    ): UploadedAssetResponse? {
        val deviceAssetId = "${file.name}-${file.size}".trim()
        val assetData = SystemFileSystem.source(Path(file.path)).buffered().readByteArray()

        val data = formData {
            append("assetData", assetData, Headers.build {
                append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
            })
            append("deviceAssetId", deviceAssetId)
            append("deviceId", deviceId)
            append("fileCreatedAt", Instant.fromEpochMilliseconds(file.dateCreated).toString())
            append("fileModifiedAt", Instant.fromEpochMilliseconds(file.lastModified).toString())
            append("filename", file.name)

            if (duration != null) {
                append("duration", duration.toString())
            }
        }

        val response = apiClient.put(
            url = Url("$endpointBase/api/assets/$id/original"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.MultiPart.FormData,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = MultiPartFormDataContent(
                parts = data
            )
        )

        return response?.body<UploadedAssetResponse>()
    }

    suspend fun getAssetInfo(
        id: String
    ): AssetInfo? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/assets/$id"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.MultiPart.FormData,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<AssetInfo>()
    }

    suspend fun getExistingAssets(
        assets: ExistingAssets
    ): ExistingAssetsResponse? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/assets/exist"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = assets
        )

        return response?.body<ExistingAssetsResponse>()
    }

    suspend fun downloadZipArchive(
        assets: DownloadAssetsZip
    ): ByteArray? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/download/archive"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = assets
        )

        return response?.bodyAsBytes()
    }

    suspend fun getDuplicateAssets() : List<DuplicateAsset>? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/duplicates"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<List<DuplicateAsset>>()
    }

    suspend fun checkBulkUpload(
        assets: CheckBulkUpload
    ): CheckBulkUploadResponse? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/assets/bulk-upload-check"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = assets
        )

        return response?.body<CheckBulkUploadResponse>()
    }
}