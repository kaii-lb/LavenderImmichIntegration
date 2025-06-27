import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readText
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import serialization.DownloadAssetsZip
import serialization.LoginCredentials

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
        val assetManager = AssetManager(
            apiClient = apiClient,
            endpointBase = "https://immich.selyn.pet",
            bearerToken = token
        )

        val response = assetManager.downloadZipArchive(
            assets = DownloadAssetsZip(
                assetIds = listOf("654e22a1-2e43-4601-8d2c-86897b0b53c1")
            )
        )

        SystemFileSystem.sink(path = Path("/home/kaii/out.zip")).buffered().apply {
            if (response != null) {
                write(response)
                flush()
                close()
            }
        }
    }
}