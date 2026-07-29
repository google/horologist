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

package com.google.android.horologist.remotecompose.lottie.renderer

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement.Transform

@SuppressLint("RestrictedApi")
/** Applies a transform described by a Lottie [Transform] object to the RemoteCanvas. */
internal fun transform(
  transform: Transform,
  paint: RemotePaint,
  animationSettings: LottieSettings,
  canvas: RemoteCanvas,
) {
  val rotation = transform.rotation.value.rf
  val (translateX, translateY) =
    Pair(transform.positionTranslation.value[0].rf, transform.positionTranslation.value[1].rf)
  val opacity = transform.opacity.value
  val (anchorPointX, anchorPointY) =
    Pair(transform.anchorPoint.value[0].rf, transform.anchorPoint.value[1].rf)

  val scale = animateVector(transform.scale, animationSettings)

  val scaleX = scale[0] / 100f
  val scaleY = scale[1] / 100f

  canvas.translate(translateX, translateY)
  canvas.rotate(rotation)
  canvas.scale(scaleX, scaleY)
  canvas.translate(-anchorPointX, -anchorPointY)

  paint.color = paint.color.copy(alpha = (opacity / 100f).rf)
}
