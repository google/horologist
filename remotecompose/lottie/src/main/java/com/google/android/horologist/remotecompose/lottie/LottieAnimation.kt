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
import androidx.compose.remote.creation.Rc.Time.ANIMATION_TIME
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.size
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.toRemoteDp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement.Transform
import com.google.android.horologist.remotecompose.lottie.renderer.Layer
import com.google.android.horologist.remotecompose.lottie.renderer.SlotMap

/**
 * Settings for the Lottie animation player.
 *
 * @property currentFrame The current frame to display.
 * @property slotMap Mapping of slot IDs to values for dynamic theming.
 */
data class LottieSettings(val currentFrame: RemoteFloat, val slotMap: SlotMap)

/** CompositionLocal for [LottieSettings]. */
val LocalAnimationSettings =
  staticCompositionLocalOf<LottieSettings> { LottieSettings(0.rf, SlotMap(emptyMap())) }

/**
 * A RemoteComposable that renders a Lottie animation to a RemoteCompose document.
 *
 * @param animation The Lottie animation to render.
 * @param transform Optional additional transform to apply to the requested shape.
 * @param slotMap Mapping of slot IDs to values for dynamic theming.
 * @param modifier The modifier to apply to the Lottie layout.
 */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
fun LottieAnimation(
  animation: Animation,
  transform: Transform? = null,
  slotMap: SlotMap = SlotMap(emptyMap()),
  modifier: RemoteModifier = RemoteModifier,
) {
  val totalFrames = animation.endFrame - animation.startFrame
  val currentFrame =
    floor(RemoteFloat(ANIMATION_TIME) * animation.frameRate.toFloat()) % totalFrames.toFloat()
  val animationSettings = LottieSettings(currentFrame, slotMap)

  CompositionLocalProvider(LocalAnimationSettings provides animationSettings) {
    val widthDp = animation.width.rf.toRemoteDp()
    val heightDp = animation.height.rf.toRemoteDp()

    val parentTransforms =
      animation.layers
        .filter { l -> l.index != null && l.transform != null }
        .associate { l -> Pair(l.index!!, l.transform!!) }

    RemoteBox(
      modifier = modifier.size(widthDp, heightDp),
      // TODO: 496943072 - ANDROID_NATIVE player doesn't support clipping yet, so we need to avoid
      // clipping for now until it does. coming in cl/893506559
      // .clip(RemoteRectangleShape)
      contentAlignment = RemoteAlignment.Center,
    ) {
      for (layer in animation.layers) {
        Layer(layer, parentTransforms, transform)
      }
    }
  }
}
