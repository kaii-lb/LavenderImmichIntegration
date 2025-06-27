import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import serialization.LoginCredentials
import serialization.LoginResponse
import serialization.LogoutResponse

@Suppress("unused")
class User(
    private val apiClient: ApiClient,
    private val endpointBase: String
) {
    suspend fun login(
        credentials: LoginCredentials
    ): LoginResponse? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/auth/login"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json
            ),
            body = credentials
        )

        return response?.body<LoginResponse>()
    }

    suspend fun logout(
        bearerToken: String
    ): LogoutResponse? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/auth/logout"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<LogoutResponse>()
    }
}