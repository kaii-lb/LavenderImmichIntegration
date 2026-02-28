package io.github.kaii_lb.lavender.immichintegration.state_managers

import androidx.compose.runtime.compositionLocalOf
import io.github.kaii_lb.lavender.immichintegration.clients.ApiClient

val LocalApiClient = compositionLocalOf<ApiClient> {
    throw IllegalStateException("CompositionLocal LocalApiClient not present")
}