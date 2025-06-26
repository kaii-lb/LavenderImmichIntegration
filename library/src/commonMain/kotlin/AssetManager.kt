import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.source
import kotlinx.io.readByteString
import serialization.File
import serialization.FileVisibility
import serialization.UploadedAssetResponse
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration

class AssetManager(
    private val apiClient: ApiClient,
    private val endpointBase: String
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
        val assetData = SystemFileSystem.source(Path(file.path)).buffered().readByteString()

        val body = mutableMapOf(
            "assetData" to assetData.toString(),
            "deviceAssetId" to deviceAssetId,
            "deviceId" to deviceId,
            "fileCreatedAt" to Instant.fromEpochMilliseconds(file.dateCreated).toString(),
            "fileModifiedAt" to Instant.fromEpochMilliseconds(file.lastModified).toString(),
            "filename" to file.name
        )

        if (isFavorite != null) {
            body["isFavorite"] = isFavorite.toString()
        }
        if (livePhotoVideoId != null) {
            body["livePhotoVideoId"] = livePhotoVideoId
        }
        if (sidecarData != null) {
            body["sidecarData"] = sidecarData.toString()
        }
        if (visibility != null) {
            body["visibility"] = visibility.name.lowercase()
        }
        if (duration != null) {
            body["duration"] = duration.seconds.toIsoString()
        }

        val response = apiClient.post(
            url = Url("$endpointBase/api/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.MultiPart.FormData,
            ),
            body = body
        )

        return response?.body<UploadedAssetResponse>()
    }
}