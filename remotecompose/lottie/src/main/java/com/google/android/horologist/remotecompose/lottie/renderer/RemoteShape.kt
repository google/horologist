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
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.abs
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.remotePath
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfGe
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.FillRule
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.DeferredPathTransform
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.materializeTrim

@SuppressLint("RestrictedApi")
internal interface RemoteShape {
  fun draw(drawScope: RemoteDrawScope, canvas: RemoteCanvas, inheritedOpacity: RemoteFloat = 1f.rf)

  fun withFillRule(fillRule: FillRule): RemoteShape = this
}

@SuppressLint("RestrictedApi")
@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
internal fun RemoteCanvas.drawPathWithFillRule(
  path: RemotePath,
  fillRule: FillRule,
  paint: androidx.compose.remote.creation.compose.state.RemotePaint? = null,
) {
  val winding = if (fillRule == FillRule.EvenOdd) 1 else 0
  val op =
    internalCanvas.recordRenderingOp(paint) {
      val pathId = document.addPathData(path, winding)
      document.drawPath(pathId)
    }
  internalCanvas.buffer.addRoots(op, path)
}

@SuppressLint("RestrictedApi")
internal class RemoteCompiledPath(val path: RemotePath, val fillRule: FillRule = FillRule.NonZero) :
  RemoteShape {
  override fun draw(
    drawScope: RemoteDrawScope,
    canvas: RemoteCanvas,
    inheritedOpacity: RemoteFloat,
  ) {
    canvas.drawPathWithFillRule(path, fillRule)
  }

  override fun withFillRule(fillRule: FillRule): RemoteCompiledPath =
    if (this.fillRule == fillRule) this else RemoteCompiledPath(path, fillRule)
}

/** Authored identity, not coordinate equality: coincident paths are still separate operands. */
internal class GeometryIdentity(
  private val source: Any,
  private val parent: GeometryIdentity? = null,
  private val copyIndex: Int = 0,
) {
  override fun equals(other: Any?): Boolean =
    other is GeometryIdentity &&
      source === other.source &&
      parent == other.parent &&
      copyIndex == other.copyIndex

  override fun hashCode(): Int =
    31 * (31 * System.identityHashCode(source) + (parent?.hashCode() ?: 0)) + copyIndex
}

@SuppressLint("RestrictedApi")
internal class RemoteLottiePath(
  val path: List<RemoteBezierValue>,
  val fillRule: FillRule = FillRule.NonZero,
  val trim: RemotePathTrim? = null,
  val geometryTransforms: List<DeferredPathTransform> = emptyList(),
  val geometryVisibility: RemoteFloat = 1f.rf,
  val identity: GeometryIdentity = GeometryIdentity(Any()),
) : RemoteShape {
  /** Modifiers operate in path-local space; paints and explicit merges consume final geometry. */
  fun resolveGeometry(): RemoteLottiePath {
    if (
      geometryTransforms.isEmpty() &&
        geometryVisibility.constantValueOrNull == 1f &&
        path.all { it.visibility.constantValueOrNull == 1f }
    )
      return this
    // Materialize supported cuts before a nonuniform boundary changes their arc lengths.
    if (geometryTransforms.isNotEmpty()) {
      val source = materializeTrim()
      if (source !== this) return source.resolveGeometry()
    }
    val resolved = path.map { source ->
      val value = geometryTransforms.fold(source) { value, transform -> transform.apply(value) }
      val visible = geometryVisibility * value.visibility
      if (visible.constantValueOrNull == 1f) value
      else
        value.copy(
          vertices = value.vertices.map { point -> point.map { it * visible } },
          inTangents = value.inTangents.map { point -> point.map { it * visible } },
          outTangents = value.outTangents.map { point -> point.map { it * visible } },
        )
    }
    return RemoteLottiePath(resolved, fillRule, trim, identity = identity)
  }

  fun withPath(path: List<RemoteBezierValue>): RemoteLottiePath =
    RemoteLottiePath(path, fillRule, trim, geometryTransforms, geometryVisibility, identity)

  fun withIdentity(identity: GeometryIdentity): RemoteLottiePath =
    RemoteLottiePath(path, fillRule, trim, geometryTransforms, geometryVisibility, identity)

  override fun draw(
    drawScope: RemoteDrawScope,
    canvas: RemoteCanvas,
    inheritedOpacity: RemoteFloat,
  ) {
    if (path.isEmpty()) return

    val rcPath = asRemotePath(drawScope)
    if (trim == null) canvas.drawPathWithFillRule(rcPath, fillRule) else trim.draw(canvas, rcPath)
  }

  internal fun asRemotePath(drawScope: RemoteDrawScope): RemotePath = drawScope.remotePath {
    for (subpath in resolveGeometry().path) {
      val vertices = subpath.vertices
      val inTangents = subpath.inTangents
      val outTangents = subpath.outTangents
      // Keep mixed curved paths on one consistent encoding. For an entirely linear
      // playback-trimmed contour, avoid approximate cubic arc-length inversion.
      val linearTrim =
        trim != null && (inTangents + outTangents).flatten().all { it.constantValueOrNull == 0f }

      if (vertices.isEmpty()) continue

      val startX = vertices[0].getOrElse(0) { 0f.rf }
      val startY = vertices[0].getOrElse(1) { 0f.rf }
      moveTo(startX, startY)

      val maxIndex = if (subpath.closed) vertices.size else vertices.size - 1
      for (i in 0 until maxIndex) {
        val p0 = vertices[i]
        val lastIndex = if (i == vertices.size - 1 && subpath.closed) 0 else i + 1
        val p4 = vertices[lastIndex]
        val inTangent = inTangents.getOrNull(lastIndex)
        val outTangent = outTangents.getOrNull(i)

        val p0x = p0.getOrElse(0) { 0f.rf }
        val p0y = p0.getOrElse(1) { 0f.rf }
        val p4x = p4.getOrElse(0) { 0f.rf }
        val p4y = p4.getOrElse(1) { 0f.rf }

        val inTangentX = inTangent?.getOrElse(0) { 0f.rf } ?: 0f.rf
        val inTangentY = inTangent?.getOrElse(1) { 0f.rf } ?: 0f.rf
        val outTangentX = outTangent?.getOrElse(0) { 0f.rf } ?: 0f.rf
        val outTangentY = outTangent?.getOrElse(1) { 0f.rf } ?: 0f.rf

        val p1x = p0x + outTangentX
        val p1y = p0y + outTangentY
        val p2x = p4x + inTangentX
        val p2y = p4y + inTangentY

        if (linearTrim) {
          lineTo(p4x, p4y)
        } else {
          curveTo(p1x, p1y, p2x, p2y, p4x, p4y)
        }
      }

      if (subpath.closed) {
        close()
      }
    }
  }

  override fun withFillRule(fillRule: FillRule): RemoteLottiePath =
    if (this.fillRule == fillRule) this
    else RemoteLottiePath(path, fillRule, trim, geometryTransforms, geometryVisibility, identity)
}

/** Trim intervals over one arc-length domain; start/end are fractions and offset is turns. */
@SuppressLint("RestrictedApi")
internal class RemotePathTrim(
  val start: RemoteFloat,
  val end: RemoteFloat,
  val offset: RemoteFloat,
  val previous: RemotePathTrim? = null,
) {
  internal val ranges: List<Pair<RemoteFloat, RemoteFloat>> = buildRanges()

  private fun buildRanges(): List<Pair<RemoteFloat, RemoteFloat>> {
    val source = previous?.ranges ?: listOf(0f.rf to 1f.rf)
    val boundedStart = clamp(start, 0f.rf, 1f.rf)
    val boundedEnd = clamp(end, 0f.rf, 1f.rf)
    val span = abs(boundedEnd - boundedStart)
    val lower =
      if (boundedStart.constantValueOrNull == 0f || boundedEnd.constantValueOrNull == 0f) 0f.rf
      else min(boundedStart, boundedEnd)
    val shiftedStart = lower + offset
    val wholeTurnsOnly = offset.constantValueOrNull?.let { it % 1f == 0f } == true
    val normalizedStart = shiftedStart - floor(shiftedStart)
    val from =
      if (wholeTurnsOnly) lower
      else if (normalizedStart.constantValueOrNull == 0f) 0f.rf
      else selectIfGe(span, 1f.rf, 0f.rf, normalizedStart)
    val to = from + span
    val windows = mutableListOf(from to min(to, 1f.rf))
    val wrappedEnd =
      if (wholeTurnsOnly || from.constantValueOrNull == 0f) 0f.rf else max(to - 1f.rf, 0f.rf)
    if (wrappedEnd.constantValueOrNull != 0f) windows.add(0f.rf to wrappedEnd)
    var total = 0f.rf
    for ((a, b) in source) total += b - a
    val result = mutableListOf<Pair<RemoteFloat, RemoteFloat>>()
    for ((windowStart, windowEnd) in windows) {
      var prefix = 0f.rf
      for ((a, b) in source) {
        val length = b - a
        val clippedStart = clamp(windowStart * total - prefix, 0f.rf, length)
        val clippedEnd = clamp(windowEnd * total - prefix, 0f.rf, length)
        if ((clippedEnd - clippedStart).constantValueOrNull != 0f) {
          result.add(a + clippedStart to a + clippedEnd)
        }
        prefix += length
      }
    }
    require(result.size <= 256) { "Compound trims require more than 256 playback intervals" }
    return result
  }

  fun draw(canvas: RemoteCanvas, path: RemotePath) {
    // Each new trim measures the preceding trim's visible extent, not the untrimmed contour.
    // Fractions remain live; the player measures the source geometry at playback time.
    for ((from, to) in ranges) canvas.drawTweenPath(path, path, 0f.rf, from, to)
  }
}

@SuppressLint("RestrictedApi")
internal class RemoteGroup(
  val childShapes: List<StyledShapes>,
  val animationSettings: LottieSettings,
  val transform: Transform?,
  val opacityMultiplier: RemoteFloat = 1f.rf,
) : RemoteShape {
  override fun draw(
    drawScope: RemoteDrawScope,
    canvas: RemoteCanvas,
    inheritedOpacity: RemoteFloat,
  ) {
    val groupOpacity =
      if (transform != null) {
        val o = animateScalar(transform.opacity, animationSettings)
        inheritedOpacity * opacityMultiplier * (o / 100f)
      } else {
        inheritedOpacity * opacityMultiplier
      }

    for (shapeGroup in childShapes) {
      canvas.save()

      if (transform != null) {
        transform(transform, null, animationSettings, canvas)
      }

      if (shapeGroup.style is NoopStyle) {
        for (shape in shapeGroup.shapes) {
          shape.draw(drawScope, canvas, groupOpacity)
        }
      } else {
        val paint = shapeGroup.style.getPaint(groupOpacity)
        drawScope.usePaint(paint) {
          canvas.applyStrokeDetails(shapeGroup.style)
          for (shape in shapeGroup.shapes) {
            shape.draw(drawScope, canvas, groupOpacity)
          }
        }
        canvas.applyStrokeDetails(null)
      }

      canvas.restore()
    }
  }

  override fun withFillRule(fillRule: FillRule): RemoteGroup {
    val newChildShapes = childShapes.map { styledShapes ->
      StyledShapes(
        shapes = styledShapes.shapes.map { it.withFillRule(fillRule) },
        style = styledShapes.style,
      )
    }
    return RemoteGroup(newChildShapes, animationSettings, transform, opacityMultiplier)
  }

  /** Scales the opacity inherited by the group's own paints, without changing its geometry. */
  fun withOpacity(multiplier: RemoteFloat): RemoteGroup =
    RemoteGroup(childShapes, animationSettings, transform, opacityMultiplier * multiplier)
}
