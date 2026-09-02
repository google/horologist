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
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Rectangle
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector

/** Evaluates a Lottie [Rectangle] into a [RemoteLottiePath]. */
@SuppressLint("RestrictedApi")
internal fun rectangle(rect: Rectangle, animationSettings: LottieSettings): RemoteLottiePath? {
  if (rect.hidden == true) return null

  val pos = animatePosition(rect.position, animationSettings)
  val posX = pos.x.constantValueOrNull ?: 0f
  val posY = pos.y.constantValueOrNull ?: 0f

  val size = animateVector(rect.size, animationSettings)
  val width = size.getOrNull(0)?.constantValueOrNull ?: 0f
  val height = size.getOrNull(1)?.constantValueOrNull ?: 0f
  val halfWidth = width / 2f
  val halfHeight = height / 2f

  val cornerRadius = animateScalar(rect.cornerRadius, animationSettings).constantValueOrNull ?: 0f
  val maxRadius = minOf(halfWidth, halfHeight)
  val r = cornerRadius.coerceIn(0f, maxRadius)

  val rcPath = RemotePath()
  rcPath.reset()

  if (r == 0f) {
    rcPath.moveTo(posX + halfWidth, posY - halfHeight)
    rcPath.lineTo(posX + halfWidth, posY + halfHeight)
    rcPath.lineTo(posX - halfWidth, posY + halfHeight)
    rcPath.lineTo(posX - halfWidth, posY - halfHeight)
    rcPath.close()
  } else {
    val k = r * 0.55228475f
    rcPath.moveTo(posX + halfWidth, posY - halfHeight + r)
    rcPath.lineTo(posX + halfWidth, posY + halfHeight - r)
    rcPath.cubicTo(
      posX + halfWidth,
      posY + halfHeight - r + k,
      posX + halfWidth - r + k,
      posY + halfHeight,
      posX + halfWidth - r,
      posY + halfHeight,
    )
    rcPath.lineTo(posX - halfWidth + r, posY + halfHeight)
    rcPath.cubicTo(
      posX - halfWidth + r - k,
      posY + halfHeight,
      posX - halfWidth,
      posY + halfHeight - r + k,
      posX - halfWidth,
      posY + halfHeight - r,
    )
    rcPath.lineTo(posX - halfWidth, posY - halfHeight + r)
    rcPath.cubicTo(
      posX - halfWidth,
      posY - halfHeight + r - k,
      posX - halfWidth + r - k,
      posY - halfHeight,
      posX - halfWidth + r,
      posY - halfHeight,
    )
    rcPath.lineTo(posX + halfWidth - r, posY - halfHeight)
    rcPath.cubicTo(
      posX + halfWidth - r + k,
      posY - halfHeight,
      posX + halfWidth,
      posY - halfHeight + r - k,
      posX + halfWidth,
      posY - halfHeight + r,
    )
    rcPath.close()
  }

  return RemoteLottiePath(rcPath)
}
