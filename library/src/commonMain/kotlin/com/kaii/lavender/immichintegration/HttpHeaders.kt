package com.kaii.lavender.immichintegration

import io.ktor.http.HttpHeaders

val HttpHeaders.XApiKey: String
    get() = "x-api-key"