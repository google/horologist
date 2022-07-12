/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.mediasample.complication

import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.watchface.complications.data.SmallImageType
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import coil.ImageLoader
import com.google.android.horologist.compose.tools.ComplicationRendererPreview
import com.google.android.horologist.media.ui.complication.MediaStatusTemplate
import com.google.android.horologist.media.ui.complication.MediaComplicationService
import com.google.android.horologist.media3.navigation.IntentBuilder
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.di.ComplicationServiceContainer
import com.google.android.horologist.tiles.images.loadImage
import kotlinx.coroutines.flow.StateFlow

/**
 * A complication provider that support small images. Upon tapping on the app icon,
 * the complication will launch the controls screen.
 */
class MediaStatusComplicationService :
    MediaComplicationService<MediaStatusTemplate.Data>() {
    lateinit var intentBuilder: IntentBuilder
    lateinit var imageLoader: ImageLoader
    lateinit var stateFlow: StateFlow<DataUpdates.State>
    override val renderer: MediaStatusTemplate = MediaStatusTemplate(this)

    override fun onCreate() {
        super.onCreate()

        ComplicationServiceContainer.inject(this)
    }

    override suspend fun data(request: ComplicationRequest): MediaStatusTemplate.Data {
        val state = stateFlow.value

        return if (state.mediaItem != null) {
            val bitmap = imageLoader.loadImage(this, state.mediaItem.mediaMetadata.artworkUri) {
                size(64)
            }
            val icon = Icon.createWithBitmap(bitmap)
            val mediaTitle = state.mediaItem.mediaMetadata.displayTitle.toString()
            val mediaArtist = state.mediaItem.mediaMetadata.artist.toString()
            MediaStatusTemplate.Data(
                text = mediaTitle,
                title = mediaArtist,
                icon = icon,
                type = SmallImageType.PHOTO,
                launchIntent = intentBuilder.buildPlayerIntent(),
            )
        } else {
            MediaStatusTemplate.Data(
                text = getString(R.string.horologist_favorites),
                title = getString(R.string.horologist_sample_app_name),
                appIconRes = R.drawable.ic_baseline_queue_music_24,
                launchIntent = intentBuilder.buildPlayerIntent(),
                type = SmallImageType.ICON,
            )
        }
    }
}

@Composable
@Preview(
    backgroundColor = 0xFF000000,
    showBackground = true,
)
fun MediaCollectionComplicationPreviewDefault() {
    val context = LocalContext.current
    val renderer = MediaStatusTemplate(context)

    ComplicationRendererPreview(renderer)
}

@Composable
@Preview(
    backgroundColor = 0xFF000000,
    showBackground = true,
)
fun MediaCollectionComplicationPreviewFavourites() {
    val context = LocalContext.current
    val renderer = MediaStatusTemplate(context)

    val icon = Icon.createWithResource(context, R.drawable.ic_baseline_queue_music_24)

    ComplicationRendererPreview(
        complicationRenderer = renderer,
        data = MediaStatusTemplate.Data(
            text = context.getString(R.string.horologist_favorites),
            title = context.getString(R.string.horologist_sample_app_name),
            icon = icon,
            type = SmallImageType.ICON,
            launchIntent = null,
        )
    )
}

@Composable
@Preview(
    backgroundColor = 0xFF000000,
    showBackground = true,
)
fun MediaCollectionComplicationPreviewMedia() {
    val context = LocalContext.current
    val renderer = MediaStatusTemplate(context)

    val icon = remember {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.album_art)
        Icon.createWithBitmap(bitmap)
    }

    ComplicationRendererPreview(
        complicationRenderer = renderer,
        data = MediaStatusTemplate.Data(
            text = context.getString(R.string.horologist_preview_song),
            title = context.getString(R.string.horologist_preview_artist),
            icon = icon,
            type = SmallImageType.PHOTO,
            launchIntent = null,
        )
    )
}
