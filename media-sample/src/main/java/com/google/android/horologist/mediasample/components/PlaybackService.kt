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

import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import com.google.android.horologist.media3.navigation.IntentBuilder
import com.google.android.horologist.media3.service.LifecycleMediaLibraryService
import com.google.android.horologist.mediasample.di.MediaApplicationContainer

class PlaybackService: LifecycleMediaLibraryService() {
    override lateinit var player: Player
    override lateinit var librarySessionCallback: MediaLibrarySession.Callback
    override lateinit var intentBuilder: IntentBuilder
    override lateinit var mediaLibrarySession: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()

        MediaApplicationContainer.inject(this)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }
}