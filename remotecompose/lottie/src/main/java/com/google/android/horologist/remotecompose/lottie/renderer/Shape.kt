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
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Ellipse
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Path
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStar
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStarType
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Rectangle
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Group
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.Fill
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

internal data class StyledShapes(val shapes: List<RemoteShape>, val style: RemoteStyle)

/** Renders a list of Lottie Shapes to the RemoteCanvas. */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
internal fun RenderShapes(shapes: List<GraphicElement>, transformStack: List<Transform>) {
  val animationSettings = LocalAnimationSettings.current
  val shapeGroups = gatherShapes(shapes, animationSettings)

  // Aspect-ratio scaling and centering is applied once, at the top level, by the
  // drawWithContent modifier in LottieAnimation - shapes draw in raw Lottie coordinates here.
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
      is Path -> currentShapes.addIfNotNull(path(shape, animationSettings))
      is Rectangle -> currentShapes.addIfNotNull(rectangle(shape, animationSettings))
      is Ellipse -> currentShapes.addIfNotNull(ellipse(shape, animationSettings))
      is PolyStar -> currentShapes.addIfNotNull(polyStar(shape, animationSettings))
      is Group -> currentShapes.addIfNotNull(group(shape, animationSettings))
      is Fill -> {
        val fill = fill(shape, animationSettings)
        shapeGroups.add(StyledShapes(currentShapes, fill))
        currentShapes = mutableListOf()
      }
      is Transform -> {} // No-op - handled groups
      else -> {}
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

// Note: We deliberately do not use `androidx.graphics.shapes.RoundedPolygon` here because:
// 1. Lottie defines its own exact Bézier tangent calculation and rounding constants (0.47829 for
// stars,
//    0.25 for polygons) matching Bodymovin/After Effects and lottie-android, whereas RoundedPolygon
//    cuts corner arcs with a different circular/smoothed curvature profile.
// 2. Lottie polystars support fractional points (e.g. 5.5 points) smoothly morphing the last
// vertex,
//    while RoundedPolygon requires an integer vertex count.
// 3. Lottie shapes support explicit path direction (e.g. counter-clockwise d=3) affecting fill
// winding rules.
// 4. Writing directly into RemotePath avoids intermediate allocations and preserves 1:1 visual
// parity.

@SuppressLint("RestrictedApi")
private fun rectangle(rect: Rectangle, animationSettings: LottieSettings): RemoteLottiePath? {
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

@SuppressLint("RestrictedApi")
private fun ellipse(el: Ellipse, animationSettings: LottieSettings): RemoteLottiePath? {
  if (el.hidden == true) return null

  val pos = animatePosition(el.position, animationSettings)
  val posX = pos.x.constantValueOrNull ?: 0f
  val posY = pos.y.constantValueOrNull ?: 0f

  val size = animateVector(el.size, animationSettings)
  val width = size.getOrNull(0)?.constantValueOrNull ?: 0f
  val height = size.getOrNull(1)?.constantValueOrNull ?: 0f
  val halfWidth = width / 2f
  val halfHeight = height / 2f

  val cpW = halfWidth * 0.55228f
  val cpH = halfHeight * 0.55228f

  val rcPath = RemotePath()
  rcPath.reset()

  if (el.direction == 3) {
    // Reversed (counter-clockwise)
    rcPath.moveTo(posX, posY - halfHeight)
    rcPath.cubicTo(
      posX - cpW,
      posY - halfHeight,
      posX - halfWidth,
      posY - cpH,
      posX - halfWidth,
      posY,
    )
    rcPath.cubicTo(
      posX - halfWidth,
      posY + cpH,
      posX - cpW,
      posY + halfHeight,
      posX,
      posY + halfHeight,
    )
    rcPath.cubicTo(
      posX + cpW,
      posY + halfHeight,
      posX + halfWidth,
      posY + cpH,
      posX + halfWidth,
      posY,
    )
    rcPath.cubicTo(
      posX + halfWidth,
      posY - cpH,
      posX + cpW,
      posY - halfHeight,
      posX,
      posY - halfHeight,
    )
    rcPath.close()
  } else {
    // Clockwise
    rcPath.moveTo(posX, posY - halfHeight)
    rcPath.cubicTo(
      posX + cpW,
      posY - halfHeight,
      posX + halfWidth,
      posY - cpH,
      posX + halfWidth,
      posY,
    )
    rcPath.cubicTo(
      posX + halfWidth,
      posY + cpH,
      posX + cpW,
      posY + halfHeight,
      posX,
      posY + halfHeight,
    )
    rcPath.cubicTo(
      posX - cpW,
      posY + halfHeight,
      posX - halfWidth,
      posY + cpH,
      posX - halfWidth,
      posY,
    )
    rcPath.cubicTo(
      posX - halfWidth,
      posY - cpH,
      posX - cpW,
      posY - halfHeight,
      posX,
      posY - halfHeight,
    )
    rcPath.close()
  }

  return RemoteLottiePath(rcPath)
}

@SuppressLint("RestrictedApi")
private fun polyStar(star: PolyStar, animationSettings: LottieSettings): RemoteLottiePath? {
  if (star.hidden == true) return null

  val pos = animatePosition(star.position, animationSettings)
  val posX = pos.x.constantValueOrNull ?: 0f
  val posY = pos.y.constantValueOrNull ?: 0f

  val points = animateScalar(star.points, animationSettings).constantValueOrNull ?: 0f
  val rotation = animateScalar(star.rotation, animationSettings).constantValueOrNull ?: 0f
  val outerRadius = animateScalar(star.outerRadius, animationSettings).constantValueOrNull ?: 0f
  val outerRoundedness =
    (animateScalar(star.outerRoundedness, animationSettings).constantValueOrNull ?: 0f) / 100f

  val rcPath =
    when (star.starType) {
      PolyStarType.Star -> {
        val innerRadius =
          star.innerRadius?.let { animateScalar(it, animationSettings).constantValueOrNull } ?: 0f
        val innerRoundedness =
          (star.innerRoundedness?.let { animateScalar(it, animationSettings).constantValueOrNull }
            ?: 0f) / 100f
        createStarPath(
          points = points,
          positionX = posX,
          positionY = posY,
          rotation = rotation,
          innerRadius = innerRadius,
          outerRadius = outerRadius,
          innerRoundedness = innerRoundedness,
          outerRoundedness = outerRoundedness,
        )
      }
      PolyStarType.Polygon -> {
        createPolygonPath(
          points = points,
          positionX = posX,
          positionY = posY,
          rotation = rotation,
          radius = outerRadius,
          roundedness = outerRoundedness,
        )
      }
    }

  return RemoteLottiePath(rcPath)
}

@SuppressLint("RestrictedApi")
private fun createStarPath(
  points: Float,
  positionX: Float,
  positionY: Float,
  rotation: Float,
  innerRadius: Float,
  outerRadius: Float,
  innerRoundedness: Float,
  outerRoundedness: Float,
): RemotePath {
  val path = RemotePath()
  path.reset()

  var currentAngle = Math.toRadians((rotation - 90.0)).toFloat()
  val anglePerPoint = (2.0 * PI / points).toFloat()
  val halfAnglePerPoint = anglePerPoint / 2.0f
  val partialPointAmount = points - points.toInt()

  var x: Float
  var y: Float
  var previousX: Float
  var previousY: Float
  var partialPointRadius = 0f

  if (partialPointAmount != 0f) {
    partialPointRadius = innerRadius + partialPointAmount * (outerRadius - innerRadius)
    x = (partialPointRadius * cos(currentAngle.toDouble())).toFloat()
    y = (partialPointRadius * sin(currentAngle.toDouble())).toFloat()
    path.moveTo(x + positionX, y + positionY)
    currentAngle += anglePerPoint * partialPointAmount / 2f
  } else {
    x = (outerRadius * cos(currentAngle.toDouble())).toFloat()
    y = (outerRadius * sin(currentAngle.toDouble())).toFloat()
    path.moveTo(x + positionX, y + positionY)
    currentAngle += halfAnglePerPoint
  }

  var longSegment = false
  val numPoints = ceil(points.toDouble()).toInt() * 2
  for (i in 0 until numPoints) {
    var radius = if (longSegment) outerRadius else innerRadius
    var dTheta = halfAnglePerPoint
    if (partialPointRadius != 0f && i == numPoints - 2) {
      dTheta = anglePerPoint * partialPointAmount / 2f
    }
    if (partialPointRadius != 0f && i == numPoints - 1) {
      radius = partialPointRadius
    }
    previousX = x
    previousY = y
    x = (radius * cos(currentAngle.toDouble())).toFloat()
    y = (radius * sin(currentAngle.toDouble())).toFloat()

    if (innerRoundedness == 0f && outerRoundedness == 0f) {
      path.lineTo(x + positionX, y + positionY)
    } else {
      val cp1Theta = (atan2(previousY.toDouble(), previousX.toDouble()) - PI / 2.0).toFloat()
      val cp1Dx = cos(cp1Theta.toDouble()).toFloat()
      val cp1Dy = sin(cp1Theta.toDouble()).toFloat()

      val cp2Theta = (atan2(y.toDouble(), x.toDouble()) - PI / 2.0).toFloat()
      val cp2Dx = cos(cp2Theta.toDouble()).toFloat()
      val cp2Dy = sin(cp2Theta.toDouble()).toFloat()

      val cp1Roundedness = if (longSegment) innerRoundedness else outerRoundedness
      val cp2Roundedness = if (longSegment) outerRoundedness else innerRoundedness
      val cp1Radius = if (longSegment) innerRadius else outerRadius
      val cp2Radius = if (longSegment) outerRadius else innerRadius

      var cp1x = cp1Radius * cp1Roundedness * 0.47829f * cp1Dx
      var cp1y = cp1Radius * cp1Roundedness * 0.47829f * cp1Dy
      var cp2x = cp2Radius * cp2Roundedness * 0.47829f * cp2Dx
      var cp2y = cp2Radius * cp2Roundedness * 0.47829f * cp2Dy
      if (partialPointAmount != 0f) {
        if (i == 0) {
          cp1x *= partialPointAmount
          cp1y *= partialPointAmount
        } else if (i == numPoints - 1) {
          cp2x *= partialPointAmount
          cp2y *= partialPointAmount
        }
      }

      path.cubicTo(
        previousX - cp1x + positionX,
        previousY - cp1y + positionY,
        x + cp2x + positionX,
        y + cp2y + positionY,
        x + positionX,
        y + positionY,
      )
    }

    currentAngle += dTheta
    longSegment = !longSegment
  }

  path.close()
  return path
}

@SuppressLint("RestrictedApi")
private fun createPolygonPath(
  points: Float,
  positionX: Float,
  positionY: Float,
  rotation: Float,
  radius: Float,
  roundedness: Float,
): RemotePath {
  val path = RemotePath()
  path.reset()

  val pts = floor(points.toDouble()).toInt()
  var currentAngle = Math.toRadians((rotation - 90.0)).toFloat()
  val anglePerPoint = (2.0 * PI / pts).toFloat()

  var x = (radius * cos(currentAngle.toDouble())).toFloat()
  var y = (radius * sin(currentAngle.toDouble())).toFloat()
  path.moveTo(x + positionX, y + positionY)
  currentAngle += anglePerPoint

  var previousX: Float
  var previousY: Float
  val numPoints = ceil(points.toDouble()).toInt()
  for (i in 0 until numPoints) {
    previousX = x
    previousY = y
    x = (radius * cos(currentAngle.toDouble())).toFloat()
    y = (radius * sin(currentAngle.toDouble())).toFloat()

    if (roundedness != 0f) {
      val cp1Theta = (atan2(previousY.toDouble(), previousX.toDouble()) - PI / 2.0).toFloat()
      val cp1Dx = cos(cp1Theta.toDouble()).toFloat()
      val cp1Dy = sin(cp1Theta.toDouble()).toFloat()

      val cp2Theta = (atan2(y.toDouble(), x.toDouble()) - PI / 2.0).toFloat()
      val cp2Dx = cos(cp2Theta.toDouble()).toFloat()
      val cp2Dy = sin(cp2Theta.toDouble()).toFloat()

      val cp1x = radius * roundedness * 0.25f * cp1Dx
      val cp1y = radius * roundedness * 0.25f * cp1Dy
      val cp2x = radius * roundedness * 0.25f * cp2Dx
      val cp2y = radius * roundedness * 0.25f * cp2Dy

      path.cubicTo(
        previousX - cp1x + positionX,
        previousY - cp1y + positionY,
        x + cp2x + positionX,
        y + cp2y + positionY,
        x + positionX,
        y + positionY,
      )
    } else {
      if (i == numPoints - 1) {
        continue
      }
      path.lineTo(x + positionX, y + positionY)
    }

    currentAngle += anglePerPoint
  }

  path.close()
  return path
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
