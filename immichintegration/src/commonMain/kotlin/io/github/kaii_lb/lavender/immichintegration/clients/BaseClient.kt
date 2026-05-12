package io.github.kaii_lb.lavender.immichintegration.clients

import io.github.kaii_lb.lavender.immichintegration.Auth
import kotlin.jvm.JvmName

open class BaseClient(
    @get:JvmName("server_endpoint")
    @set:JvmName("server_endpoint")
    protected var endpoint: String,

    @get:JvmName("server_auth")
    @set:JvmName("server_auth")
    protected var auth: Auth
) {
    fun setEndpoint(endpoint: String) {
        this.endpoint = endpoint
    }

    fun setAuth(auth: Auth) {
        this.auth = auth
    }

    internal fun getEndpoint() = endpoint
    internal fun getAuth() = auth
}