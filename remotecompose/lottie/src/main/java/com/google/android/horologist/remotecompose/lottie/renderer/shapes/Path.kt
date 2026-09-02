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
import androidx.compose.remote.creation.RemotePath
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Path
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateBezier

/** Evaluates a Lottie [Path] into a [RemoteLottiePath]. */
@SuppressLint("RestrictedApi")
internal fun path(lottiePath: Path, animationSettings: LottieSettings): RemoteLottiePath {
  val path = animateBezier(lottiePath.shape, animationSettings)
  val vertices = path.vertices
  val inTangents = path.inTangents
  val outTangents = path.outTangents

  val rcPath = RemotePath()
  rcPath.reset()
  rcPath.moveTo(vertices[0][0], vertices[0][1])

  for (i in vertices.indices) {
    val p0 = vertices[i]
    val lastIndex = if (i == vertices.size - 1 && path.closed) 0 else i + 1
    val p4 = vertices[lastIndex]
    val inTangent = inTangents[lastIndex]
    val outTangent = outTangents[i]
    val p1 = listOf(p0[0] + outTangent[0], p0[1] + outTangent[1])
    val p2 = listOf(p4[0] + inTangent[0], p4[1] + inTangent[1])

    rcPath.cubicTo(p1[0], p1[1], p2[0], p2[1], p4[0], p4[1])
  }

  return RemoteLottiePath(rcPath)
}
