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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.remote.core.RemoteClock
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteComposePlayerFlags
import androidx.compose.remote.player.compose.embedded.RcPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
  strictOffsetTopology: Boolean = false,
) {
  RemoteComposePlayerFlags.isEmbeddedPlayerEnabled = true
  val doc =
    rememberRemoteDocument(clock = clock) {
      // When progress is specified, bind the animation to a named RemoteFloat ("progress").
      // This allows updating progress dynamically via document.setNamedFloat("progress", value)
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
        strictOffsetTopology = strictOffsetTopology,
      )
    }
  doc.value?.let { document ->
    if (progress != null) {
      document.setNamedFloat("progress", progress)
      SideEffect { document.setNamedFloat("progress", progress) }
    }
    RcPlayer(document = document, modifier = modifier)
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

/**
 * The natural wall-clock duration of [animation] in milliseconds, derived from its frame range and
 * frame rate.
 *
 * Preview GIFs are captured over a fixed `@AnimatedPreview(durationMs = ...)` window, so keeping
 * that window aligned with this value is what makes a capture show exactly one pass of the
 * animation with no frozen tail.
 */
internal val Animation.naturalDurationMillis: Int
  get() =
    if (frameRate > 0) {
      (((endFrame - startFrame) / frameRate) * 1000).toInt().coerceAtLeast(100)
    } else {
      1000
    }

/**
 * Displays an animated Lottie animation using Remote Compose, driving the progress through a single
 * non-repeating Compose animation so that a preview capture shows exactly one pass.
 *
 * Progress ramps linearly from `0f` to `1f` over [durationMillis] and then holds at `1f`. Previews
 * are captured as a fixed-length GIF, so a looping source would replay the animation as many times
 * as it fits in the capture window; playing once and holding keeps the GIF readable and makes the
 * last captured frame the animation's end state. Set `@AnimatedPreview(durationMs = ...)` to
 * [Animation.naturalDurationMillis] (rounded up to a whole number of capture frames) so the single
 * pass fills the capture window.
 *
 * @param animation The parsed Lottie animation to play.
 * @param modifier The modifier to apply to the host layout.
 * @param slotMap Optional mapping of slot IDs to values for dynamic theming.
 * @param durationMillis Optional override for the animation duration in milliseconds. Defaults to
 *   the animation's own [Animation.naturalDurationMillis].
 */
@SuppressLint("RestrictedApi")
@Composable
internal fun LottieAnimatedPreview(
  animation: Animation,
  modifier: Modifier = Modifier,
  slotMap: SlotMap = SlotMap.Empty,
  durationMillis: Int? = null,
) {
  val duration = durationMillis ?: animation.naturalDurationMillis
  val progress = remember(animation, duration) { Animatable(0f) }
  LaunchedEffect(progress) {
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = duration, easing = LinearEasing),
    )
  }
  LottiePreview(
    animation = animation,
    modifier = modifier,
    slotMap = slotMap,
    progress = progress.value,
  )
}

/**
 * Displays an animated Lottie animation from a raw resource ID using Remote Compose, driving the
 * progress through a single non-repeating Compose animation so that a preview capture shows exactly
 * one pass.
 *
 * @param animationResId The raw resource ID of the Lottie JSON file.
 * @param modifier The modifier to apply to the host layout.
 * @param slotMap Optional mapping of slot IDs to values for dynamic theming.
 * @param durationMillis Optional override for the animation duration in milliseconds. Defaults to
 *   the animation's own [Animation.naturalDurationMillis].
 */
@SuppressLint("RestrictedApi")
@Composable
fun LottieAnimatedPreview(
  @RawRes animationResId: Int,
  modifier: Modifier = Modifier,
  slotMap: SlotMap = SlotMap.Empty,
  durationMillis: Int? = null,
) {
  val context = LocalContext.current
  val animation = remember(animationResId) { Animation.load(animationResId, context) }
  LottieAnimatedPreview(animation, modifier, slotMap, durationMillis)
}
