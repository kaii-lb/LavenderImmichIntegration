package io.github.kaii_lb.lavender.immichintegration

import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

@Serializable
sealed interface Auth {
    @Serializable
    data class AccessToken(
        val accessToken: String
    ) : Auth

    @Serializable
    data class ApiKey(
        val apiKey: String
    ) : Auth

    @Serializable
    object None : Auth

    val headers: Map<String, String>
        get() =
            when (this) {
                is AccessToken -> {
                    mapOf(
                        HttpHeaders.Authorization to "Bearer $accessToken"
                    )
                }

                is ApiKey -> {
                    mapOf(
                        HttpHeaders.XApiKey to apiKey
                    )
                }

                else -> {
                    emptyMap()
                }
            }

    /** returns the accessToken or apiKey if this auth is either,
     *  otherwise returns an empty string */
    @Suppress("unused")
    fun asString() = when (this) {
        is AccessToken -> accessToken
        is ApiKey -> apiKey
        None -> ""
    }
}