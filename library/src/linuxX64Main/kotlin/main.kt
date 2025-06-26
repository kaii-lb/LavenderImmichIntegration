import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import serialization.LoginCredentials
import serialization.LoginResponse

fun main() {
    val apiClient = ApiClient()
    val userAuth = UserAuth(
        apiClient = apiClient,
        endpointBase = "https://immich.selyn.pet"
    )

    print("Enter token: ")
    val token = readln()
    // print("Enter password: ")
    // val password = readln()
    println()

    runBlocking {
        val response = userAuth.logout(
            bearerToken = token
        )

        println(response.toString())
    }
}