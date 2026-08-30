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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.glance.wear.core.WearWidgetParams
import androidx.glance.wear.tooling.preview.RectangularAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.RoundAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.SquircleAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.WearWidgetPreview
import com.google.android.horologist.images.coil.FakeImageLoader
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.repository.PlaylistRepository
import com.google.android.horologist.mediasample.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun mediaCollectionsWidgetPreviewData(): MediaCollectionsWidget {
  return remember {
    MediaCollectionsWidget(
      playlistRepository =
        object : PlaylistRepository {
          override suspend fun get(playlistId: String): Playlist? = null

          override fun getAll(): Flow<List<Playlist>> =
            flowOf(
              listOf(
                Playlist(
                  id = "s1",
                  name = "Kyoto Songs",
                  artworkUri = "${FakeImageLoader.TestUriPrefix}${R.drawable.kyoto}",
                  mediaList = emptyList(),
                ),
                Playlist(
                  id = "c2",
                  name = "Podcasts",
                  artworkUri =
                    "${FakeImageLoader.TestUriPrefix}${R.drawable.ic_baseline_podcasts_24}",
                  mediaList = emptyList(),
                ),
              )
            )

          override fun getAllDownloaded(): Flow<List<Playlist>> = flowOf(emptyList())
        },
      imageLoader = FakeImageLoader.Resources,
    )
  }
}

@Preview(name = "Squircle Preview", device = "spec:width=1000dp,height=1000dp,dpi=320")
@Composable
fun MediaCollectionWidgetSquirclePreview(
  @PreviewParameter(SquircleAllWidgetPreviewParams::class) params: WearWidgetParams
) = WearWidgetPreview(mediaCollectionsWidgetPreviewData(), params)

@Preview(name = "Round Preview", device = "spec:width=1000dp,height=1000dp,dpi=320")
@Composable
fun MediaCollectionWidgetRoundPreview(
  @PreviewParameter(RoundAllWidgetPreviewParams::class) params: WearWidgetParams
) = WearWidgetPreview(mediaCollectionsWidgetPreviewData(), params)

@Preview(name = "Widget Picker Preview", device = "spec:width=1000dp,height=1000dp,dpi=320")
@Composable
fun MediaCollectionWidgetRectangularPreview(
  @PreviewParameter(RectangularAllWidgetPreviewParams::class) params: WearWidgetParams
) = WearWidgetPreview(mediaCollectionsWidgetPreviewData(), params)
