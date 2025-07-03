package com.kaii.lavender.immichintegration

import com.kaii.lavender.immichintegration.serialization.LoginCredentials
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readText
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
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

        val response = albumManager.getAlbumInfo(
            albumId = "98da30f2-f163-4a22-b960-92d793044402"
        )

        println("Response: $response")
    }

    exitProcess(0)
}