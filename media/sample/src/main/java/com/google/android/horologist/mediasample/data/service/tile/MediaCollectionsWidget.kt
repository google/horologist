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
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.compose.remote.creation.compose.action.Action
import androidx.compose.remote.creation.compose.action.pendingIntentAction
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteArrangement
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteImage
import androidx.compose.remote.creation.compose.layout.RemoteRow
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.background
import androidx.compose.remote.creation.compose.modifier.clickable
import androidx.compose.remote.creation.compose.modifier.clip
import androidx.compose.remote.creation.compose.modifier.fillMaxHeight
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.modifier.fillMaxWidth
import androidx.compose.remote.creation.compose.modifier.padding
import androidx.compose.remote.creation.compose.modifier.size
import androidx.compose.remote.creation.compose.shapes.RemoteRoundedCornerShape
import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.remote.creation.compose.state.rsp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.wear.GlanceWearWidget
import androidx.glance.wear.WearWidgetBrush
import androidx.glance.wear.WearWidgetData
import androidx.glance.wear.WearWidgetDocument
import androidx.glance.wear.color
import androidx.glance.wear.core.ContainerInfo
import androidx.glance.wear.core.WearWidgetParams
import androidx.glance.wear.image
import androidx.palette.graphics.Palette
import androidx.wear.compose.remote.material3.RemoteMaterialTheme
import androidx.wear.compose.remote.material3.RemoteText
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.horologist.logo.R as LogoR
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.app.MediaActivity
import com.google.android.horologist.mediasample.ui.app.UampColors
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.first

/** Figma uses Google Sans Flex at weight 550 for the playlist label. */
private val LabelFontWeight = FontWeight(550)

/** Artwork shown on a large-widget tile. */
data class PlaylistTile(val name: String, val artwork: ImageBitmap?, val action: Action)

/**
 * A Widget providing links to playlists, following the Media partial-height widget mocks.
 * - [ContainerInfo.CONTAINER_TYPE_SMALL]: the first playlist, with its artwork as background.
 * - [ContainerInfo.CONTAINER_TYPE_LARGE]: artwork of the first two playlists above a "Browse"
 *   button that opens the app.
 */
class MediaCollectionsWidget(
  private val playlistRepository: PlaylistRepository,
  private val imageLoader: ImageLoader,
) : GlanceWearWidget() {

  override suspend fun provideWidgetData(
    context: Context,
    params: WearWidgetParams,
  ): WearWidgetData {
    val isLarge = params.containerType == ContainerInfo.CONTAINER_TYPE_LARGE
    val playlists = playlistRepository.getAll().first().take(if (isLarge) LARGE_TILE_COUNT else 1)

    return when {
      playlists.isEmpty() -> emptyDocument()
      isLarge -> largeDocument(context, playlists)
      else -> smallDocument(context, playlists.first())
    }
  }

  private suspend fun smallDocument(context: Context, playlist: Playlist): WearWidgetData {
    val artwork =
      playlist.artworkUri?.let { loadArtworkBitmap(context, it, SMALL_BACKGROUND_SIZE_PX) }
    val tinted = artwork?.let { it.withScrim(artworkScrimColor(it)) }
    val fallback = WearWidgetBrush.color(UampColors.primaryContainer.rc)
    val background =
      tinted?.let { fallback.image(RemoteImageBitmap(it.asImageBitmap()), ContentScale.Crop) }
        ?: fallback
    val labelColor = tinted?.let { labelColorOn(it) } ?: UampColors.onPrimaryContainer

    val appLogo = loadAppLogoBitmap(context)

    return WearWidgetDocument(background = background) {
      SmallMediaCollectionContent(
        playlistName = playlist.name,
        playlistAction = playlistAction(requestCode = 1, playlistId = playlist.id),
        appLogo = appLogo,
        labelColor = labelColor,
      )
    }
  }

  private fun artworkScrimColor(artwork: Bitmap): Color {
    val palette = Palette.from(artwork).generate()
    val swatch = palette.darkVibrantSwatch ?: palette.darkMutedSwatch ?: palette.dominantSwatch
    return Color(swatch?.rgb ?: android.graphics.Color.BLACK).copy(alpha = SMALL_SCRIM_ALPHA)
  }

  private fun labelColorOn(tintedArtwork: Bitmap): Color {
    val palette = Palette.from(tintedArtwork).generate()
    val dominant = palette.dominantSwatch?.rgb?.let { Color(it) } ?: return Color.White
    if (dominant.luminance() < LIGHT_BACKGROUND_LUMINANCE) return Color.White
    val dark = palette.darkVibrantSwatch ?: palette.darkMutedSwatch
    return dark?.rgb?.let { Color(it) } ?: Color.Black
  }

  private fun loadAppLogoBitmap(context: Context): ImageBitmap? {
    val sizePx = (APP_LOGO_SIZE_DP * context.resources.displayMetrics.density).roundToInt()
    return ContextCompat.getDrawable(context, LogoR.mipmap.ic_horologist_round)
      ?.toBitmap(sizePx, sizePx)
      ?.asImageBitmap()
  }

  private suspend fun largeDocument(context: Context, playlists: List<Playlist>): WearWidgetData {
    val artworks = playlists.map { playlist ->
      playlist.artworkUri?.let { loadArtworkBitmap(context, it, TILE_SIZE_PX) }?.asImageBitmap()
    }
    val browseLabel = context.getString(R.string.widget_browse)
    val appLogo = loadAppLogoBitmap(context)

    return WearWidgetDocument(background = WearWidgetBrush.color(UampColors.surfaceContainer.rc)) {
      LargeMediaCollectionContent(
        browseLabel = browseLabel,
        appAction = pendingIntentAction { ctx -> createAppPendingIntent(ctx) },
        appLogo = appLogo,
        tiles =
          playlists.mapIndexed { index, playlist ->
            PlaylistTile(
              name = playlist.name,
              artwork = artworks[index],
              action = playlistAction(requestCode = index + 1, playlistId = playlist.id),
            )
          },
      )
    }
  }

  private fun emptyDocument(): WearWidgetData =
    WearWidgetDocument(background = WearWidgetBrush.color(remoteColorScheme.surfaceContainerLow)) {
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

  private suspend fun loadArtworkBitmap(
    context: Context,
    artworkUri: String,
    sizePx: Int,
  ): Bitmap? {
    val request =
      ImageRequest.Builder(context).data(artworkUri).size(sizePx).allowHardware(false).build()
    val result = imageLoader.execute(request)
    return (result.drawable as? BitmapDrawable)?.bitmap
  }

  @Composable
  private fun playlistAction(requestCode: Int, playlistId: String): Action =
    pendingIntentAction { ctx ->
      PendingIntent.getActivity(
        ctx,
        requestCode,
        mediaActivityIntent(ctx).putExtra(MediaActivity.CollectionKey, playlistId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
    }

  private fun createAppPendingIntent(context: Context): PendingIntent =
    PendingIntent.getActivity(
      context,
      APP_REQUEST_CODE,
      mediaActivityIntent(context),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  private fun mediaActivityIntent(context: Context): Intent =
    Intent(context, MediaActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

  companion object {
    const val LARGE_TILE_COUNT = 2
    const val APP_LOGO_SIZE_DP = 24
    private const val APP_REQUEST_CODE = 0
    private const val SMALL_BACKGROUND_SIZE_PX = 320
    private const val TILE_SIZE_PX = 200
    private const val SMALL_SCRIM_ALPHA = 0.38f

    private const val LIGHT_BACKGROUND_LUMINANCE = 0.5f
  }
}

private fun Bitmap.withScrim(scrim: Color): Bitmap =
  copy(Bitmap.Config.ARGB_8888, true).also { Canvas(it).drawColor(scrim.toArgb()) }

@SuppressLint("RestrictedApi")
@RemoteComposable
@Composable
fun SmallMediaCollectionContent(
  playlistName: String,
  playlistAction: Action,
  appLogo: ImageBitmap?,
  labelColor: Color,
) {
  RemoteRow(
    modifier =
      RemoteModifier.fillMaxSize().clickable(playlistAction).padding(start = 20.rdp, end = 14.rdp),
    horizontalArrangement = RemoteArrangement.spacedBy(6.rdp),
    verticalAlignment = RemoteAlignment.CenterVertically,
  ) {
    if (appLogo != null) AppLogo(appLogo)
    RemoteText(
      text = playlistName.rs,
      color = labelColor.rc,
      fontSize = 14.rsp,
      fontWeight = LabelFontWeight,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@SuppressLint("RestrictedApi")
@RemoteComposable
@Composable
fun LargeMediaCollectionContent(
  browseLabel: String,
  appAction: Action,
  appLogo: ImageBitmap?,
  tiles: List<PlaylistTile>,
) {
  RemoteColumn(
    modifier = RemoteModifier.fillMaxSize().padding(4.rdp),
    verticalArrangement = RemoteArrangement.spacedBy(4.rdp),
  ) {
    RemoteRow(
      modifier = RemoteModifier.weight(1f).fillMaxWidth(),
      horizontalArrangement = RemoteArrangement.spacedBy(4.rdp),
    ) {
      for (index in 0 until MediaCollectionsWidget.LARGE_TILE_COUNT) {
        PlaylistArtworkTile(
          tile = tiles.getOrNull(index),
          modifier = RemoteModifier.weight(1f).fillMaxHeight(),
        )
      }
    }
    RemoteRow(
      modifier =
        RemoteModifier.weight(1f)
          .fillMaxWidth()
          .clip(RemoteRoundedCornerShape(28.rdp))
          .background(UampColors.primary.rc)
          .clickable(appAction),
      horizontalArrangement = RemoteArrangement.spacedBy(6.rdp, RemoteAlignment.CenterHorizontally),
      verticalAlignment = RemoteAlignment.CenterVertically,
    ) {
      if (appLogo != null) AppLogo(appLogo)
      RemoteText(
        text = browseLabel.rs,
        color = UampColors.onPrimary.rc,
        fontSize = 14.rsp,
        fontWeight = LabelFontWeight,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@SuppressLint("RestrictedApi")
@RemoteComposable
@Composable
private fun AppLogo(appLogo: ImageBitmap) {
  RemoteImage(
    remoteBitmap = RemoteImageBitmap(appLogo),
    contentDescription = null,
    modifier =
      RemoteModifier.size(MediaCollectionsWidget.APP_LOGO_SIZE_DP.rdp)
        .clip(RemoteRoundedCornerShape((MediaCollectionsWidget.APP_LOGO_SIZE_DP / 2).rdp)),
  )
}

@SuppressLint("RestrictedApi")
@RemoteComposable
@Composable
private fun PlaylistArtworkTile(tile: PlaylistTile?, modifier: RemoteModifier) {
  val shaped =
    modifier.clip(RemoteRoundedCornerShape(28.rdp)).background(UampColors.surfaceContainerHigh.rc)
  RemoteBox(
    modifier = if (tile != null) shaped.clickable(tile.action) else shaped,
    contentAlignment = RemoteAlignment.Center,
  ) {
    if (tile?.artwork != null) {
      RemoteImage(
        remoteBitmap = RemoteImageBitmap(tile.artwork),
        contentDescription = tile.name.rs,
        modifier = RemoteModifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
      )
    }
  }
}
