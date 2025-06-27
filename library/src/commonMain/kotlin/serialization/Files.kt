package serialization

import kotlinx.io.files.SystemPathSeparator

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

enum class FileVisibility {
    archive,
    timeline,
    hidden,
    locked
}

enum class UploadStatus {
    created,
    replaced,
    duplicate
}