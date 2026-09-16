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

package com.google.android.horologist.remotecompose.lottie.renderer.shapes

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Ellipse
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.RoundedCorners
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector

private const val ELLIPSE_CONTROL_POINT_CONSTANT = 0.55228f

/** Evaluates a Lottie [Ellipse] parametric shape into a [RemoteLottiePath]. */
@SuppressLint("RestrictedApi")
internal fun evaluateEllipse(
  el: Ellipse,
  animationSettings: LottieSettings,
  trimPath: TrimPath? = null,
  roundedCorners: RoundedCorners? = null,
): RemoteLottiePath? {
  if (el.hidden?.constantValue == true) return null

  val pos = animatePosition(el.position, animationSettings)
  val size = animateVector(el.size, animationSettings)
  val width = size.getOrElse(0) { 0f.rf }
  val height = size.getOrElse(1) { 0f.rf }
  val halfWidth = width / 2f
  val halfHeight = height / 2f

  val cpW = halfWidth * ELLIPSE_CONTROL_POINT_CONSTANT
  val cpH = halfHeight * ELLIPSE_CONTROL_POINT_CONSTANT

  val vertices: List<List<RemoteFloat>>
  val inTangents: List<List<RemoteFloat>>
  val outTangents: List<List<RemoteFloat>>

  if (el.direction == 3) {
    // Reversed (counter-clockwise)
    vertices =
      listOf(
        listOf(pos.x, pos.y - halfHeight),
        listOf(pos.x - halfWidth, pos.y),
        listOf(pos.x, pos.y + halfHeight),
        listOf(pos.x + halfWidth, pos.y),
      )
    inTangents =
      listOf(listOf(cpW, 0f.rf), listOf(0f.rf, -cpH), listOf(-cpW, 0f.rf), listOf(0f.rf, cpH))
    outTangents =
      listOf(listOf(-cpW, 0f.rf), listOf(0f.rf, cpH), listOf(cpW, 0f.rf), listOf(0f.rf, -cpH))
  } else {
    // Clockwise
    vertices =
      listOf(
        listOf(pos.x, pos.y - halfHeight),
        listOf(pos.x + halfWidth, pos.y),
        listOf(pos.x, pos.y + halfHeight),
        listOf(pos.x - halfWidth, pos.y),
      )
    inTangents =
      listOf(listOf(-cpW, 0f.rf), listOf(0f.rf, -cpH), listOf(cpW, 0f.rf), listOf(0f.rf, cpH))
    outTangents =
      listOf(listOf(cpW, 0f.rf), listOf(0f.rf, cpH), listOf(-cpW, 0f.rf), listOf(0f.rf, -cpH))
  }

  val remoteBezier =
    RemoteBezierValue(
      closed = true,
      inTangents = inTangents,
      outTangents = outTangents,
      vertices = vertices,
    )

  // Ellipses have no sharp corners for a rounding modifier to replace.
  return trimParametricPath(remoteBezier, trimPath, animationSettings)
}
