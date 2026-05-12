@file:Suppress("unused")

package io.github.kaii_lb.lavender.immichintegration.clients

import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.serialization.AuthStatus
import io.github.kaii_lb.lavender.immichintegration.serialization.ChangePasswordRequest
import io.github.kaii_lb.lavender.immichintegration.serialization.FullUserResponse
import io.github.kaii_lb.lavender.immichintegration.serialization.LoginCredentials
import io.github.kaii_lb.lavender.immichintegration.serialization.LoginStatus
import io.github.kaii_lb.lavender.immichintegration.serialization.LogoutStatus
import io.github.kaii_lb.lavender.immichintegration.serialization.ProfilePictureResponse
import io.github.kaii_lb.lavender.immichintegration.serialization.ServerPing
import io.github.kaii_lb.lavender.immichintegration.serialization.UserDetails
import io.github.kaii_lb.lavender.immichintegration.serialization.UserResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import kotlinx.serialization.json.Json

internal class LoginClient(
    private val client: ApiClient,
    endpoint: String,
    auth: Auth
) : BaseClient(endpoint, auth) {
    suspend fun ping(
        address: String? = null
    ): Boolean =
        client.get(
            url = Url("${address ?: endpoint}/api/server/ping"),
            headers = null,
            body = null
        )?.body<ServerPing>()?.response == "pong"

    suspend fun login(
        email: String,
        password: String,
        userAgent: String
    ): LoginStatus {
        val response = client.post(
            url = Url("$endpoint/api/auth/login"),
            headers = mapOf(
                HttpHeaders.UserAgent to userAgent
            ),
            body = Json.encodeToString(
                LoginCredentials(email, password)
            )
        )?.body<LoginStatus.LoggedIn>()

        return response ?: LoginStatus.Failed
    }

    suspend fun logout(): Boolean {
        val response = client.post(
            url = Url("$endpoint/api/auth/logout"),
            headers = auth.headers,
            body = null
        )?.body<LogoutStatus>()

        return response?.successful == true
    }

    suspend fun getUsers(): List<UserResponse> {
        val response = client.get(
            url = Url("$endpoint/api/users"),
            headers = auth.headers,
            body = null
        )?.body<List<UserResponse>>()

        return response ?: emptyList()
    }

    suspend fun validate(): Boolean {
        val response = client.post(
            url = Url("$endpoint/api/auth/validateToken"),
            headers = auth.headers,
            body = null
        )?.body<AuthStatus>()

        return response?.valid ?: false
    }

    suspend fun getMe(): FullUserResponse? {
        val response = client.get(
            url = Url("$endpoint/api/users/me"),
            headers = auth.headers,
            body = null
        )?.body<FullUserResponse>()

        return response
    }

    suspend fun uploadPfp(
        bytes: ByteArray,
        filename: String,
    ): ProfilePictureResponse? =
        client.uploadForm(
            url = Url("$endpoint/api/users/profile-image"),
            headers = auth.headers + mapOf(
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
        userId: String
    ): ByteArray? =
        client.get(
            url = Url("$endpoint/api/users/$userId/profile-image"),
            headers = auth.headers + mapOf(
                HttpHeaders.ContentType to ContentType.Application.OctetStream
            ),
            body = null
        )?.body<ByteArray>()

    suspend fun updateInfo(
        name: String? = null,
        email: String? = null
    ): Boolean =
        client.put(
            url = Url("$endpoint/api/users/me"),
            headers = auth.headers,
            body = UserDetails(name = name, email = email)
        )?.status == HttpStatusCode.OK

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        invalidateSessions: Boolean = false
    ): Boolean = client.post(
        url = Url("$endpoint/api/auth/change-password"),
        headers = auth.headers,
        body = ChangePasswordRequest(invalidateSessions, currentPassword, newPassword)
    )?.status == HttpStatusCode.OK
}