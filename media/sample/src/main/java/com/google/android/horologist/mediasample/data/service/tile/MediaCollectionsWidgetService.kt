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

import androidx.glance.wear.AssociateWithGlanceWearWidget
import androidx.glance.wear.GlanceWearWidget
import androidx.glance.wear.GlanceWearWidgetService
import coil.ImageLoader
import com.google.android.horologist.media.repository.PlaylistRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A Widget Service providing glanceable media collections (playlist, album) as part of a
 * dual-service architecture alongside [MediaCollectionsTileService].
 */
@AssociateWithGlanceWearWidget(MediaCollectionsWidget::class)
@AndroidEntryPoint
class MediaCollectionsWidgetService : GlanceWearWidgetService() {

    @Inject
    lateinit var playlistRepository: PlaylistRepository

    @Inject
    lateinit var imageLoader: ImageLoader

    override val widget: GlanceWearWidget by lazy {
        MediaCollectionsWidget(playlistRepository, imageLoader)
    }
}
