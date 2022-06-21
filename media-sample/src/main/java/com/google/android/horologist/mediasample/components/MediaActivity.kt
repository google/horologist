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

package com.google.android.horologist.mediasample.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.repository.PlayerRepository
import com.google.android.horologist.mediasample.catalog.UampService
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import com.google.android.horologist.mediasample.ui.MediaPlayerScreenViewModel
import com.google.android.horologist.mediasample.ui.WearApp
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class MediaActivity : ComponentActivity() {
    lateinit var mediaPlayerScreenViewModelFactory: MediaPlayerScreenViewModel.Factory

    lateinit var playerRepository: PlayerRepository

    lateinit var uampService: UampService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MediaApplicationContainer.inject(this)

        setContent {
            val navController = rememberSwipeDismissableNavController()
            WearApp(navController, mediaPlayerScreenViewModelFactory)
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launchWhenResumed {
            playerRepository.connected.filter { it }.first()

            if (playerRepository.currentMediaItem.value == null) {
                val mediaItems = uampService.catalog().music.map {
                    MediaItem(
                        it.id,
                        it.source,
                        it.title,
                        it.artist,
                        it.image,
                    )
                }

                playerRepository.setMediaItems(mediaItems)
            }
        }
    }
}
