/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.screensizes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.protolayout.ActionBuilders
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.tiles.MediaCollectionsTileRenderer
import com.google.android.horologist.media.ui.tiles.toTileColors
import com.google.android.horologist.media.ui.uamp.UampColors
import com.google.android.horologist.tiles.images.drawableResToImageResource

class SampleTileTest(device: Device) : ScreenSizeTest(device = device, showTimeText = false) {

    @Composable
    override fun Content() {
        SampleTilePreview()
    }
}

@Composable
fun SampleTilePreview() {
    val context = LocalContext.current

    val action = ActionBuilders.LaunchAction.Builder()
        .build()

    val tileState = remember {
        MediaCollectionsTileRenderer.MediaCollectionsState(
            chipName = R.string.horologist_browse_library_playlists,
            chipAction = action,
            collection1 = MediaCollectionsTileRenderer.MediaCollection(
                name = context.getString(R.string.horologist_browse_downloads_title),
                artworkId = "1",
                action = action,
            ),
            collection2 = MediaCollectionsTileRenderer.MediaCollection(
                name = context.getString(R.string.horologist_nothing_playing),
                artworkId = "2",
                action = action,
            ),
        )
    }

    val resourceState = remember {
        MediaCollectionsTileRenderer.ResourceState(
            appIcon = com.google.android.horologist.logo.R.drawable.ic_stat_horologist,
            images = mapOf(
                "1" to drawableResToImageResource(R.drawable.ic_baseline_queue_music_24),
                "2" to drawableResToImageResource(R.drawable.ic_baseline_podcasts_24),
            ),
        )
    }

    val renderer = remember {
        MediaCollectionsTileRenderer(
            context = context,
            materialTheme = UampColors.toTileColors(),
            debugResourceMode = false,
        )
    }

    TileLayoutPreview(
        tileState,
        resourceState,
        renderer,
    )
}
