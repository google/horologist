/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.mediasample.complication

import android.graphics.drawable.Icon
import androidx.media3.common.MediaItem
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.SmallImageType
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import coil.ImageLoader
import com.google.android.horologist.media.ui.complication.MediaStatusTemplate
import com.google.android.horologist.media.ui.complication.MediaStatusTemplate.Data
import com.google.android.horologist.media3.navigation.IntentBuilder
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.tiles.complication.ComplicationTemplate
import com.google.android.horologist.tiles.complication.DataComplicationService
import com.google.android.horologist.tiles.images.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * A media complication service that shows the app name and favorites category
 * when not playing, but switches to current media when playing.
 */
@AndroidEntryPoint
class MediaStatusComplicationService :
    DataComplicationService<Data, ComplicationTemplate<Data>>() {
    @Inject
    lateinit var intentBuilder: IntentBuilder

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var dataUpdates: DataUpdates

    override val renderer: MediaStatusTemplate = MediaStatusTemplate(this)

    override fun previewData(type: ComplicationType): Data = renderer.previewData()

    override suspend fun data(request: ComplicationRequest): Data {
        val state = dataUpdates.stateFlow.value

        return if (state.mediaItem != null) {
            whilePlayingData(state.mediaItem)
        } else {
            notPlayingData()
        }
    }

    private fun notPlayingData() = Data(
        text = getString(R.string.horologist_favorites),
        title = getString(R.string.horologist_sample_app_name),
        appIconRes = R.drawable.ic_baseline_queue_music_24,
        launchIntent = intentBuilder.buildPlayerIntent(),
        type = SmallImageType.ICON,
    )

    private suspend fun whilePlayingData(mediaItem: MediaItem): Data {
        val bitmap = withTimeoutOrNull(2.seconds) {
            imageLoader.loadImage(
                context = this@MediaStatusComplicationService,
                data = mediaItem.mediaMetadata.artworkUri
            ) {
                size(64)
            }
        }
        val icon = if (bitmap != null) Icon.createWithBitmap(bitmap) else null
        val mediaTitle = mediaItem.mediaMetadata.displayTitle.toString()
        val mediaArtist = mediaItem.mediaMetadata.artist.toString()
        return Data(
            text = mediaTitle,
            title = mediaArtist,
            icon = icon,
            type = SmallImageType.PHOTO,
            launchIntent = intentBuilder.buildPlayerIntent(),
        )
    }
}
