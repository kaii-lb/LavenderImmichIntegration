import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.append
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import serialization.DeleteAssets
import serialization.File
import serialization.FileVisibility
import serialization.UpdateAssets
import serialization.UploadedAssetResponse
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class AssetManager(
    private val apiClient: ApiClient,
    private val endpointBase: String,
    private val bearerToken: String
) {
    @OptIn(ExperimentalTime::class)
    suspend fun uploadAsset(
        file: File,
        deviceId: String,
        visibility: FileVisibility? = null,
        duration: Int? = null,
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
                append("duration", duration.toString())
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

    suspend fun deleteAssets(
        assets: DeleteAssets
    ): String? {
        val response = apiClient.delete(
            url = Url("$endpointBase/api/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
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
}