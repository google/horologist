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

import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.MergeMode
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.FillRule
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import java.awt.geom.Area
import java.awt.geom.Path2D
import java.awt.geom.PathIterator

internal actual fun evaluateStaticBooleanMergePaths(
  mode: MergeMode,
  remainder: RemoteShape,
  last: RemoteShape,
): List<RemoteShape> {
  val resultArea = Area(shapeToAwtPath(remainder))
  val otherArea = Area(shapeToAwtPath(last))
  when (mode) {
    MergeMode.Add -> resultArea.add(otherArea)
    MergeMode.Subtract -> resultArea.subtract(otherArea)
    MergeMode.Intersect -> resultArea.intersect(otherArea)
    MergeMode.ExcludeIntersections -> resultArea.exclusiveOr(otherArea)
    else -> resultArea.add(otherArea)
  }

  val subpaths = awtPathToBezierValues(resultArea.getPathIterator(null))
  return listOf(RemoteLottiePath(subpaths, fillRule = FillRule.EvenOdd))
}

private fun shapeToAwtPath(shape: RemoteShape): Path2D.Float {
  val winding =
    if (shape is RemoteLottiePath && shape.fillRule == FillRule.EvenOdd) {
      Path2D.WIND_EVEN_ODD
    } else {
      Path2D.WIND_NON_ZERO
    }
  val bezier = shapeToBezierValues(shape)
  bezier.forEach { it.requireStaticModifierGeometry("CPU path conversion / merge paths") }
  val awtPath = Path2D.Float(winding)
  for (subpath in bezier) {
    val vertices = subpath.vertices
    val inTangents = subpath.inTangents
    val outTangents = subpath.outTangents
    if (vertices.isEmpty()) continue

    val startX = vertices[0].getOrElse(0) { 0f.rf }.constantValueOrNull ?: 0f
    val startY = vertices[0].getOrElse(1) { 0f.rf }.constantValueOrNull ?: 0f
    awtPath.moveTo(startX, startY)

    val maxIndex = if (subpath.closed) vertices.size else vertices.size - 1
    for (i in 0 until maxIndex) {
      val p0 = vertices[i]
      val lastIndex = if (i == vertices.size - 1 && subpath.closed) 0 else i + 1
      val p4 = vertices[lastIndex]
      val inTangent = inTangents.getOrNull(lastIndex)
      val outTangent = outTangents.getOrNull(i)

      val p0x = p0.getOrElse(0) { 0f.rf }.constantValueOrNull ?: 0f
      val p0y = p0.getOrElse(1) { 0f.rf }.constantValueOrNull ?: 0f
      val p4x = p4.getOrElse(0) { 0f.rf }.constantValueOrNull ?: 0f
      val p4y = p4.getOrElse(1) { 0f.rf }.constantValueOrNull ?: 0f

      val inTangentX = inTangent?.getOrElse(0) { 0f.rf }?.constantValueOrNull ?: 0f
      val inTangentY = inTangent?.getOrElse(1) { 0f.rf }?.constantValueOrNull ?: 0f
      val outTangentX = outTangent?.getOrElse(0) { 0f.rf }?.constantValueOrNull ?: 0f
      val outTangentY = outTangent?.getOrElse(1) { 0f.rf }?.constantValueOrNull ?: 0f

      if (outTangentX == 0f && outTangentY == 0f && inTangentX == 0f && inTangentY == 0f) {
        awtPath.lineTo(p4x, p4y)
      } else {
        awtPath.curveTo(
          p0x + outTangentX,
          p0y + outTangentY,
          p4x + inTangentX,
          p4y + inTangentY,
          p4x,
          p4y,
        )
      }
    }
    if (subpath.closed) {
      awtPath.closePath()
    }
  }
  return awtPath
}

private fun awtPathToBezierValues(iterator: PathIterator): List<RemoteBezierValue> {
  val subpaths = mutableListOf<RemoteBezierValue>()
  val coords = FloatArray(6)
  var currentVertices = mutableListOf<List<RemoteFloat>>()
  var currentInTangents = mutableListOf<List<RemoteFloat>>()
  var currentOutTangents = mutableListOf<List<RemoteFloat>>()
  var currentPoint = Point(0f, 0f)
  var startPoint = Point(0f, 0f)

  while (!iterator.isDone) {
    when (iterator.currentSegment(coords)) {
      PathIterator.SEG_MOVETO -> {
        if (currentVertices.isNotEmpty()) {
          subpaths.add(
            RemoteBezierValue(
              closed = false,
              vertices = currentVertices,
              inTangents = currentInTangents,
              outTangents = currentOutTangents,
            )
          )
          currentVertices = mutableListOf()
          currentInTangents = mutableListOf()
          currentOutTangents = mutableListOf()
        }
        val x = coords[0]
        val y = coords[1]
        currentPoint = Point(x, y)
        startPoint = Point(x, y)
        currentVertices.add(listOf(x.rf, y.rf))
        currentInTangents.add(listOf(0f.rf, 0f.rf))
        currentOutTangents.add(listOf(0f.rf, 0f.rf))
      }
      PathIterator.SEG_LINETO -> {
        val x = coords[0]
        val y = coords[1]
        currentPoint = Point(x, y)
        currentVertices.add(listOf(x.rf, y.rf))
        currentInTangents.add(listOf(0f.rf, 0f.rf))
        currentOutTangents.add(listOf(0f.rf, 0f.rf))
      }
      PathIterator.SEG_QUADTO -> {
        val x1 = coords[0]
        val y1 = coords[1]
        val x2 = coords[2]
        val y2 = coords[3]
        if (currentOutTangents.isNotEmpty()) {
          currentOutTangents[currentOutTangents.lastIndex] =
            listOf(((2f / 3f) * (x1 - currentPoint.x)).rf, ((2f / 3f) * (y1 - currentPoint.y)).rf)
        }
        currentPoint = Point(x2, y2)
        currentVertices.add(listOf(x2.rf, y2.rf))
        currentInTangents.add(listOf(((2f / 3f) * (x1 - x2)).rf, ((2f / 3f) * (y1 - y2)).rf))
        currentOutTangents.add(listOf(0f.rf, 0f.rf))
      }
      PathIterator.SEG_CUBICTO -> {
        val x1 = coords[0]
        val y1 = coords[1]
        val x2 = coords[2]
        val y2 = coords[3]
        val x3 = coords[4]
        val y3 = coords[5]
        if (currentOutTangents.isNotEmpty()) {
          currentOutTangents[currentOutTangents.lastIndex] =
            listOf((x1 - currentPoint.x).rf, (y1 - currentPoint.y).rf)
        }
        currentPoint = Point(x3, y3)
        currentVertices.add(listOf(x3.rf, y3.rf))
        currentInTangents.add(listOf((x2 - x3).rf, (y2 - y3).rf))
        currentOutTangents.add(listOf(0f.rf, 0f.rf))
      }
      PathIterator.SEG_CLOSE -> {
        if (currentVertices.isNotEmpty()) {
          val lastV = currentVertices.last()
          val lastX = lastV[0].constantValueOrNull ?: 0f
          val lastY = lastV[1].constantValueOrNull ?: 0f
          if (lastX == startPoint.x && lastY == startPoint.y && currentVertices.size > 1) {
            val lastInTan = currentInTangents.last()
            currentVertices.removeAt(currentVertices.lastIndex)
            currentInTangents.removeAt(currentInTangents.lastIndex)
            currentOutTangents.removeAt(currentOutTangents.lastIndex)
            currentInTangents[0] = lastInTan
          }
          subpaths.add(
            RemoteBezierValue(
              closed = true,
              vertices = currentVertices,
              inTangents = currentInTangents,
              outTangents = currentOutTangents,
            )
          )
          currentVertices = mutableListOf()
          currentInTangents = mutableListOf()
          currentOutTangents = mutableListOf()
        }
      }
    }
    iterator.next()
  }
  if (currentVertices.isNotEmpty()) {
    subpaths.add(
      RemoteBezierValue(
        closed = false,
        vertices = currentVertices,
        inTangents = currentInTangents,
        outTangents = currentOutTangents,
      )
    )
  }
  return subpaths
}
