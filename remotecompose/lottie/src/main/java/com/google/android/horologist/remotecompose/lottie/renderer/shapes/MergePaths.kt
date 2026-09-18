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
import android.graphics.Path
import android.graphics.PathIterator
import android.graphics.PathMeasure
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.remote.core.operations.PathCombine
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.MergeMode
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.MergePaths
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.FillRule
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.GeometryIdentity
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteCompiledPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import kotlin.math.abs

/**
 * Evaluates a [MergePaths] modifier across a collection of [RemoteShape] geometries, performing
 * either contour concatenation (Merge) or boolean path operations (Add, Subtract, Intersect,
 * ExcludeIntersections).
 */
@SuppressLint("RestrictedApi")
internal fun evaluateMergePaths(
  shapes: List<RemoteShape>,
  mergePaths: MergePaths,
  animationSettings: LottieSettings,
): List<RemoteShape> {
  if (mergePaths.hidden?.constantValue == true || shapes.isEmpty()) return emptyList()

  val fillRule =
    (shapes.firstOrNull() as? RemoteLottiePath)?.fillRule
      ?: (shapes.firstOrNull() as? RemoteCompiledPath)?.fillRule
      ?: FillRule.NonZero

  return (when (mergePaths.mode) {
      MergeMode.Merge -> {
        listOf(concatenateOperands(shapes.map(::normalizeMergeOperand), fillRule))
      }
      MergeMode.Add,
      MergeMode.Subtract,
      MergeMode.Intersect,
      MergeMode.ExcludeIntersections -> {
        val op =
          when (mergePaths.mode) {
            MergeMode.Add -> Path.Op.UNION
            MergeMode.Subtract -> Path.Op.DIFFERENCE
            MergeMode.Intersect -> Path.Op.INTERSECT
            MergeMode.ExcludeIntersections -> Path.Op.XOR
            else -> Path.Op.UNION
          }

        val operands = shapes.map(::normalizeMergeOperand)
        // Lottie combines the last authored operand with the concatenation of all earlier
        // contours. It does not reduce a sequence of pairwise boolean operations.
        val remainder = concatenateOperands(operands.dropLast(1))
        val last = operands.last()
        val live = operands.any { operand ->
          operand is RemoteBooleanPath ||
            (operand as RemoteLottiePath).path.any { path ->
              (path.vertices + path.inTangents + path.outTangents).flatten().any {
                it.constantValueOrNull == null
              }
            }
        }
        if (live) {
          val remoteOp =
            when (mergePaths.mode) {
              MergeMode.Add -> PathCombine.OP_UNION
              MergeMode.Subtract -> PathCombine.OP_DIFFERENCE
              MergeMode.Intersect -> PathCombine.OP_INTERSECT
              MergeMode.ExcludeIntersections -> PathCombine.OP_XOR
              else -> error("Not a boolean operation")
            }
          return listOf(RemoteBooleanPath(remainder, last, remoteOp))
        }
        val resultPath = Path()
        check(resultPath.op(shapeToAndroidPath(remainder), shapeToAndroidPath(last), op)) {
          "Boolean path combination failed"
        }

        val subpaths = androidPathToBezierValues(resultPath)
        // Skia boolean results can use EVEN_ODD. Dropping that winding can fill excluded holes.
        val resultFillRule =
          if (resultPath.fillType == Path.FillType.EVEN_ODD) FillRule.EvenOdd else FillRule.NonZero
        listOf(RemoteLottiePath(subpaths, fillRule = resultFillRule))
      }
    })
    .map { if (it is RemoteLottiePath) it.withIdentity(GeometryIdentity(mergePaths)) else it }
}

/** Retains operation trees and group boundaries without extracting live result vertices. */
private fun normalizeMergeOperand(shape: RemoteShape): RemoteShape =
  when (shape) {
    is RemoteBooleanPath -> shape
    is RemoteLottiePath -> {
      val source = shape.materializeTrim()
      require(source.trim == null) { "Merges of playback-trimmed paths are not yet supported" }
      source.resolveGeometry()
    }
    is RemoteGroup -> {
      val operand =
        concatenateOperands(shape.childShapes.flatMap { it.shapes }.map(::normalizeMergeOperand))
      if (shape.transform == null) operand
      else
        transformRemoteShape(operand, shape.transform, shape.animationSettings)
          .let(::normalizeMergeOperand)
    }
    else -> RemoteLottiePath(shapeToBezierValues(shape))
  }

private fun concatenateOperands(
  shapes: List<RemoteShape>,
  fillRule: FillRule = FillRule.NonZero,
): RemoteShape {
  if (shapes.size == 1) return shapes.single()
  // Union is not contour concatenation: winding and cancellation may differ. Alpha19's
  // pathAppend accepts coordinates, not the path IDs produced by pathCombine.
  require(shapes.all { it is RemoteLottiePath }) {
    "Runtime Boolean contour concatenation is not yet supported"
  }
  return RemoteLottiePath(shapes.flatMap { (it as RemoteLottiePath).path }, fillRule)
}

@SuppressLint("RestrictedApi")
internal fun shapeToAndroidPath(shape: RemoteShape): Path {
  return when (shape) {
    is RemoteBooleanPath -> error("A live boolean result cannot be converted to a CPU path")
    is RemoteLottiePath ->
      buildAndroidPathFromBezier(shape.resolveGeometry().path).apply {
        fillType =
          if (shape.fillRule == FillRule.EvenOdd) Path.FillType.EVEN_ODD else Path.FillType.WINDING
      }
    is RemoteGroup -> buildAndroidPathFromBezier(shapeToBezierValues(shape))
    else -> Path()
  }
}

@SuppressLint("RestrictedApi")
internal fun shapeToBezierValues(shape: RemoteShape): List<RemoteBezierValue> {
  return when (shape) {
    is RemoteBooleanPath -> error("A live boolean result cannot be converted to Bezier vertices")
    is RemoteLottiePath -> shape.resolveGeometry().path
    is RemoteGroup -> {
      val subpaths = mutableListOf<RemoteBezierValue>()
      for (group in shape.childShapes) {
        for (child in group.shapes) {
          subpaths.addAll(shapeToBezierValues(child))
        }
      }
      if (shape.transform == null) subpaths
      else subpaths.map { transformBezierValue(it, shape.transform, shape.animationSettings) }
    }
    else -> {
      val androidPath = shapeToAndroidPath(shape)
      androidPathToBezierValues(androidPath)
    }
  }
}

@SuppressLint("RestrictedApi")
internal fun buildAndroidPathFromBezier(path: List<RemoteBezierValue>): Path {
  path.forEach { it.requireStaticModifierGeometry("CPU path conversion / merge paths") }
  val androidPath = Path()
  if (path.isEmpty()) return androidPath
  for (subpath in path) {
    val vertices = subpath.vertices
    val inTangents = subpath.inTangents
    val outTangents = subpath.outTangents

    if (vertices.isEmpty()) continue

    val startX = vertices[0].getOrElse(0) { 0f.rf }.constantValueOrNull ?: 0f
    val startY = vertices[0].getOrElse(1) { 0f.rf }.constantValueOrNull ?: 0f
    androidPath.moveTo(startX, startY)

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

      val p1x = p0x + outTangentX
      val p1y = p0y + outTangentY
      val p2x = p4x + inTangentX
      val p2y = p4y + inTangentY

      if (outTangentX == 0f && outTangentY == 0f && inTangentX == 0f && inTangentY == 0f) {
        androidPath.lineTo(p4x, p4y)
      } else {
        androidPath.cubicTo(p1x, p1y, p2x, p2y, p4x, p4y)
      }
    }

    if (subpath.closed) {
      androidPath.close()
    }
  }
  return androidPath
}

@SuppressLint("RestrictedApi")
internal fun androidPathToBezierValues(path: Path): List<RemoteBezierValue> {
  if (path.isEmpty) return emptyList()
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
    return extractBezierViaPathIterator(path)
  }

  return extractBezierViaPathMeasure(path)
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("RestrictedApi")
private fun extractBezierViaPathIterator(path: Path): List<RemoteBezierValue> {
  val subpaths = mutableListOf<RemoteBezierValue>()
  val iterator = path.pathIterator
  if (!iterator.hasNext()) return emptyList()

  val points = FloatArray(8)
  var currentVertices = mutableListOf<List<RemoteFloat>>()
  var currentInTangents = mutableListOf<List<RemoteFloat>>()
  var currentOutTangents = mutableListOf<List<RemoteFloat>>()
  var currentPoint = Point(0f, 0f)
  var startPoint = Point(0f, 0f)

  while (iterator.hasNext()) {
    val type = iterator.next(points, 0)
    when (type) {
      PathIterator.VERB_MOVE -> {
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
        val x = points[0]
        val y = points[1]
        currentPoint = Point(x, y)
        startPoint = Point(x, y)
        currentVertices.add(listOf(x.rf, y.rf))
        currentInTangents.add(listOf(0f.rf, 0f.rf))
        currentOutTangents.add(listOf(0f.rf, 0f.rf))
      }
      PathIterator.VERB_LINE -> {
        val x = points[2]
        val y = points[3]
        currentPoint = Point(x, y)
        currentVertices.add(listOf(x.rf, y.rf))
        currentInTangents.add(listOf(0f.rf, 0f.rf))
        currentOutTangents.add(listOf(0f.rf, 0f.rf))
      }
      PathIterator.VERB_QUAD -> {
        val x1 = points[2]
        val y1 = points[3]
        val x2 = points[4]
        val y2 = points[5]
        val outX = (2f / 3f) * (x1 - currentPoint.x)
        val outY = (2f / 3f) * (y1 - currentPoint.y)
        val inX = (2f / 3f) * (x1 - x2)
        val inY = (2f / 3f) * (y1 - y2)

        if (currentOutTangents.isNotEmpty()) {
          currentOutTangents[currentOutTangents.size - 1] = listOf(outX.rf, outY.rf)
        }
        currentPoint = Point(x2, y2)
        currentVertices.add(listOf(x2.rf, y2.rf))
        currentInTangents.add(listOf(inX.rf, inY.rf))
        currentOutTangents.add(listOf(0f.rf, 0f.rf))
      }
      PathIterator.VERB_CONIC -> {
        for (segment in conicToCubics(points)) {
          val cubic = segment.points
          currentOutTangents[currentOutTangents.lastIndex] =
            listOf((cubic[2] - cubic[0]).rf, (cubic[3] - cubic[1]).rf)
          currentVertices.add(listOf(cubic[6].rf, cubic[7].rf))
          currentInTangents.add(listOf((cubic[4] - cubic[6]).rf, (cubic[5] - cubic[7]).rf))
          currentOutTangents.add(listOf(0f.rf, 0f.rf))
        }
        currentPoint = Point(points[4], points[5])
      }
      PathIterator.VERB_CUBIC -> {
        val x1 = points[2]
        val y1 = points[3]
        val x2 = points[4]
        val y2 = points[5]
        val x3 = points[6]
        val y3 = points[7]

        val outX = x1 - currentPoint.x
        val outY = y1 - currentPoint.y
        val inX = x2 - x3
        val inY = y2 - y3

        if (currentOutTangents.isNotEmpty()) {
          currentOutTangents[currentOutTangents.size - 1] = listOf(outX.rf, outY.rf)
        }
        currentPoint = Point(x3, y3)
        currentVertices.add(listOf(x3.rf, y3.rf))
        currentInTangents.add(listOf(inX.rf, inY.rf))
        currentOutTangents.add(listOf(0f.rf, 0f.rf))
      }
      PathIterator.VERB_CLOSE -> {
        if (currentVertices.isNotEmpty()) {
          val lastV = currentVertices.last()
          val lastX = lastV[0].constantValueOrNull ?: 0f
          val lastY = lastV[1].constantValueOrNull ?: 0f
          val isSame = lastX == startPoint.x && lastY == startPoint.y
          if (isSame && currentVertices.size > 1) {
            val lastInTan = currentInTangents.last()
            currentVertices.removeAt(currentVertices.size - 1)
            currentInTangents.removeAt(currentInTangents.size - 1)
            currentOutTangents.removeAt(currentOutTangents.size - 1)
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
      PathIterator.VERB_DONE -> break
    }
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

@SuppressLint("RestrictedApi")
private fun extractBezierViaPathMeasure(path: Path): List<RemoteBezierValue> {
  val subpaths = mutableListOf<RemoteBezierValue>()
  val pm = PathMeasure(path, false)
  var hasContour = true
  while (hasContour) {
    val length = pm.length
    if (length > 0f) {
      val segmentPath = Path()
      pm.getSegment(0f, length, segmentPath, true)
      val approx = segmentPath.approximate(0.5f)
      if (approx.size >= 6) {
        val vertices = mutableListOf<List<RemoteFloat>>()
        val inTangents = mutableListOf<List<RemoteFloat>>()
        val outTangents = mutableListOf<List<RemoteFloat>>()
        val pointCount = approx.size / 3
        val firstX = approx[1]
        val firstY = approx[2]
        val lastX = approx[approx.size - 2]
        val lastY = approx[approx.size - 1]
        val duplicateClose = pm.isClosed && pointCount > 1 && firstX == lastX && firstY == lastY
        val limit = if (duplicateClose) pointCount - 1 else pointCount

        for (i in 0 until limit) {
          val x = approx[i * 3 + 1]
          val y = approx[i * 3 + 2]
          vertices.add(listOf(x.rf, y.rf))
          inTangents.add(listOf(0f.rf, 0f.rf))
          outTangents.add(listOf(0f.rf, 0f.rf))
        }
        subpaths.add(
          RemoteBezierValue(
            closed = pm.isClosed,
            vertices = vertices,
            inTangents = inTangents,
            outTangents = outTangents,
          )
        )
      }
    }
    hasContour = pm.nextContour()
  }

  if (subpaths.isNotEmpty()) {
    return subpaths
  }

  return fallbackApproximatePath(path)
}

@SuppressLint("RestrictedApi")
private fun fallbackApproximatePath(path: Path): List<RemoteBezierValue> {
  val approx = path.approximate(0.5f)
  if (approx.size < 6) return emptyList()
  val vertices = mutableListOf<List<RemoteFloat>>()
  val inTangents = mutableListOf<List<RemoteFloat>>()
  val outTangents = mutableListOf<List<RemoteFloat>>()
  val pointCount = approx.size / 3
  val firstX = approx[1]
  val firstY = approx[2]
  val lastX = approx[approx.size - 2]
  val lastY = approx[approx.size - 1]
  val isClosed = pointCount > 1 && abs(firstX - lastX) < 0.001f && abs(firstY - lastY) < 0.001f
  val limit = if (isClosed) pointCount - 1 else pointCount

  for (i in 0 until limit) {
    val x = approx[i * 3 + 1]
    val y = approx[i * 3 + 2]
    vertices.add(listOf(x.rf, y.rf))
    inTangents.add(listOf(0f.rf, 0f.rf))
    outTangents.add(listOf(0f.rf, 0f.rf))
  }
  return listOf(
    RemoteBezierValue(
      closed = isClosed,
      vertices = vertices,
      inTangents = inTangents,
      outTangents = outTangents,
    )
  )
}
