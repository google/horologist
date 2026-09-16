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
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.OffsetPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar

/** Evaluates an [OffsetPath] modifier across [shapes], expanding or shrinking path contours. */
@SuppressLint("RestrictedApi")
internal fun evaluateOffsetPath(
  shapes: List<RemoteShape>,
  offsetPath: OffsetPath,
  animationSettings: LottieSettings,
): List<RemoteShape> {
  if (offsetPath.hidden?.constantValue == true || shapes.isEmpty()) return shapes

  val amount = animateScalar(offsetPath.amount, animationSettings)
  require(amount.constantValueOrNull?.isFinite() != false) { "OffsetPath requires a finite amount" }
  if (amount.constantValueOrNull == 0f) return shapes

  val miterLimit =
    offsetPath.miterLimit?.let { animateScalar(it, animationSettings) }
      ?: (offsetPath.miterLimitNumeric ?: 4f).rf
  require(miterLimit.constantValueOrNull?.isFinite() != false) {
    "OffsetPath requires a finite miter limit"
  }

  return shapes.map { shape ->
    when (shape) {
      is RemoteBooleanPath -> error("OffsetPath after a live boolean merge is not yet supported")
      is RemoteLottiePath -> {
        val source = shape.materializeTrim()
        val newSubpaths =
          source.path.map { subpath ->
            if (
              (subpath.inTangents + subpath.outTangents).flatten().all {
                it.constantValueOrNull == 0f
              }
            ) {
              offsetPolygon(subpath, amount, offsetPath.lineJoin, miterLimit)
            } else {
              offsetCurves(subpath, amount, offsetPath.lineJoin, miterLimit)
            }
          }
        source.withPath(newSubpaths)
      }
      is RemoteGroup -> {
        val newChildShapes =
          shape.childShapes.map { styledShapes ->
            com.google.android.horologist.remotecompose.lottie.renderer.StyledShapes(
              shapes = evaluateOffsetPath(styledShapes.shapes, offsetPath, animationSettings),
              style = styledShapes.style,
            )
          }
        RemoteGroup(
          newChildShapes,
          shape.animationSettings,
          shape.transform,
          shape.opacityMultiplier,
        )
      }
      else -> shape
    }
  }
}
