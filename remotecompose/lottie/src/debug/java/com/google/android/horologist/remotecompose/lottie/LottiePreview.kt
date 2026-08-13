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

package com.google.android.horologist.remotecompose.lottie

import android.annotation.SuppressLint
import androidx.annotation.RawRes
import androidx.compose.remote.core.RemoteClock
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.horologist.remotecompose.lottie.format.Animation

/**
 * Displays a Lottie animation using Remote Compose.
 *
 * @param animation The parsed Lottie animation to play.
 * @param modifier The modifier to apply to the host layout.
 * @param slotMap Optional mapping of slot IDs to values for dynamic theming.
 * @param clock The clock driving the animation. The document carries this clock through to the
 *   player.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@SuppressLint("RestrictedApi")
@Composable
internal fun LottiePreview(
  animation: Animation,
  modifier: Modifier = Modifier,
  slotMap: SlotMap = SlotMap.Empty,
  clock: RemoteClock = RemoteClock.SYSTEM,
  progress: Float? = null,
) {
  val doc =
    rememberRemoteDocument(clock = clock) {
      // When progress is specified, bind the animation to a named RemoteFloat ("progress").
      // This allows updating progress dynamically via player.setUserLocalFloat("progress", value)
      // on the single compiled RemoteDocument, avoiding document regeneration on frame changes.
      val progressVar =
        if (progress != null) {
          rememberNamedRemoteFloat("progress") { 0f.rf }
        } else {
          null
        }
      LottieAnimation(
        animation,
        slotMap = slotMap,
        progress = progressVar,
        modifier = RemoteModifier.fillMaxSize(),
      )
    }
  doc.value?.let { document ->
    RemoteDocumentPlayer(
      document = document,
      modifier = modifier,
      documentWidth = animation.width,
      documentHeight = animation.height,
      update = { player ->
        if (progress != null) {
          player.setUserLocalFloat("progress", progress)
        }
      },
    )
  }
}

/**
 * Displays a Lottie animation from a raw resource ID using Remote Compose.
 *
 * @param animationResId The raw resource ID of the Lottie JSON file.
 * @param modifier The modifier to apply to the host layout.
 * @param slotMap Optional mapping of slot IDs to values for dynamic theming.
 * @param clock The clock driving the animation.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@SuppressLint("RestrictedApi")
@Composable
fun LottiePreview(
  @RawRes animationResId: Int,
  modifier: Modifier = Modifier,
  slotMap: SlotMap = SlotMap.Empty,
  clock: RemoteClock = RemoteClock.SYSTEM,
  progress: Float? = null,
) {
  val context = LocalContext.current
  val animation = remember(animationResId) { Animation.load(animationResId, context) }
  LottiePreview(animation, modifier, slotMap, clock, progress)
}
