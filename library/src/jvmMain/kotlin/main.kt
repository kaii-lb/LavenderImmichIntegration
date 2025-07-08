package com.kaii.lavender.immichintegration

import com.kaii.lavender.immichintegration.serialization.CheckBulkUpload
import com.kaii.lavender.immichintegration.serialization.CheckBulkUploadAsset
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
    val userAuth = UserAuth(
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
        val albumManager = AssetManager(
            apiClient = apiClient,
            endpointBase = "https://immich.selyn.pet",
            bearerToken = token
        )

        val response = albumManager.checkBulkUpload(
            assets = CheckBulkUpload(
                assets = listOf(CheckBulkUploadAsset(
                    checksum = "9ca32c4615e9465ffa6bf637ff40502f0402341a",
                    filename = "95cea8ae3175df3382c3584e01101ab4 (1).jpg"
                ))
            )
        )

        println("Response: $response")
    }

    exitProcess(0)
}