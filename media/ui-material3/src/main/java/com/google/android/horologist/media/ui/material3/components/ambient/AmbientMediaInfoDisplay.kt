package com.google.android.horologist.media.ui.material3.components.ambient

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.material3.components.display.NothingPlayingDisplay
import com.google.android.horologist.media.ui.material3.components.display.TextMediaDisplay
import com.google.android.horologist.media.ui.state.model.MediaUiModel

/**
 * Ambient [MediaDisplay] implementation for [PlayerScreen] including player status.
 *
 * @param media The current media being played.
 * @param loading Whether the player is currently loading.
 * @param modifier Modifier for the display.
 * @param colorScheme The color scheme to use for the display.
 */
@Composable
public fun AmbientMediaInfoDisplay(
  media: MediaUiModel?,
  loading: Boolean,
  modifier: Modifier = Modifier,
  colorScheme: ColorScheme = MaterialTheme.colorScheme
) {
  if (loading) {
    LoadingMediaDisplay(modifier, colorScheme = colorScheme)
  } else if (media is MediaUiModel.Ready) {
    TextMediaDisplay(
      modifier = modifier,
      title = media.title,
      subtitle = media.subtitle,
      titleIcon = media.titleIcon,
    )
  } else {
    NothingPlayingDisplay(modifier)
  }
}
