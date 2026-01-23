package com.kaii.lavender.immichintegration.state_managers

import androidx.compose.runtime.compositionLocalOf
import com.kaii.lavender.immichintegration.clients.ApiClient

val LocalApiClient = compositionLocalOf<ApiClient> {
    throw IllegalStateException("CompositionLocal LocalApiClient not present")
}