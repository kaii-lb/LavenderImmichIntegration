package com.kaii.lavender.immichintegration

import com.kaii.lavender.immichintegration.serialization.CreateProfilePicResponse
import com.kaii.lavender.immichintegration.serialization.File
import com.kaii.lavender.immichintegration.serialization.UpdateUserInfo
import com.kaii.lavender.immichintegration.serialization.UserFull
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.append
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

@Suppress("unused")
class UserManager(
    private val apiClient: ApiClient,
    private val endpointBase: String,
    private val bearerToken: String
) {
    suspend fun createProfilePic(
        file: File
    ): CreateProfilePicResponse? {
        val assetData = SystemFileSystem.source(Path(file.path)).buffered().readByteArray()

        val data = formData {
            append("file", assetData, Headers.build {
                append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
            })
        }

        val response = apiClient.post(
            url = Url("$endpointBase/api/users/profile-image"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.MultiPart.FormData,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = MultiPartFormDataContent(
                data
            )
        )

        return response?.body<CreateProfilePicResponse>()
    }

    suspend fun getProfilePic(
        userId: String
    ): ByteArray? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/users/${userId}/profile-image"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<ByteArray>()
    }

    suspend fun getMyUser(): UserFull? {
        val response = apiClient.get(
            url = Url("$endpointBase/api/users/me"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<UserFull>()
    }

    suspend fun changeName(
        newName: String
    ): UserFull? {
        val info = getMyUser()

        if (info == null) {
            return null
        }

        val response = apiClient.put(
            url = Url("$endpointBase/api/users/me"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Application.Json,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = UpdateUserInfo(
                name = newName,
                email = info.email
            )
        )

        return response?.body<UserFull>()
    }
}