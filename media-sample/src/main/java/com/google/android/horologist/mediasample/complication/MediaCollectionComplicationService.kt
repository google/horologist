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

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.google.android.horologist.compose.tools.ComplicationRendererPreview
import com.google.android.horologist.media.ui.complication.MediaCollectionTemplate
import com.google.android.horologist.media.ui.complication.MediaComplicationService
import com.google.android.horologist.media3.navigation.IntentBuilder
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.di.ComplicationServiceContainer
import com.google.android.horologist.mediasample.di.ServiceContainer

/**
 * A complication provider that support small images. Upon tapping on the app icon,
 * the complication will launch the controls screen.
 */
class MediaCollectionComplicationService :
    MediaComplicationService<MediaCollectionTemplate.Data>() {
    lateinit var intentBuilder: IntentBuilder
    override val renderer: MediaCollectionTemplate = MediaCollectionTemplate(this)

    override fun onCreate() {
        super.onCreate()

        ComplicationServiceContainer.inject(this)
    }

    override suspend fun data(request: ComplicationRequest): MediaCollectionTemplate.Data {
        return MediaCollectionTemplate.Data(
            collectionName = getString(R.string.horologist_favorites),
            appIcon = R.drawable.ic_baseline_queue_music_24,
            appImage = R.drawable.ic_uamp,
            launchIntent = intentBuilder.buildPlayerIntent(),
            appName = R.string.horologist_sample_app_name
        )
    }
}

@Composable
@Preview(
    backgroundColor = 0xFF000000,
    showBackground = true,
)
fun MediaCollectionComplicationPreviewDefaultData() {
    val context = LocalContext.current
    val renderer = MediaCollectionTemplate(context)

    ComplicationRendererPreview(renderer)
}
