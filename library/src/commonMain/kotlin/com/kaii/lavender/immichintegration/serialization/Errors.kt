package com.kaii.lavender.immichintegration.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BulkIdErrorReason {
    @SerialName("duplicate")
    Duplicate,

    @SerialName("no_permission")
    NoPermission,

    @SerialName("not_found")
    NotFound,

    @SerialName("unknown")
    Unknown,
}