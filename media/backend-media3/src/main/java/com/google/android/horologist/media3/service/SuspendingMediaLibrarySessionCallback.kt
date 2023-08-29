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

package com.google.android.horologist.media3.service

import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media3.logging.ErrorReporter
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.guava.future

/**
 * ListenableFuture to Coroutines adapting base class for MediaLibrarySession.Callback.
 *
 * Each metho is implemented like for like,
 */
@ExperimentalHorologistApi
public abstract class SuspendingMediaLibrarySessionCallback(
    private val serviceScope: CoroutineScope,
    private val appEventLogger: ErrorReporter,
) :
    MediaLibrarySession.Callback {
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: MediaLibraryService.LibraryParams?,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return serviceScope.future {
                try {
                    onGetLibraryRootInternal(session, browser, params)
                } catch (e: Exception) {
                    appEventLogger.logMessage(
                        "onGetLibraryRoot: $e",
                        ErrorReporter.Category.App,
                        ErrorReporter.Level.Error,
                    )
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                }
            }
        }

        protected abstract suspend fun onGetLibraryRootInternal(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: MediaLibraryService.LibraryParams?,
        ): LibraryResult<MediaItem>

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return serviceScope.future {
                try {
                    onGetItemInternal(session, browser, mediaId)
                } catch (e: Exception) {
                    appEventLogger.logMessage(
                        "onGetItem: $e",
                        ErrorReporter.Category.App,
                        ErrorReporter.Level.Error,
                    )
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                }
            }
        }

        protected abstract suspend fun onGetItemInternal(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): LibraryResult<MediaItem>

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
        ): ListenableFuture<MutableList<MediaItem>> {
            return serviceScope.future {
                onAddMediaItemsInternal(mediaSession, controller, mediaItems)
            }
        }

        /**
         * default implementation of onAddMediaItems that sets the URI from the requestMetadata
         * if present.
         */
        protected open suspend fun onAddMediaItemsInternal(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
        ): MutableList<MediaItem> {
            return mediaItems.map {
                if (it.requestMetadata.mediaUri != null) {
                    it.buildUpon()
                        .setUri(it.requestMetadata.mediaUri)
                        .build()
                } else {
                    it
                }
            }.toMutableList()
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: MediaLibraryService.LibraryParams?,
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            return serviceScope.future {
                try {
                    onGetChildrenInternal(session, browser, parentId, page, pageSize, params)
                } catch (e: Exception) {
                    appEventLogger.logMessage(
                        "onGetChildren: $e",
                        ErrorReporter.Category.App,
                        ErrorReporter.Level.Error,
                    )
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                }
            }
        }

        protected abstract suspend fun onGetChildrenInternal(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: MediaLibraryService.LibraryParams?,
        ): LibraryResult<ImmutableList<MediaItem>>
    }
