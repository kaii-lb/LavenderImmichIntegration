package com.kaii.lavender.immichintegration.state_managers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastMap
import com.kaii.lavender.immichintegration.clients.AlbumsClient
import com.kaii.lavender.immichintegration.clients.ApiClient
import com.kaii.lavender.immichintegration.clients.AssetsClient
import com.kaii.lavender.immichintegration.serialization.albums.AlbumCreationInfo
import com.kaii.lavender.immichintegration.serialization.albums.AlbumCreationState
import com.kaii.lavender.immichintegration.serialization.albums.AlbumGetState
import com.kaii.lavender.immichintegration.serialization.albums.AlbumUserCreationInfo
import com.kaii.lavender.immichintegration.serialization.albums.AlbumUserRole
import com.kaii.lavender.immichintegration.serialization.assets.AssetBulkUploadCheckItem
import com.kaii.lavender.immichintegration.serialization.assets.AssetBulkUploadRequest
import com.kaii.lavender.immichintegration.serialization.assets.AssetUploadRequest
import com.kaii.lavender.immichintegration.serialization.assets.UploadAssetRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import java.io.File
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AlbumsStateManager(
    private val baseUrl: String,
    private val coroutineScope: CoroutineScope,
    apiClient: ApiClient
) {
    private val albumClient =
        AlbumsClient(
            baseUrl = baseUrl,
            client = apiClient
        )

    private val assetClient =
        AssetsClient(
            baseUrl = baseUrl,
            client = apiClient
        )

    @OptIn(ExperimentalUuidApi::class)
    suspend fun create(
        name: String,
        description: String,
        userId: Uuid,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        val created = albumClient.createAlbum(
            info = AlbumCreationInfo(
                albumName = name,
                albumUsers = AlbumUserCreationInfo(
                    role = AlbumUserRole.Editor,
                    userId = userId
                ),
                assetIds = emptyList(),
                description = description
            ),
            accessToken = accessToken
        )

        if (created is AlbumCreationState.Created) {
            created.album
        } else {
            null
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(
        id: Uuid,
        accessToken: String,
        onResult: (deleted: Boolean) -> Unit
    ) = coroutineScope.launch(Dispatchers.IO) {
        onResult(albumClient.delete(id = id, accessToken = accessToken))
    }

    @OptIn(ExperimentalUuidApi::class)
    fun addAssets(
        id: Uuid,
        accessToken: String,
        assetIds: List<UploadAssetRequest>,
        deviceId: String,
        onItemDone: () -> Unit,
        onResult: (added: Boolean) -> Unit
    ) = coroutineScope.launch(Dispatchers.IO) {
        val exists = assetClient.check(
            assets = AssetBulkUploadRequest(
                assetIds.map { item ->
                    val checksum = item.filename // TODO

                    AssetBulkUploadCheckItem(
                        checksum = checksum,
                        id = item.id.toString()
                    )
                }
            ),
            accessToken = accessToken
        )?.map {
            it.id
        } ?: emptyList()

        val missing = assetIds.fastFilter { it.id.toString() !in exists }

        repeat(exists.size) { // skip already existing data
            onItemDone()
        }

        missing.forEach { item ->
            val file = File(item.absolutePath)
            val assetData = file.readBytes() // TODO: buffer

            val resp = assetClient.upload(
                AssetUploadRequest(
                    assetData = assetData,
                    deviceAssetId = "${item.filename}-${item.size}",
                    deviceId = deviceId,
                    fileCreatedAt = Instant.fromEpochSeconds(item.dateTaken).format(DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET),
                    fileModifiedAt = Instant.fromEpochSeconds(item.dateModified).format(DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET),
                    metadata = emptyList(),
                    filename = item.filename
                ),
                accessToken = accessToken
            )

            if (resp != null) {
                onItemDone()
            }
        }

        onResult(
            albumClient.addAssetsToAlbums(
                albumIds = listOf(id),
                assetIds = assetIds.fastMap {
                    it.id
                },
                accessToken = accessToken
            )
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    fun downloadAssets(
        id: Uuid,
        accessToken: String,
        outputPath: String,
        onSetupDone: (itemCount: Int) -> Unit,
        onItemDone: () -> Unit,
        onFailed: () -> Unit
    ) = coroutineScope.launch(Dispatchers.IO) {
        val state = albumClient.get(id = id, accessToken = accessToken)

        if (state is AlbumGetState.Retrieved) {
            onSetupDone(state.album.assets.size)

            // TODO: buffer
            state.album.assets.forEach { asset ->
                val downloaded = assetClient.download(id = Uuid.parse(asset.id), accessToken = accessToken)

                if (downloaded != null) {
                    val file = File(outputPath, asset.originalFileName)
                    if (!file.exists()) file.createNewFile()

                    file.writeBytes(downloaded)

                    onItemDone()
                }
            }
        } else {
            onFailed()
        }
    }
}

@Composable
fun rememberAlbumState(baseUrl: String): AlbumsStateManager {
    val apiClient = LocalApiClient.current
    val coroutineScope = rememberCoroutineScope()

    return remember(baseUrl) {
        AlbumsStateManager(
            baseUrl = baseUrl,
            coroutineScope = coroutineScope,
            apiClient = apiClient
        )
    }
}