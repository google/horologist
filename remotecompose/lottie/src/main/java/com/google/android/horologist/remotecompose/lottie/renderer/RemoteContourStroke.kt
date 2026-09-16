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

@file:Suppress("RestrictedApi", "INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.google.android.horologist.remotecompose.lottie.renderer

import androidx.compose.remote.core.operations.ConditionalOperations
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteDrawScope
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf

/**
 * Assemble only present contours, then stroke once. Separate draw calls would apply translucent
 * paint more than once at overlaps; collapsed empty contours can instead leave round caps.
 * PathAppend resets winding, so this representation is deliberately used only for strokes.
 */
internal class RemoteContourStroke(private val source: RemoteLottiePath) : RemoteShape {
  override fun draw(
    drawScope: RemoteDrawScope,
    canvas: RemoteCanvas,
    inheritedOpacity: RemoteFloat,
  ) {
    check(source.trim == null) {
      "Trimming a conditional multi-contour stroke is not yet supported"
    }
    val inputs = mutableListOf<RemoteFloat>()
    fun input(value: RemoteFloat): RemoteFloat = value.also { inputs.add(it) }
    data class Edge(val visible: RemoteFloat, val coordinates: List<RemoteFloat>)
    data class Contour(
      val visible: RemoteFloat,
      val start: List<RemoteFloat>,
      val edges: List<Edge>,
      val closed: Boolean,
    )
    val contours =
      source.path
        .filter { it.vertices.isNotEmpty() }
        .map { path ->
          val count = path.vertices.size
          Contour(
            input(path.visibility),
            path.vertices[0].map(::input),
            (0 until if (path.closed) count else count - 1).map { i ->
              val next = (i + 1) % count
              Edge(
                input(path.topology?.segments?.get(i) ?: 1f.rf),
                (0..1).map { input(path.vertices[i][it] + path.outTangents[i][it]) } +
                  (0..1).map { input(path.vertices[next][it] + path.inTangents[next][it]) } +
                  path.vertices[next].map(::input),
              )
            },
            path.closed,
          )
        }
    val op =
      canvas.internalCanvas.recordRenderingOp {
        val id = canvas.document.pathCreate(0f, 0f)
        // Resolve every expression before entering a conditional recording block.
        val encoded = inputs.associateWith {
          it.getFloatIdForCreationState(canvas.internalCanvas.creationState)
        }
        fun value(input: RemoteFloat): Float = encoded.getValue(input)
        canvas.document.pathAppendReset(id)
        for (contour in contours) {
          canvas.document.conditionalOperations(
            ConditionalOperations.TYPE_GT,
            value(contour.visible),
            0f,
          )
          canvas.document.pathAppendMoveTo(id, value(contour.start[0]), value(contour.start[1]))
          for (edge in contour.edges) {
            canvas.document.conditionalOperations(
              ConditionalOperations.TYPE_GT,
              value(edge.visible),
              0f,
            )
            val c = edge.coordinates.map(::value)
            canvas.document.pathAppendCubicTo(id, c[0], c[1], c[2], c[3], c[4], c[5])
            canvas.document.endConditionalOperations()
          }
          if (contour.closed) canvas.document.pathAppendClose(id)
          canvas.document.endConditionalOperations()
        }
        canvas.document.drawPath(id)
      }
    canvas.internalCanvas.buffer.addRoots(op, *inputs.toTypedArray())
  }
}
