import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readText
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import okhttp3.internal.toImmutableList
import serialization.Album
import serialization.CreateAlbum
import serialization.DeleteAssets
import serialization.DownloadAssetsZip
import serialization.LoginCredentials
import serialization.UploadAssetToAlbum
import serialization.UserMini
import serialization.UserRole
import kotlin.system.exitProcess

fun main() {
    val apiClient = ApiClient()
    val userAuth = User(
        apiClient = apiClient,
        endpointBase = "https://immich.selyn.pet"
    )

    val path = Path("/home/kaii/bearertoken.txt")
    val token = if (!SystemFileSystem.exists(path)) {
        print("Enter email: ")
        val email = readln()
        print("Enter password: ")
        val password = readln()
        println()

        runBlocking {
            val response = userAuth.login(
                credentials = LoginCredentials(
                    email = email,
                    password = password
                )
            )

            val accessToken = response?.accessToken

            if (accessToken != null) {
                SystemFileSystem.sink(path = path, append = false)
                    .buffered().apply {
                        write(accessToken.toByteArray())
                        flush()
                        close()
                    }

                println("Your access token is: $accessToken")
                accessToken.trim()
            } else throw IllegalStateException("Null access token!")
        }
    } else {
        SystemFileSystem.source(path).buffered().readText().trim()
    }

    runBlocking {
        val albumManager = AlbumManager(
            apiClient = apiClient,
            endpointBase = "https://immich.selyn.pet",
            bearerToken = token
        )

        val response = albumManager.addAssetToAlbum(
            albumId = "008602f0-740b-456c-96fc-69ec5e0b1d0b",
            assets = UploadAssetToAlbum(
                ids = listOf("fc55e1d6-5ba0-4101-8f1c-faec14af8e83")
            )
        )

        println("Response: $response")
    }

    exitProcess(0)
}