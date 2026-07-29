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
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.runtime.Composable
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement.Fill
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement.Group
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement.Path
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement.Transform
import com.google.android.horologist.remotecompose.lottie.format.ShapeType

internal data class StyledShapes(val shapes: List<RemoteShape>, val style: RemoteStyle)

/** Renders a list of Lottie Shapes to the RemoteCanvas. */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
internal fun RenderShapes(shapes: List<GraphicElement>, transformStack: List<Transform>) {
  val animationSettings = LocalAnimationSettings.current
  val shapeGroups = gatherShapes(shapes, animationSettings)

  RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
    for (shapeGroup in shapeGroups) {
      val paint = shapeGroup.style.getPaint()

      for (transform in transformStack) {
        remoteCanvas.save()
        transform(transform, paint, animationSettings, remoteCanvas)
      }

      usePaint(paint) {
        for (shape in shapeGroup.shapes) {
          shape.draw(this, remoteCanvas)
        }
      }

      for (transform in transformStack) {
        remoteCanvas.restore()
      }
    }
  }
}

private fun gatherShapes(
  shapes: List<GraphicElement>,
  animationSettings: LottieSettings,
): List<StyledShapes> {
  val shapeGroups = mutableListOf<StyledShapes>()
  var currentShapes = mutableListOf<RemoteShape>()

  for (shape in shapes.reversed()) {
    when (shape) {
      is GraphicElement.Path -> currentShapes.addIfNotNull(path(shape, animationSettings))
      is GraphicElement.Group -> currentShapes.addIfNotNull(group(shape, animationSettings))
      is GraphicElement.Fill -> {
        val fill = fill(shape, animationSettings)
        shapeGroups.add(StyledShapes(currentShapes, fill))
        currentShapes = mutableListOf()
      }
      is GraphicElement.Transform -> {} // No-op - handled groups
    }
  }

  // Groups don't have to have styling information associated with them, because the child nodes
  // can have styles instead. If there's a Group node left over that doesn't have a style, add
  // it to the render tree anyway
  if (currentShapes.isNotEmpty() && currentShapes.all { it is RemoteGroup }) {
    shapeGroups.add(StyledShapes(currentShapes, NoopStyle()))
  }

  return shapeGroups
}

private fun group(group: Group, animationSettings: LottieSettings): RemoteGroup? {
  if (group.hidden == true) {
    return null
  }

  val reversed = group.shapes.reversed()

  if (reversed.firstOrNull()?.type == ShapeType.Transform) {
    val transform = reversed[0] as Transform
    val styledShapes = gatherShapes(reversed.drop(1), animationSettings)
    return RemoteGroup(styledShapes, animationSettings, transform)
  } else {
    return RemoteGroup(gatherShapes(reversed, animationSettings), animationSettings, null)
  }
}

@SuppressLint("RestrictedApi")
private fun path(lottiePath: Path, animationSettings: LottieSettings): RemoteLottiePath {
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

private fun fill(fill: Fill, animationSettings: LottieSettings): RemoteFill {
  val slotId = fill.color.slotId
  val remoteColor = if (slotId != null) animationSettings.slotMap.getColor(slotId) else null
  return RemoteFill(remoteColor ?: fill.color.value)
}

private fun MutableList<RemoteShape>.addIfNotNull(shape: RemoteShape?) {
  if (shape != null) {
    this.add(shape)
  }
}
