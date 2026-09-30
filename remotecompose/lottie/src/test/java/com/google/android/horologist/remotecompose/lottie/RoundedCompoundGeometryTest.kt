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

@file:Suppress("RestrictedApi")

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.RemoteState
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.remote.player.view.RemoteComposePlayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.RoundedCorners
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimPath
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BezierKeyframe
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.LogicalBezier
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluatePathGeometry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/** Equal-length contours give independently known whole-contour and symmetric arc cuts. */
@RunWith(AndroidJUnit4::class)
class RoundedCompoundGeometryTest : MotionPixelHarness() {
  @Test fun firstHalfKeepsOnlyFirstRoundedContour() = wholeContour(0f, 50f, 0f)

  @Test fun secondHalfKeepsOnlySecondRoundedContour() = wholeContour(50f, 100f, 32f)

  @Test
  fun middleHalfCutsBothRoundedArcsAtTheirSymmetryPoint() {
    val paths = evaluate(25f, 75f)
    assertThat(paths).hasSize(2)
    // Cubic rounding uses k=0.5519: de Casteljau at t=1/2 gives this point.
    point(paths[0].vertices.first(), 8.82785f, 1.17215f)
    point(paths[0].vertices.last(), 10f, 10f)
    point(paths[1].vertices.first(), 32f, 0f)
    point(paths[1].vertices.last(), 40.82785f, 1.17215f)
  }

  @Test fun changingCompoundCutStaysLiveAcrossBackwardSeeks() = verifyPlayback(false)

  @Test fun changingRadiusAndCompoundCutStayLiveAcrossBackwardSeeks() = verifyPlayback(true)

  @Test
  fun twoClosedContoursWithChangingRadiusAndTrim() {
    fun square(x: Int) =
      """{"c":true,"v":[[$x,12],[${x+20},12],[${x+20},32],[$x,32]],"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}"""
    val stroke =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""
    val shapes =
      """{"ty":"sh","ks":{"a":1,"k":[{"t":0,"s":[${square(8)},${square(36)}]}]}},
      {"ty":"rd","r":${animated("[0]","[8]")}},
      {"ty":"tm","s":${fixed("0")},"e":${animated("[25]","[75]")},"o":${fixed("0")},"m":1},$stroke"""
    assertMatchesStaticFrames(
      animation(shapes),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "rounding-compound-live-radius",
      frames = (0..10).toList() + listOf(5, 0, 10, 0),
      maxMeanRedError = .0002f,
      maxPeakRedError = .3f,
      staticAt = { frame ->
        // Explicit rounded geometry, each contour trimmed natively in its own group.
        // Equal perimeters map the shared percentage to these independent local domains.
        val radius = frame * .8f
        fun rounded(x: Float): String {
          val vertices =
            listOf(
              listOf(x, 12f + radius),
              listOf(x + radius, 12f),
              listOf(x + 20 - radius, 12f),
              listOf(x + 20, 12f + radius),
              listOf(x + 20, 32f - radius),
              listOf(x + 20 - radius, 32f),
              listOf(x + radius, 32f),
              listOf(x, 32f - radius),
            )
          val k = radius * .5519f
          val zero = listOf(0f, 0f)
          val incoming =
            listOf(
              zero,
              listOf(-k, 0f),
              zero,
              listOf(0f, -k),
              zero,
              listOf(k, 0f),
              zero,
              listOf(0f, k),
            )
          val outgoing =
            listOf(
              listOf(0f, -k),
              zero,
              listOf(k, 0f),
              zero,
              listOf(0f, k),
              zero,
              listOf(-k, 0f),
              zero,
            )
          return """{"c":true,"v":$vertices,"i":$incoming,"o":$outgoing}"""
        }
        animation(
          (0..1).joinToString(",") { i ->
            val end = (50f + frame * 10f - i * 100f).coerceIn(0f, 100f)
            """{"ty":"gr","it":[{"ty":"sh","ks":${fixed(rounded(8f+i*28f))}},
            {"ty":"tm","s":${fixed("0")},"e":${fixed(end.toString())},"o":${fixed("0")},"m":1},$stroke]}"""
          }
        )
      },
    )
  }

  private fun verifyPlayback(animatedRadius: Boolean) {
    var player: RemoteComposePlayer? = null
    var recordings = 0
    composeRule.setContent {
      val document =
        rememberRemoteDocument(profile = LottieProfiles.NoRuntimeShaders) {
          val end = rememberNamedRemoteFloat("cut-end") { 0f.rf }
          val radius = if (animatedRadius) rememberNamedRemoteFloat("radius") { 0f.rf } else 4f.rf
          val result =
            evaluatePathGeometry(
              source(),
              TrimPath(end = StaticScalarProperty(value = end)),
              RoundedCorners(radius = StaticScalarProperty(value = radius)),
              LottieSettings(0f.rf),
            )
          fun named(name: String, value: RemoteFloat) =
            RemoteFloat.createNamedRemoteFloatExpression(name, RemoteState.Domain.User) { value }
          val output =
            result.withPath(
              result.path.mapIndexed { i, path ->
                path.copy(
                  visibility = named("visible-$i", path.visibility),
                  vertices =
                    path.vertices.mapIndexed { j, point ->
                      if (j == path.vertices.lastIndex)
                        listOf(named("end-x-$i", point[0]), named("end-y-$i", point[1]))
                      else point
                    },
                )
              }
            )
          val paint = RemotePaint { color = Color.Red.rc }
          RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
            recordings++
            usePaint(paint) { output.draw(this, remoteCanvas) }
          }
        }
      Box(Modifier.size(64.dp).testTag("motion")) {
        document.value?.let {
          RemoteDocumentPlayer(
            it,
            modifier = Modifier.size(64.dp),
            documentWidth = 64,
            documentHeight = 64,
            init = { view ->
              player = view
              view.setShaderControl { false }
            },
          )
        }
      }
    }
    capture("motion").recycle()
    fun read(name: String) =
      checkNotNull(player).getNamedFloat(RemoteState.Domain.User.prefixed(name))
    val radii = if (animatedRadius) listOf(0f, 1f, 4f, 2f, 0f, 4f) else listOf(4f)
    for (radius in radii) for (end in listOf(0f, 25f, 50f, 75f, 100f, 50f, 0f, 75f, 25f)) {
      composeRule.runOnIdle {
        checkNotNull(player).setUserLocalFloat("cut-end", end)
        if (animatedRadius) checkNotNull(player).setUserLocalFloat("radius", radius)
      }
      capture("motion").recycle()
      for (i in 0..1) {
        val extent = (end - 50f * i).coerceIn(0f, 50f)
        assertThat(read("visible-$i")).isEqualTo(if (extent == 0f) 0f else 1f)
        if (extent == 0f) continue
        val y = if (extent == 25f) radius * .2930375f else 10f
        val x = if (extent == 25f) 10f - y else 10f
        assertThat(read("end-x-$i")).isWithin(.0001f).of(x + i * 32f)
        assertThat(read("end-y-$i")).isWithin(.0001f).of(y)
      }
    }
    assertThat(recordings).isEqualTo(1)
  }

  private fun wholeContour(start: Float, end: Float, x: Float) {
    val path = evaluate(start, end).single()
    assertThat(path.vertices).hasSize(4)
    for ((i, expected) in listOf(0f to 0f, 6f to 0f, 10f to 4f, 10f to 10f).withIndex()) {
      point(path.vertices[i], x + expected.first, expected.second)
    }
    point(path.outTangents[1], 2.2076f, 0f)
    point(path.inTangents[2], 0f, -2.2076f)
  }

  private fun source(): AnimatedBezierProperty {
    fun contour(x: Float) =
      BezierValue(
        closed = false,
        vertices = listOf(listOf(x, 0f), listOf(x + 10f, 0f), listOf(x + 10f, 10f)),
        inTangents = List(3) { listOf(0f, 0f) },
        outTangents = List(3) { listOf(0f, 0f) },
      )
    return AnimatedBezierProperty(
      keyframes = listOf(BezierKeyframe(0f, listOf(contour(0f), contour(32f))))
    )
  }

  private fun evaluate(start: Float, end: Float): List<RemoteBezierValue> {
    val result =
      evaluatePathGeometry(
        source(),
        TrimPath(
          start = StaticScalarProperty(value = start.rf),
          end = StaticScalarProperty(value = end.rf),
        ),
        RoundedCorners(radius = StaticScalarProperty(value = 4f.rf)),
        LottieSettings(0f.rf),
      )
    assertThat(result.trim).isNull()
    return result.path
      .filter { it.visibility.constantValue > 0f }
      .map { path ->
        val logical = LogicalBezier(path)
        val indices = logical.active.indices.filter { logical.active[it].constantValue > 0f }
        path.copy(
          vertices = indices.map { logical.vertices[it].values() },
          inTangents = indices.map { logical.incoming[it].values() },
          outTangents = indices.map { logical.outgoing[it].values() },
          topology = null,
        )
      }
  }

  private fun point(actual: List<RemoteFloat>, x: Float, y: Float) {
    assertThat(actual[0].constantValue).isWithin(.0001f).of(x)
    assertThat(actual[1].constantValue).isWithin(.0001f).of(y)
  }
}
