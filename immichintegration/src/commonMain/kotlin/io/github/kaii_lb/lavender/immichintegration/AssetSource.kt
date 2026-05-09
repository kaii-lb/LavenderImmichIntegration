package io.github.kaii_lb.lavender.immichintegration

import io.ktor.client.request.forms.InputProvider

interface AssetSource {
    fun getStream(): InputProvider
}