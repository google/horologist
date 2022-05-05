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

package com.google.android.horologist.sample.di

import com.google.android.horologist.sample.MainActivity
import com.google.android.horologist.sample.media.MediaDataSource
import com.google.android.horologist.sample.media.MediaPlayerScreenViewModel
import com.google.android.horologist.sample.media.PlayerRepositoryImpl

/**
 * Simple DI implementation - to be replaced by hilt.
 */
object SampleAppDI {

    fun inject(mainActivity: MainActivity) {
        mainActivity.mediaPlayerScreenViewModelFactory =
            getMediaPlayerScreenViewModelFactory(getPlayerRepositoryImpl(getMediaDataSource()))
    }

    private fun getMediaPlayerScreenViewModelFactory(playerRepositoryImpl: PlayerRepositoryImpl): MediaPlayerScreenViewModel.Factory =
        MediaPlayerScreenViewModel.Factory(playerRepositoryImpl)

    private fun getPlayerRepositoryImpl(mediaDataSource: MediaDataSource): PlayerRepositoryImpl =
        PlayerRepositoryImpl(mediaDataSource)

    private fun getMediaDataSource(): MediaDataSource = MediaDataSource()
}
