package com.kaii.lavender.immichintegration.clients

import com.kaii.lavender.immichintegration.serialization.AuthStatus
import com.kaii.lavender.immichintegration.serialization.LoginCredentials
import com.kaii.lavender.immichintegration.serialization.LoginStatus
import com.kaii.lavender.immichintegration.serialization.LogoutStatus
import com.kaii.lavender.immichintegration.serialization.ProfilePictureResponse
import com.kaii.lavender.immichintegration.serialization.UserDetails
import com.kaii.lavender.immichintegration.serialization.UserResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import kotlinx.serialization.json.Json

class LoginClient(
    private val baseUrl: String,
    private val client: ApiClient
) {
    suspend fun login(
        email: String,
        password: String,
        userAgent: String
    ): LoginStatus {
        val response = client.post(
            url = Url("$baseUrl/api/auth/login"),
            headers = mapOf(
                HttpHeaders.UserAgent to userAgent
            ),
            body = Json.encodeToString(
                LoginCredentials(email, password)
            )
        )?.body<LoginStatus.LoggedIn>()

        return response ?: LoginStatus.Failed
    }

    suspend fun logout(accessToken: String): Boolean {
        val response = client.post(
            url = Url("$baseUrl/api/auth/logout"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = null
        )?.body<LogoutStatus>()

        return response?.successful == true
    }

    suspend fun getUsers(accessToken: String): List<UserResponse> {
        val response = client.get(
            url = Url("$baseUrl/api/users"),
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

    suspend fun uploadPfp(
        bytes: ByteArray,
        filename: String,
        accessToken: String
    ): ProfilePictureResponse? =
        client.uploadForm(
            url = Url("$baseUrl/api/users/profile-image"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken",
                HttpHeaders.ContentType to ContentType.MultiPart.FormData,
                HttpHeaders.ContentDisposition to "filename=\"${filename}\""
            ),
            formData = formData {
                append("file", bytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"${filename}\"")
                })
            }
        )?.body<ProfilePictureResponse>()


    suspend fun downloadPfp(
        userId: String,
        accessToken: String
    ): ByteArray? =
        client.get(
            url = Url("$baseUrl/api/users/$userId/profile-image"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken",
                HttpHeaders.ContentType to ContentType.Application.OctetStream
            ),
            body = null
        )?.body<ByteArray>()

    suspend fun updateUsername(
        name: String,
        accessToken: String
    ): Boolean =
        client.put(
            url = Url("$baseUrl/api/users/me"),
            headers = mapOf(
                HttpHeaders.Authorization to "Bearer $accessToken"
            ),
            body = UserDetails(name = name)
        )?.status == HttpStatusCode.OK
}