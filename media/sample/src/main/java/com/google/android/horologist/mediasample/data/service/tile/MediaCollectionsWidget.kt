/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.mediasample.data.service.tile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.remote.creation.compose.action.Action
import androidx.compose.remote.creation.compose.action.pendingIntentAction
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteArrangement
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteImage
import androidx.compose.remote.creation.compose.layout.RemotePaddingValues
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.modifier.fillMaxWidth
import androidx.compose.remote.creation.compose.modifier.height
import androidx.compose.remote.creation.compose.modifier.padding
import androidx.compose.remote.creation.compose.modifier.size
import androidx.compose.remote.creation.compose.modifier.width
import androidx.compose.remote.creation.compose.state.RemoteDp
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.remote.creation.compose.text.RemoteTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.glance.wear.GlanceWearWidget
import androidx.glance.wear.WearWidgetBrush
import androidx.glance.wear.WearWidgetData
import androidx.glance.wear.WearWidgetDocument
import androidx.glance.wear.color
import androidx.glance.wear.core.ContainerInfo
import androidx.glance.wear.core.WearWidgetParams
import androidx.wear.compose.remote.material3.RemoteButton
import androidx.wear.compose.remote.material3.RemoteButtonDefaults
import androidx.wear.compose.remote.material3.RemoteMaterialTheme
import androidx.wear.compose.remote.material3.RemoteText
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.mediasample.ui.app.MediaActivity
import kotlinx.coroutines.flow.first

/**
 * A Widget providing link to playlists with responsive breakpoint support.
 */
class MediaCollectionsWidget(
    private val playlistRepository: PlaylistRepository,
    private val imageLoader: ImageLoader,
) : GlanceWearWidget() {

    override suspend fun provideWidgetData(
        context: Context,
        params: WearWidgetParams,
    ): WearWidgetData {
        val playlists = playlistRepository.getAll().first().take(2)
        val firstPlaylist = playlists.firstOrNull()

        if (firstPlaylist == null) {
            return WearWidgetDocument(background = WearWidgetBrush.color(remoteColorScheme.surfaceContainerLow)) {
                RemoteBox(
                    modifier = RemoteModifier.fillMaxSize(),
                    contentAlignment = RemoteAlignment.Center,
                ) {
                    RemoteText(
                        text = "No Playlists".rs,
                        style = RemoteMaterialTheme.typography.titleMedium,
                        color = remoteColorScheme.onPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        val firstPlaylistPendingIntent = createPlaylistPendingIntent(context, 1, firstPlaylist.id)
        val firstPlaylistArtworkBitmap = firstPlaylist.artworkUri?.let { loadArtworkBitmap(context, it) }
        val firstPlaylistArtwork = firstPlaylistArtworkBitmap?.asImageBitmap()

        val secondPlaylist = playlists.getOrNull(1)
        val secondPlaylistsPendingIntent = secondPlaylist?.let { playlist ->
            createPlaylistPendingIntent(context, 2, playlist.id)
        }
        val secondPlaylistArtworkBitmap = secondPlaylist?.artworkUri?.let { loadArtworkBitmap(context, it) }
        val secondPlaylistArtwork = secondPlaylistArtworkBitmap?.asImageBitmap()

        val isLargeContainer = params.containerType == ContainerInfo.CONTAINER_TYPE_LARGE

        return WearWidgetDocument(background = WearWidgetBrush.color(remoteColorScheme.surfaceContainerLow)) {
            WidgetContent(
                playlistName = firstPlaylist.name,
                playlistAction = pendingIntentAction { _ -> firstPlaylistPendingIntent },
                playlistArtwork = firstPlaylistArtwork,
                secondPlaylistName = secondPlaylist?.name,
                secondPlaylistAction = secondPlaylistsPendingIntent?.let { pendingIntentAction { _ -> it } },
                secondPlaylistArtwork = secondPlaylistArtwork,
                heightDp = params.heightDp,
                isLarge = isLargeContainer,
            )
        }
    }

    private suspend fun loadArtworkBitmap(
        context: Context,
        artworkUri: String,
    ): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(artworkUri)
            .size(ARTWORK_SIZE)
            .allowHardware(false)
            .build()
        val result = imageLoader.execute(request)
        return (result.drawable as? BitmapDrawable)?.bitmap
    }

    private fun createPlaylistPendingIntent(
        context: Context,
        requestCode: Int,
        playlistId: String,
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, MediaActivity::class.java).apply {
                putExtra(MediaActivity.CollectionKey, playlistId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    companion object {
        const val WIDGET_HEIGHT_BREAKPOINT_DP = 80f
        const val CONTAINER_TYPE_LARGE = 1
        private const val ARTWORK_SIZE = 48
    }
}

@SuppressLint("RestrictedApi")
@RemoteComposable
@Composable
fun WidgetContent(
  playlistName: String,
  playlistAction: Action,
  playlistArtwork: ImageBitmap?,
  secondPlaylistName: String? = null,
  secondPlaylistAction: Action? = null,
  secondPlaylistArtwork: ImageBitmap? = null,
  heightDp: Float = 0f,
  isLarge: Boolean = (heightDp >= MediaCollectionsWidget.WIDGET_HEIGHT_BREAKPOINT_DP),
) {
    val showSecondPlaylist = isLarge &&
        secondPlaylistName != null &&
        secondPlaylistAction != null

    val containerPadding = if (showSecondPlaylist) 4.rdp else 2.rdp
    val imageSize = if (showSecondPlaylist) 36.rdp else 48.rdp
    val textStyle = if (showSecondPlaylist) RemoteMaterialTheme.typography.titleMedium else RemoteMaterialTheme.typography.titleLarge

    RemoteBox(
        modifier = RemoteModifier.fillMaxSize().padding(containerPadding),
        contentAlignment = RemoteAlignment.Center,
    ) {
        RemoteColumn(
            horizontalAlignment = RemoteAlignment.CenterHorizontally,
            verticalArrangement = RemoteArrangement.spacedBy(2.rdp, RemoteAlignment.CenterVertically),
            modifier = RemoteModifier.fillMaxSize(),
        ) {
            PlaylistButton(
                playlistName = playlistName,
                playlistAction = playlistAction,
                playlistArtwork = playlistArtwork,
                imageSize = imageSize,
                textStyle = textStyle,
                modifier = if (showSecondPlaylist) RemoteModifier.fillMaxWidth().weight(1f) else RemoteModifier.fillMaxSize(),
            )
            if (showSecondPlaylist) {
                PlaylistButton(
                    playlistName = secondPlaylistName,
                    playlistAction = secondPlaylistAction,
                    playlistArtwork = secondPlaylistArtwork,
                    imageSize = imageSize,
                    textStyle = textStyle,
                    modifier = RemoteModifier.fillMaxWidth().weight(1f),
                )
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@RemoteComposable
@Composable
private fun PlaylistButton(
    playlistName: String,
    playlistAction: Action,
    playlistArtwork: ImageBitmap?,
    modifier: RemoteModifier = RemoteModifier,
    imageSize: RemoteDp = 40.rdp,
    textStyle: RemoteTextStyle = RemoteMaterialTheme.typography.titleMedium,
) {
    RemoteButton(
        onClick = playlistAction,
        modifier = modifier.fillMaxWidth(),
        contentPadding = RemotePaddingValues(horizontal = 6.rdp, vertical = 4.rdp),
        colors = RemoteButtonDefaults.buttonColors(
            containerColor = RemoteMaterialTheme.colorScheme.secondaryContainer,
            contentColor = RemoteMaterialTheme.colorScheme.onSecondaryContainer,
        ),
        icon = {
          if (playlistArtwork != null) {
            RemoteImage(
                  bitmap = playlistArtwork,
                  contentDescription = "Artwork".rs,
                  modifier = RemoteModifier.size(imageSize),
            )
          }
        },
    ) {
        RemoteText(
            text = playlistName.rs,
            style = textStyle,
            color = RemoteMaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
