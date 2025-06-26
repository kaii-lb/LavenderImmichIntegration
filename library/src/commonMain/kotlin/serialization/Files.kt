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
    ARCHIVE,
    TIMELINE,
    HIDDEN,
    LOCKED
}

enum class UploadStatus {
    CREATED,
    REPLACED,
    DUPLICATE
}