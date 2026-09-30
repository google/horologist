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

@file:SuppressLint("UnsafeOptInUsageError")

package com.google.android.horologist.media3.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Process
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaLibraryInfo
import androidx.media3.common.Player
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionError
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.test.core.app.ApplicationProvider
import com.google.android.horologist.media3.FakeErrorReporter
import com.google.common.collect.ImmutableList
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SuspendingMediaLibrarySessionCallbackTest {
  private val context = ApplicationProvider.getApplicationContext<Context>()
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
  private val callback = TestCallback(scope)
  private lateinit var player: Player
  private lateinit var session: MediaLibrarySession

  @Before
  fun setUp() {
    player = TestExoPlayerBuilder(context).build()
    session = MediaLibrarySession.Builder(context, player, callback).build()
  }

  @After
  fun tearDown() {
    session.release()
    player.release()
    scope.cancel()
  }

  @Test
  fun trustedControllerCanSetMediaItems() {
    val result = callback.onConnectAsync(session, controller(uid = OTHER_UID, trusted = true)).get()

    assertThat(result.isAccepted).isTrue()
    assertThat(result.availablePlayerCommands.contains(Player.COMMAND_SET_MEDIA_ITEM)).isTrue()
    assertThat(result.availablePlayerCommands.contains(Player.COMMAND_CHANGE_MEDIA_ITEMS)).isTrue()
  }

  @Test
  fun untrustedControllerCannotSetMediaItems() {
    val result = callback.onConnectAsync(session, controller(uid = OTHER_UID)).get()

    val commands = result.availablePlayerCommands
    assertThat(commands.contains(Player.COMMAND_SET_MEDIA_ITEM)).isFalse()
    assertThat(commands.contains(Player.COMMAND_CHANGE_MEDIA_ITEMS)).isFalse()
    assertThat(commands.contains(Player.COMMAND_SET_DEVICE_VOLUME_WITH_FLAGS)).isFalse()
  }

  @Test
  fun untrustedControllerUrisAreRemoved() {
    val item = MediaItem.Builder().setMediaId("id").setUri("https://example.com/a.mp3").build()

    val result =
      callback.onAddMediaItems(session, controller(uid = OTHER_UID), mutableListOf(item)).get()

    assertThat(result.single().mediaId).isEqualTo("id")
    assertThat(result.single().localConfiguration).isNull()
  }

  @Test
  fun ownAppUrisAreKept() {
    val item = MediaItem.Builder().setMediaId("id").setUri("https://example.com/a.mp3").build()

    val result =
      callback
        .onAddMediaItems(session, controller(uid = Process.myUid()), mutableListOf(item))
        .get()

    assertThat(result.single().localConfiguration?.uri.toString())
      .isEqualTo("https://example.com/a.mp3")
  }

  private fun controller(uid: Int, trusted: Boolean = false): MediaSession.ControllerInfo =
    MediaSession.ControllerInfo.createTestOnlyControllerInfo(
      "com.example.controller",
      /* pid= */ 0,
      uid,
      MediaLibraryInfo.VERSION_INT,
      /* interfaceVersion= */ 7,
      trusted,
      Bundle.EMPTY,
      /* isPackageNameVerified= */ true,
    )

  private class TestCallback(scope: CoroutineScope) :
    SuspendingMediaLibrarySessionCallback(scope, FakeErrorReporter()) {
    override suspend fun onGetLibraryRootInternal(
      session: MediaLibrarySession,
      browser: MediaSession.ControllerInfo,
      params: MediaLibraryService.LibraryParams?,
    ): LibraryResult<MediaItem> = LibraryResult.ofError(SessionError.ERROR_NOT_SUPPORTED)

    override suspend fun onGetItemInternal(
      session: MediaLibrarySession,
      browser: MediaSession.ControllerInfo,
      mediaId: String,
    ): LibraryResult<MediaItem> = LibraryResult.ofError(SessionError.ERROR_NOT_SUPPORTED)

    override suspend fun onGetChildrenInternal(
      session: MediaLibrarySession,
      browser: MediaSession.ControllerInfo,
      parentId: String,
      page: Int,
      pageSize: Int,
      params: MediaLibraryService.LibraryParams?,
    ): LibraryResult<ImmutableList<MediaItem>> =
      LibraryResult.ofError(SessionError.ERROR_NOT_SUPPORTED)
  }

  companion object {
    private const val OTHER_UID = 12345
  }
}
