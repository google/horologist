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
import androidx.compose.remote.creation.compose.modifier.padding
import androidx.compose.remote.creation.compose.modifier.size
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.RemoteDp
import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.remote.creation.compose.text.RemoteTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.glance.wear.GlanceWearWidget
import androidx.glance.wear.WearWidgetBrush
import androidx.glance.wear.WearWidgetData
import androidx.glance.wear.WearWidgetDocument
import androidx.glance.wear.color
import androidx.glance.wear.core.ContainerInfo
import androidx.glance.wear.core.WearWidgetParams
import androidx.glance.wear.tooling.preview.RectangularAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.RoundAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.SquircleAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.WearWidgetPreview
import androidx.palette.graphics.Palette
import androidx.wear.compose.remote.material3.RemoteButton
import androidx.wear.compose.remote.material3.RemoteButtonDefaults
import androidx.wear.compose.remote.material3.RemoteMaterialTheme
import androidx.wear.compose.remote.material3.RemoteText
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.horologist.images.coil.FakeImageLoader
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.app.MediaActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

/** A Widget providing link to playlists with responsive breakpoint support. */
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
      return WearWidgetDocument(
        background = WearWidgetBrush.color(remoteColorScheme.surfaceContainerLow)
      ) {
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

    val firstPlaylistArtworkBitmapAndColor =
      firstPlaylist.artworkUri?.let { loadArtworkBitmap(context, it) }
    val firstPlaylistArtwork = firstPlaylistArtworkBitmapAndColor?.first?.asImageBitmap()
    val firstPlaylistColor = firstPlaylistArtworkBitmapAndColor?.second

    val secondPlaylist = playlists.getOrNull(1)
    val secondPlaylistArtworkBitmapAndColor =
      secondPlaylist?.artworkUri?.let { loadArtworkBitmap(context, it) }
    val secondPlaylistArtwork = secondPlaylistArtworkBitmapAndColor?.first?.asImageBitmap()
    val secondPlaylistColor = secondPlaylistArtworkBitmapAndColor?.second

    val isLargeContainer = params.containerType == ContainerInfo.CONTAINER_TYPE_LARGE

    return WearWidgetDocument(
      background = WearWidgetBrush.color(remoteColorScheme.surfaceContainerLow)
    ) {
      WidgetContent(
        playlistName = firstPlaylist.name,
        playlistAction =
          pendingIntentAction { ctx -> createPlaylistPendingIntent(ctx, 1, firstPlaylist.id) },
        playlistArtwork = firstPlaylistArtwork,
        playlistColor = firstPlaylistColor,
        secondPlaylistName = secondPlaylist?.name,
        secondPlaylistAction =
          secondPlaylist?.let { playlist ->
            pendingIntentAction { ctx -> createPlaylistPendingIntent(ctx, 2, playlist.id) }
          },
        secondPlaylistArtwork = secondPlaylistArtwork,
        secondPlaylistColor = secondPlaylistColor,
        heightDp = params.heightDp,
        isLarge = isLargeContainer,
      )
    }
  }

  private suspend fun loadArtworkBitmap(context: Context, artworkUri: String): Pair<Bitmap?, Color?> {
    val request =
      ImageRequest.Builder(context).data(artworkUri).size(ARTWORK_SIZE).allowHardware(false).build()
    val result = imageLoader.execute(request)
    val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
    val color = bitmap?.let { bmp ->
      val palette = Palette.from(bmp).generate()
      val extractedInt = palette.getDominantColor(android.graphics.Color.TRANSPARENT)
      if (extractedInt != android.graphics.Color.TRANSPARENT) Color(extractedInt) else null
    }
    return Pair(bitmap, color)
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
  playlistColor: Color?,
  secondPlaylistName: String? = null,
  secondPlaylistAction: Action? = null,
  secondPlaylistArtwork: ImageBitmap? = null,
  secondPlaylistColor: Color? = null,
  heightDp: Float = 0f,
  isLarge: Boolean = (heightDp >= MediaCollectionsWidget.WIDGET_HEIGHT_BREAKPOINT_DP),
) {
  val showSecondPlaylist = isLarge && secondPlaylistName != null && secondPlaylistAction != null

  val containerPadding = if (showSecondPlaylist) 4.rdp else 2.rdp
  // Use a fixed 36.rdp image size so it neatly fits inside the pill vertically
  val imageSize = 36.rdp
  val textStyle = RemoteMaterialTheme.typography.titleMedium

  RemoteBox(
    modifier = RemoteModifier.fillMaxSize().padding(containerPadding),
    contentAlignment = RemoteAlignment.Center,
  ) {
    RemoteColumn(
      horizontalAlignment = RemoteAlignment.CenterHorizontally,
      // 4.rdp gap matches the small spacing in the Figma element
      verticalArrangement = RemoteArrangement.spacedBy(4.rdp, RemoteAlignment.CenterVertically),
      modifier = RemoteModifier.fillMaxSize(),
    ) {
      PlaylistButton(
        playlistName = playlistName,
        playlistAction = playlistAction,
        playlistArtwork = playlistArtwork,
        playlistColor = playlistColor,
        imageSize = imageSize,
        textStyle = textStyle,
        // fillMaxWidth() without weight(1f) ensures it wraps height like a true pill
        modifier = RemoteModifier.fillMaxWidth(),
      )
      if (showSecondPlaylist) {
        PlaylistButton(
          playlistName = secondPlaylistName,
          playlistAction = secondPlaylistAction,
          playlistArtwork = secondPlaylistArtwork,
          playlistColor = secondPlaylistColor,
          imageSize = imageSize,
          textStyle = textStyle,
          modifier = RemoteModifier.fillMaxWidth(),
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
  playlistColor: Color?,
  modifier: RemoteModifier = RemoteModifier,
  imageSize: RemoteDp = 40.rdp,
  textStyle: RemoteTextStyle = RemoteMaterialTheme.typography.titleMedium,
) {
  // Determine if the background is light or dark to pick the best text color.
  // Luminance < 0.5 is considered dark.
  val isBackgroundDark = playlistColor?.let { it.luminance() < 0.5f } ?: true
  val contentColor = if (isBackgroundDark) Color.White else Color.Black

  RemoteButton(
    onClick = playlistAction,
    modifier = modifier.fillMaxWidth(),
    contentPadding = RemotePaddingValues(horizontal = 12.rdp, vertical = 8.rdp),
    colors =
      RemoteButtonDefaults.buttonColors(
        containerColor =
          playlistColor?.let { RemoteColor(it) }
            ?: RemoteMaterialTheme.colorScheme.secondaryContainer,
        contentColor = RemoteColor(contentColor),
      ),
    icon = {
      if (playlistArtwork != null) {
        RemoteImage(
          remoteBitmap = RemoteImageBitmap(playlistArtwork),
          contentDescription = "Artwork".rs,
          modifier = RemoteModifier.size(imageSize),
        )
      }
    },
  ) {
    RemoteText(
      text = playlistName.rs,
      style = textStyle,
      color = RemoteColor(contentColor),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}
