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
import androidx.compose.remote.creation.Rc.Time.ANIMATION_TIME
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.drawWithContent
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.renderer.Layer

/**
 * Settings for the Lottie animation player.
 *
 * @property currentFrame The current frame to display.
 * @property slotMap Mapping of slot IDs to values for dynamic theming.
 */
internal data class LottieSettings(
  val currentFrame: RemoteFloat,
  val slotMap: SlotMap = SlotMap.Empty,
  val width: Float = 0f,
  val height: Float = 0f,
)

/** CompositionLocal for [LottieSettings]. */
internal val LocalAnimationSettings =
  staticCompositionLocalOf<LottieSettings> { LottieSettings(0.rf, SlotMap.Empty, 0f, 0f) }

/**
 * A RemoteComposable that loads and renders a Lottie animation from a raw resource ID.
 *
 * @param rawRes The raw resource ID of the Lottie JSON file.
 * @param modifier The modifier to apply to the Lottie layout.
 * @param slotMap Mapping of slot IDs to values for dynamic theming.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
fun LottieAnimation(
  @RawRes rawRes: Int,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap.Empty,
  progress: RemoteFloat? = null,
) {
  val context = LocalContext.current
  val animation = remember(rawRes) { Animation.load(rawRes, context) }
  LottieAnimation(animation, modifier, slotMap, progress)
}

/**
 * A RemoteComposable that loads and renders a Lottie animation from a JSON string.
 *
 * @param json The JSON string of the Lottie animation.
 * @param modifier The modifier to apply to the Lottie layout.
 * @param slotMap Mapping of slot IDs to values for dynamic theming.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
fun LottieAnimation(
  json: String,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap.Empty,
  progress: RemoteFloat? = null,
) {
  val animation = remember(json) { Animation.decodeFromString(json) }
  LottieAnimation(animation, modifier, slotMap, progress)
}

/**
 * A RemoteComposable that renders a Lottie animation to a RemoteCompose document.
 *
 * @param animation The Lottie animation to render.
 * @param modifier The modifier to apply to the Lottie layout.
 * @param slotMap Mapping of slot IDs to values for dynamic theming.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
internal fun LottieAnimation(
  animation: Animation,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap.Empty,
  progress: RemoteFloat? = null,
) {
  // Total span of frames across the animation timeline.
  val totalFrames = (animation.endFrame - animation.startFrame).toFloat()
  val startFrameRf = animation.startFrame.toFloat().rf

  // Determine current frame: if progress [0.0, 1.0] is provided (e.g. via a named variable
  // or user-driven state), map it directly to frames; otherwise drive it continuously
  // from the Remote Compose document animation clock time.
  val currentFrame =
    if (progress != null) {
      startFrameRf + (progress * totalFrames)
    } else {
      startFrameRf +
        (floor(RemoteFloat(ANIMATION_TIME) * animation.frameRate.toFloat()) % totalFrames)
    }
  val animationSettings =
    LottieSettings(
      currentFrame = currentFrame,
      slotMap = slotMap,
      width = animation.width.toFloat(),
      height = animation.height.toFloat(),
    )

  CompositionLocalProvider(LocalAnimationSettings provides animationSettings) {
    val parentTransforms =
      animation.layers
        .filter { l -> l.index != null && l.transform != null }
        .associate { l -> Pair(l.index!!, l.transform!!) }

    val lottieWidth = animation.width.rf
    val lottieHeight = animation.height.rf

    val scaleModifier = RemoteModifier.drawWithContent {
      val canvasWidth = size.width
      val canvasHeight = size.height

      val scaleX = canvasWidth / lottieWidth
      val scaleY = canvasHeight / lottieHeight
      val scale = min(scaleX, scaleY)

      val scaledWidth = lottieWidth * scale
      val scaledHeight = lottieHeight * scale
      val dx = (canvasWidth - scaledWidth) / 2.rf
      val dy = (canvasHeight - scaledHeight) / 2.rf

      translate(dx, dy) { scale(scale, scale) { drawContent() } }
    }

    RemoteBox(
      modifier = modifier.then(scaleModifier),
      // TODO: 496943072 - ANDROID_NATIVE player doesn't support clipping yet, so we need to avoid
      // clipping for now until it does. coming in cl/893506559
      // .clip(RemoteRectangleShape)
      contentAlignment = RemoteAlignment.Center,
    ) {
      for (layer in animation.layers) {
        Layer(layer, parentTransforms, null)
      }
    }
  }
}
