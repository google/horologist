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

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Process
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionError
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
) : MediaLibrarySession.Callback {
  @SuppressLint("UnsafeOptInUsageError")
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
        LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
      }
    }
  }

  protected abstract suspend fun onGetLibraryRootInternal(
    session: MediaLibrarySession,
    browser: MediaSession.ControllerInfo,
    params: MediaLibraryService.LibraryParams?,
  ): LibraryResult<MediaItem>

  @SuppressLint("UnsafeOptInUsageError")
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
        LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
      }
    }
  }

  protected abstract suspend fun onGetItemInternal(
    session: MediaLibrarySession,
    browser: MediaSession.ControllerInfo,
    mediaId: String,
  ): LibraryResult<MediaItem>

  /**
   * Whether [controller] is trusted to supply playable URIs in [MediaItem.localConfiguration].
   *
   * By default this is this app, the media notification, Android Auto, and controllers that Android
   * considers [trusted][MediaSession.ControllerInfo.isTrusted] (system apps, apps holding
   * `MEDIA_CONTENT_CONTROL` and enabled notification listeners).
   *
   * Media3's default [MediaSession.Callback.onConnectAsync] only gives untrusted controllers
   * read-only commands, so they can't add media items at all. This check also protects apps that
   * override `onConnectAsync` to give other apps more commands.
   */
  @SuppressLint("UnsafeOptInUsageError")
  protected open fun isTrustedController(
    session: MediaSession,
    controller: MediaSession.ControllerInfo,
  ): Boolean =
    controller.uid == Process.myUid() ||
      // Covers the Wear OS SysUI media controls (UMO), so no Wear-specific check is needed.
      controller.isTrusted ||
      session.isMediaNotificationController(controller) ||
      session.isAutomotiveController(controller) ||
      session.isAutoCompanionController(controller)

  override fun onAddMediaItems(
    mediaSession: MediaSession,
    controller: MediaSession.ControllerInfo,
    mediaItems: MutableList<MediaItem>,
  ): ListenableFuture<MutableList<MediaItem>> {
    val items =
      if (isTrustedController(mediaSession, controller)) {
        mediaItems
      } else {
        mediaItems.map { it.withoutLocalConfiguration() }.toMutableList()
      }
    return serviceScope.future { onAddMediaItemsInternal(mediaSession, controller, items) }
  }

  /**
   * Resolves the [MediaItem]s to play.
   *
   * [MediaItem.localConfiguration] (the playable URI) is only present for
   * [trusted][isTrustedController] controllers, and is removed for any other app. Override this to
   * resolve [MediaItem.mediaId] against your own catalog. Never use
   * [MediaItem.RequestMetadata.mediaUri] as the playable URI without validating it, since it can be
   * set by any controller. The default implementation returns the items unchanged.
   */
  protected open suspend fun onAddMediaItemsInternal(
    mediaSession: MediaSession,
    controller: MediaSession.ControllerInfo,
    mediaItems: MutableList<MediaItem>,
  ): MutableList<MediaItem> {
    return mediaItems
  }

  @SuppressLint("UnsafeOptInUsageError")
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
        LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
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

internal fun MediaItem.withoutLocalConfiguration(): MediaItem =
  if (localConfiguration == null) this else buildUpon().setUri(null as Uri?).build()
