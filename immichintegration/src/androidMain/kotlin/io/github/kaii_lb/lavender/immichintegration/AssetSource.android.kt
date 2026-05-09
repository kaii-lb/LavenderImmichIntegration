package io.github.kaii_lb.lavender.immichintegration

import android.content.Context
import android.net.Uri
import io.ktor.client.request.forms.InputProvider
import io.ktor.utils.io.streams.asInput

class UriAssetSource(
    private val context: Context,
    private val uri: Uri
) : AssetSource {
    override fun getStream(): InputProvider = InputProvider {
        val inputStream =
            context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Cannot open URI")

        inputStream.asInput()
    }
}