/*
 * Copyright 2024 The Android Open Source Project
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

import android.content.Context
import android.graphics.BitmapFactory
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.tools.tileRendererPreviewData
import com.google.android.horologist.media.ui.tiles.MediaCollectionsTileRenderer
import com.google.android.horologist.media.ui.tiles.toTileColors
import com.google.android.horologist.mediasample.BuildConfig
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.app.UampColors
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.images.toImageResource

@Preview(device = WearDevices.LARGE_ROUND)
@Preview(device = WearDevices.SMALL_ROUND)
fun SampleTilePreview(context: Context): TilePreviewData = tileRendererPreviewData(
    renderer = MediaCollectionsTileRenderer(
        context = context,
        debugResourceMode = BuildConfig.DEBUG,
    ),
    tileState = MediaCollectionsTileRenderer.MediaCollectionsState(
        chipName = R.string.sample_playlists,
        chipAction = ActionBuilders.LaunchAction.Builder().build(),
        collection1 = MediaCollectionsTileRenderer.MediaCollection(
            name = "Kyoto Songs",
            artworkId = "s1",
            action = ActionBuilders.LaunchAction.Builder().build(),
        ),
        collection2 = MediaCollectionsTileRenderer.MediaCollection(
            name = "Podcasts",
            artworkId = "c2",
            action = ActionBuilders.LaunchAction.Builder().build(),
        ),
    ),
    resourceState = MediaCollectionsTileRenderer.ResourceState(
        appIcon = com.google.android.horologist.logo.R.drawable.ic_stat_horologist,
        images = mapOf(
            "s1" to BitmapFactory.decodeResource(context.resources, R.drawable.kyoto)
                ?.toImageResource(),
            "c2" to drawableResToImageResource(R.drawable.ic_baseline_podcasts_24),
        ),
    ),
)
