/*
 * Copyright 2021 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.mediasample.media

import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.android.horologist.media3.service.CoroutinesMediaLibrarySessionCallback
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope

class UampMediaLibrarySessionCallback(
    serviceScope: CoroutineScope,
    appEventLogger: ErrorReporter
) : CoroutinesMediaLibrarySessionCallback(serviceScope, appEventLogger) {
    override suspend fun onGetLibraryRootInternal(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult<MediaItem> {
        // TODO implement
        return LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
    }

    override suspend fun onGetItemInternal(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): LibraryResult<MediaItem> {
        // TODO implement
        return LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
    }

    override suspend fun onGetChildrenInternal(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult<ImmutableList<MediaItem>> {
        // TODO implement
        return LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
    }
}
