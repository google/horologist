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
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.remote.creation.compose.state.tan
import androidx.compose.remote.creation.compose.state.toRad
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector

/**
 * Binds a transform to its containing composition's timeline before entering a child timeline. The
 * returned properties retain RemoteFloat playback expressions, not frame-zero snapshots; evaluating
 * them again in a nested composition cannot retime those expressions.
 */
@SuppressLint("RestrictedApi")
internal fun Transform.bindToTimeline(animationSettings: LottieSettings): Transform =
  copy(
    anchorPoint = StaticPositionProperty(value = animatePosition(anchorPoint, animationSettings)),
    positionTranslation =
      StaticPositionProperty(value = animatePosition(positionTranslation, animationSettings)),
    rotation = StaticScalarProperty(value = animateScalar(rotation, animationSettings)),
    scale =
      StaticVectorProperty(animated = false.rb, value = animateVector(scale, animationSettings)),
    opacity = StaticScalarProperty(value = animateScalar(opacity, animationSettings)),
    skew = skew?.let { StaticScalarProperty(value = animateScalar(it, animationSettings)) },
    skewAxis = skewAxis?.let { StaticScalarProperty(value = animateScalar(it, animationSettings)) },
  )

@SuppressLint("RestrictedApi")
/** Clamps scale components symmetrically away from zero to avoid matrix inversion singularities. */
internal fun clampScale(scale: RemoteFloat): RemoteFloat {
  val posClamped = selectIfLt(scale, 0.0001f.rf, 0.0001f.rf, scale)
  val negClamped = selectIfLt(scale, (-0.0001f).rf, scale, (-0.0001f).rf)
  return selectIfLt(scale, 0f.rf, negClamped, posClamped)
}

@SuppressLint("RestrictedApi")
/**
 * Calculates the inverse scale factor, guarding against division-by-zero singularities when scale
 * is zero or near zero.
 */
internal fun computeInverseScale(scale: RemoteFloat): RemoteFloat {
  val clamped = clampScale(scale)
  return 1f.rf / clamped
}

@SuppressLint("RestrictedApi")
/** Applies a transform described by a Lottie [Transform] object to the RemoteCanvas. */
internal fun transform(
  transform: Transform,
  paint: RemotePaint? = null,
  animationSettings: LottieSettings,
  canvas: RemoteCanvas,
) {
  val rotation = animateScalar(transform.rotation, animationSettings)
  val translation = animatePosition(transform.positionTranslation, animationSettings)
  val opacity = animateScalar(transform.opacity, animationSettings)
  val anchorPoint = animatePosition(transform.anchorPoint, animationSettings)

  val scale = animateVector(transform.scale, animationSettings)

  val scaleX = scale[0] / 100f
  val scaleY = scale[1] / 100f
  val safeScaleX = clampScale(scaleX)
  val safeScaleY = clampScale(scaleY)

  canvas.translate(translation.x, translation.y)
  canvas.rotate(rotation)

  val skew = transform.skew?.let { animateScalar(it, animationSettings) }
  val skewAxis = transform.skewAxis?.let { animateScalar(it, animationSettings) }
  if (skew != null) {
    val axis = skewAxis ?: 0f.rf
    canvas.rotate(90f.rf - axis)
    canvas.internalCanvas.skew(0f.rf, tan(toRad(skew)))
    canvas.rotate(axis - 90f.rf)
  }

  canvas.scale(safeScaleX, safeScaleY)
  canvas.translate(-anchorPoint.x, -anchorPoint.y)

  paint?.let { it.color = it.color.copy(alpha = opacity / 100f) }
}

@SuppressLint("RestrictedApi")
/**
 * Inverts the transform operations described by a Lottie [Transform] object on the RemoteCanvas.
 */
internal fun inverseTransform(
  transform: Transform,
  animationSettings: LottieSettings,
  canvas: RemoteCanvas,
) {
  val rotation = animateScalar(transform.rotation, animationSettings)
  val translation = animatePosition(transform.positionTranslation, animationSettings)
  val anchorPoint = animatePosition(transform.anchorPoint, animationSettings)

  val scale = animateVector(transform.scale, animationSettings)

  val scaleX = scale[0] / 100f
  val scaleY = scale[1] / 100f

  val invScaleX = computeInverseScale(scaleX)
  val invScaleY = computeInverseScale(scaleY)

  canvas.translate(anchorPoint.x, anchorPoint.y)
  canvas.scale(invScaleX, invScaleY)

  val skew = transform.skew?.let { animateScalar(it, animationSettings) }
  val skewAxis = transform.skewAxis?.let { animateScalar(it, animationSettings) }
  if (skew != null) {
    val axis = skewAxis ?: 0f.rf
    canvas.rotate(90f.rf - axis)
    canvas.internalCanvas.skew(0f.rf, -tan(toRad(skew)))
    canvas.rotate(axis - 90f.rf)
  }

  canvas.rotate(-rotation)
  canvas.translate(-translation.x, -translation.y)
}
