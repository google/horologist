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
import androidx.compose.remote.creation.RemotePath
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteDrawScope
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement.Transform

@SuppressLint("RestrictedApi")
internal interface RemoteShape {
  fun draw(drawScope: RemoteDrawScope, canvas: RemoteCanvas)
}

@SuppressLint("RestrictedApi")
internal class RemoteLottiePath(val path: RemotePath) : RemoteShape {
  override fun draw(drawScope: RemoteDrawScope, canvas: RemoteCanvas) {
    canvas.drawPath(path)
  }
}

@SuppressLint("RestrictedApi")
internal class RemoteGroup(
  val childShapes: List<StyledShapes>,
  val animationSettings: LottieSettings,
  val transform: Transform?,
) : RemoteShape {
  override fun draw(drawScope: RemoteDrawScope, canvas: RemoteCanvas) {
    for (shapeGroup in childShapes) {
      val paint = shapeGroup.style.getPaint()
      canvas.save()

      if (transform != null) {
        transform(transform, paint, animationSettings, canvas)
      }

      drawScope.usePaint(paint) {
        for (shape in shapeGroup.shapes) {
          shape.draw(drawScope, canvas)
        }
      }

      canvas.restore()
    }
  }
}
