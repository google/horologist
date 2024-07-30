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

package com.google.android.horologist.mediasample.data.service.playback

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionError
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.service.SuspendingMediaLibrarySessionCallback
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope

class UampMediaLibrarySessionCallback(
    serviceScope: CoroutineScope,
    appEventLogger: ErrorReporter,
) : SuspendingMediaLibrarySessionCallback(serviceScope, appEventLogger) {
    @SuppressLint("UnsafeOptInUsageError")
    override suspend fun onGetLibraryRootInternal(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?,
    ): LibraryResult<MediaItem> {
        // TODO implement
        return LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override suspend fun onGetItemInternal(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String,
    ): LibraryResult<MediaItem> {
        // TODO implement
        return LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override suspend fun onGetChildrenInternal(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?,
    ): LibraryResult<ImmutableList<MediaItem>> {
        // TODO implement
        return LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
    }
}
