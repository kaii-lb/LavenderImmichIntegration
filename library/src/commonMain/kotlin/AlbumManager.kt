import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import serialization.AddAssetResponse
import serialization.Album
import serialization.CreateAlbum
import serialization.UploadAssetToAlbum
import serialization.UserMini

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
        assets: UploadAssetToAlbum
    ): List<AddAssetResponse>? {
        val response = apiClient.put(
            url = Url("$endpointBase/api/albums/${albumId}/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = assets
        )

        return response?.body<List<AddAssetResponse>>()
    }
}