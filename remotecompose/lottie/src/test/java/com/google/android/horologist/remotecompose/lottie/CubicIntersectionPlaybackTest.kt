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

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteState
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.remote.player.view.RemoteComposePlayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.cubicIntersection
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import kotlin.math.abs
import org.junit.Test

class CubicIntersectionPlaybackTest : MotionPixelHarness() {
  @Test
  fun analyticIntersectionsSurviveMultipleCallsAndBackwardSeeks() {
    // x=t; y=(t-.2)(t-.5)(t-.8). These roots are analytic, not another solver's output.
    val triple = listOf(0f, -.08f, 1f / 3f, .14f, 2f / 3f, -.14f, 1f, .08f)
    val horizontal = listOf(0f, 0f, 1f / 3f, 0f, 2f / 3f, 0f, 1f, 0f)
    val separated = horizontal.mapIndexed { index, value -> if (index % 2 == 1) 1f else value }
    val vertical = listOf(1f, 0f, 1f, 1f / 3f, 1f, 2f / 3f, 1f, 1f)
    // Pinned web offset controls at the final concave join of the 5.4-point rounded star.
    // Its unsorted intersection list selects a near-endpoint candidate before this crossing.
    val starA =
      listOf(
        31.662961f,
        17.531815f,
        32.763981f,
        19.289709f,
        33.249137f,
        20.064311f,
        33.393115f,
        20.081140f,
      )
    val starB =
      listOf(
        33.393115f,
        20.081140f,
        33.216743f,
        20.060525f,
        33.070135f,
        20.560674f,
        33.359038f,
        19.575087f,
      )
    // Independent piecewise-linear intersection, with a convergence check, not Newton or web's
    // recursive bounding-box candidates. These samples are test geometry, never production frames.
    val starRoot = polylineIntersection(starA, starB, 1024)
    val coarseRoot = polylineIntersection(starA, starB, 512)
    assertThat(starRoot.first).isWithin(.00001f).of(coarseRoot.first)
    assertThat(starRoot.second).isWithin(.00001f).of(coarseRoot.second)
    assertThat(starRoot.first).isLessThan(.8f)
    assertThat(starRoot.second).isGreaterThan(.8f)
    // Two nearby t roots but widely separated u roots; an interior-only seed grid misses the first.
    val narrowA =
      listOf(
        52.405149f,
        47.506182f,
        53.556779f,
        51.021535f,
        92.725525f,
        170.584143f,
        44.286093f,
        49.485563f,
      )
    val narrowB =
      listOf(
        46.211146f,
        44.422291f,
        11.751380f,
        61.652174f,
        51.216828f,
        50.098491f,
        44.900022f,
        51.947764f,
      )
    val narrowRoot = polylineIntersection(narrowA, narrowB, 4096)
    val coarseNarrow = polylineIntersection(narrowA, narrowB, 2048)
    assertThat(narrowRoot.first).isWithin(.0001f).of(coarseNarrow.first)
    assertThat(narrowRoot.second).isWithin(.0001f).of(coarseNarrow.second)
    assertThat(narrowRoot.second).isGreaterThan(.97f)
    val cases =
      listOf(
        Triple(triple, horizontal, .2f to .2f),
        Triple(horizontal, separated, 1f to 0f),
        Triple(horizontal, vertical, 1f to 0f), // Excluded shared end/start point.
        Triple(starA, starB, starRoot),
        Triple(narrowA, narrowB, narrowRoot),
      )
    for ((a, b, expected) in cases) {
      val actual = cubicIntersection(a.map { it.rf }, b.map { it.rf })
      assertThat(actual.first.constantValue).isWithin(.0001f).of(expected.first)
      assertThat(actual.second.constantValue).isWithin(.0001f).of(expected.second)
      val disabled = cubicIntersection(a.map { it.rf }, b.map { it.rf }, 0f.rf)
      assertThat(disabled.first.constantValue).isEqualTo(1f)
      assertThat(disabled.second.constantValue).isEqualTo(0f)
    }
    var player: RemoteComposePlayer? = null
    var recordings = 0
    fun name(index: Int, axis: Int) = RemoteState.Domain.User.prefixed("intersection-$index-$axis")
    composeRule.setContent {
      val document =
        rememberRemoteDocument(profile = LottieProfiles.NoRuntimeShaders) {
          val shift = rememberNamedRemoteFloat("shift") { 0f.rf }
          val enabled = rememberNamedRemoteFloat("enabled") { 1f.rf }
          RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
            val canvas = remoteCanvas.internalCanvas
            val operation = canvas.recordRenderingOp {
              recordings++
              val writer = canvas.creationState.document
              for ((index, case) in cases.withIndex()) {
                fun translated(points: List<Float>) = points.mapIndexed { coordinate, value ->
                  if (coordinate % 2 == 1) value.rf + shift else value.rf
                }
                val (t, u) =
                  cubicIntersection(translated(case.first), translated(case.second), enabled)
                for ((axis, value) in listOf(t, u).withIndex()) {
                  writer.setFloatName(
                    value.getIdForCreationState(canvas.creationState),
                    name(index, axis),
                  )
                }
              }
              writer.drawRect(0f, 0f, 8f, 8f)
            }
            canvas.buffer.addRoots(operation, shift, enabled)
          }
        }
      Box(Modifier.size(64.dp).testTag("motion")) {
        document.value?.let {
          RemoteDocumentPlayer(
            it,
            modifier = Modifier.size(64.dp),
            documentWidth = 64,
            documentHeight = 64,
            init = { player = it },
          )
        }
      }
    }
    capture("motion").recycle()
    for ((shift, enabled) in
      listOf(0f to 1f, 3f to 1f, -2f to 0f, 3f to 1f, 0f to 0f, 0f to 1f, 0f to 1f)) {
      composeRule.runOnIdle {
        checkNotNull(player).setUserLocalFloat("shift", shift)
        checkNotNull(player).setUserLocalFloat("enabled", enabled)
      }
      capture("motion").recycle()
      for ((index, case) in cases.withIndex()) {
        for ((axis, expected) in listOf(case.third.first, case.third.second).withIndex()) {
          assertWithMessage("case=$index, axis=$axis, shift=$shift")
            .that(checkNotNull(player).getNamedFloat(name(index, axis)))
            .isWithin(.0001f)
            .of(if (enabled == 1f) expected else if (axis == 0) 1f else 0f)
        }
      }
    }
    assertThat(recordings).isEqualTo(1)
  }

  private fun polylineIntersection(a: List<Float>, b: List<Float>, count: Int): Pair<Float, Float> {
    fun points(curve: List<Float>) =
      Array(count + 1) { index ->
        val t = index.toDouble() / count
        val s = 1.0 - t
        DoubleArray(2) { axis ->
          curve[axis] * s * s * s +
            3.0 * curve[axis + 2] * t * s * s +
            3.0 * curve[axis + 4] * t * t * s +
            curve[axis + 6] * t * t * t
        }
      }
    val pa = points(a)
    val pb = points(b)
    var firstT = 1.0
    var firstU = 0.0
    for (i in 0 until count) for (j in 0 until count) {
      val ax = pa[i + 1][0] - pa[i][0]
      val ay = pa[i + 1][1] - pa[i][1]
      val bx = pb[j + 1][0] - pb[j][0]
      val by = pb[j + 1][1] - pb[j][1]
      val dx = pb[j][0] - pa[i][0]
      val dy = pb[j][1] - pa[i][1]
      val det = ax * by - ay * bx
      if (abs(det) < 1e-16) continue
      val localT = (dx * by - dy * bx) / det
      val localU = (dx * ay - dy * ax) / det
      if (localT !in 0.0..1.0 || localU !in 0.0..1.0) continue
      val t = (i + localT) / count
      val u = (j + localU) / count
      if (t < .99999 && u > .00001 && t < firstT) {
        firstT = t
        firstU = u
      }
    }
    check(firstT < 1.0) { "Independent polylines must intersect" }
    return firstT.toFloat() to firstU.toFloat()
  }
}
