package com.kaii.lavender.immichintegration.serialization

import kotlinx.io.files.SystemPathSeparator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** @param dateCreated in milliseconds since epoch
 * @param lastModified in milliseconds since epoch */
data class File(
    val path: String,
    val size: Long,
    val dateCreated: Long,
    val lastModified: Long,
) {
    val name = path.split(SystemPathSeparator).last()
}

@Suppress("unused")
enum class FileVisibility {
    @SerialName("archive")
    Archive,
    @SerialName("timeline")
    Timeline,
    @SerialName("hidden")
    Hidden,
    @SerialName("locked")
    Locked
}

@Suppress("unused")
enum class UploadStatus {
    @SerialName("created")
    Created,
    @SerialName("replaced")
    Replaced,
    @SerialName("duplicate")
    Duplicate
}

@Serializable
data class RestoreFromTrash(
    val ids: List<String>
)

@Serializable
data class ModifyTrashResponse(
    val count: Int
)