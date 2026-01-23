package com.kaii.lavender.immichintegration.clients

import com.kaii.lavender.immichintegration.serialization.AuthStatus
import com.kaii.lavender.immichintegration.serialization.LoginCredentials
import com.kaii.lavender.immichintegration.serialization.LoginStatus
import com.kaii.lavender.immichintegration.serialization.LogoutStatus
import com.kaii.lavender.immichintegration.serialization.UserResponse
import io.ktor.client.call.body
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.serialization.json.Json

class LoginClient(
    private val baseUrl: String,
    private val client: ApiClient
) {
    suspend fun login(
        email: String,
        password: String
    ): LoginStatus {
        val response = client.post(
            url = Url("$baseUrl/api/auth/login"),
            headers = null,
            body = Json.encodeToString(
                LoginCredentials(email, password)
            )
        )?.body<LoginStatus.LoggedIn>()

        return response ?: LoginStatus.Failed
    }

    suspend fun logout(accessToken: String): Boolean {
        val response = client.post(
            url = Url("$baseUrl/api/auth/login"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<LogoutStatus>()

        return response?.successful ?: false
    }

    suspend fun getUsers(accessToken: String): List<UserResponse> {
        val response = client.post(
            url = Url("$baseUrl/api/auth/validateToken"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<List<UserResponse>>()

        return response ?: emptyList()
    }

    suspend fun validate(accessToken: String): Boolean {
        val response = client.post(
            url = Url("$baseUrl/api/auth/validateToken"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<AuthStatus>()

        return response?.valid ?: false
    }

    suspend fun getMe(accessToken: String): UserResponse? {
        val response = client.get(
            url = Url("$baseUrl/api/users/me"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<UserResponse>()

        return response
    }
}