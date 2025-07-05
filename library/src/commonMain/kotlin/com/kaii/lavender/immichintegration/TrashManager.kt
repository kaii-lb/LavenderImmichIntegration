package com.kaii.lavender.immichintegration

import com.kaii.lavender.immichintegration.serialization.ModifyTrashResponse
import com.kaii.lavender.immichintegration.serialization.RestoreFromTrash
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url

@Suppress("unused")
class TrashManager(
    private val apiClient: ApiClient,
    private val endpointBase: String,
    private val bearerToken: String
) {
    suspend fun restoreItems(
        ids: RestoreFromTrash
    ): ModifyTrashResponse? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/trash/restore/assets"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Any,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ids
        )

        return response?.body<ModifyTrashResponse>()
    }

    suspend fun emptyTrash(): ModifyTrashResponse? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/trash/empty"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Any,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<ModifyTrashResponse>()
    }

    suspend fun restoreTrash(): ModifyTrashResponse? {
        val response = apiClient.post(
            url = Url("$endpointBase/api/trash/restore"),
            headers = mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json,
                HttpHeaders.Accept to ContentType.Any,
                HttpHeaders.Authorization to "Bearer $bearerToken"
            ),
            body = ""
        )

        return response?.body<ModifyTrashResponse>()
    }
}