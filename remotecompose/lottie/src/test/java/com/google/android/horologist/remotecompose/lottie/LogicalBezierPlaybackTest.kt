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
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.RoundedCorners
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierTopology
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.LogicalBezier
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.roundRemoteBezier
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test

class LogicalBezierPlaybackTest : MotionPixelHarness() {
  @Test
  fun closedEdgeOwnersPreserveHandlesThroughEveryPaddingPattern() {
    val vertices = listOf(listOf(12f, 12f), listOf(52f, 12f), listOf(52f, 52f), listOf(12f, 52f))
    val incoming = List(4) { listOf((it + 1).toFloat(), -2f) }
    val outgoing = List(4) { listOf(-3f, (it + 1).toFloat()) }
    for (mask in 0 until 16) {
      val active = List(4) { if (mask and (1 shl it) != 0) 1f else 0f }
      val flags = active.map { it.rf }
      val source =
        RemoteBezierValue(
          true,
          incoming.map { p -> p.map { it.rf } },
          outgoing.map { p -> p.map { it.rf } },
          vertices.map { p -> p.map { it.rf } },
          RemoteBezierTopology(flags, flags),
        )
      val actual = LogicalBezier(source).canonical()
      for (i in 0 until 4) {
        val next = (0 until 4).map { (i + it) % 4 }.firstOrNull { active[it] == 1f }
        assertThat(actual.vertices[i].map { it.constantValue }).isEqualTo(vertices[next ?: i])
        assertThat(actual.inTangents[i].map { it.constantValue })
          .isEqualTo(if (active[(i + 3) % 4] == 1f) incoming[i] else listOf(0f, 0f))
        assertThat(actual.outTangents[i].map { it.constantValue })
          .isEqualTo(if (active[i] == 1f) outgoing[i] else listOf(0f, 0f))
      }
      assertThat(actual.topology!!.segments.map { it.constantValue }).isEqualTo(active)
    }
  }

  @Test
  fun liveDoubleRoundCoordinatesMatchConstantEvaluation() {
    val source =
      RemoteBezierValue(
        true,
        List(4) { listOf(0f.rf, 0f.rf) },
        List(4) { listOf(0f.rf, 0f.rf) },
        listOf(
          listOf(12f.rf, 12f.rf),
          listOf(52f.rf, 12f.rf),
          listOf(52f.rf, 20f.rf),
          listOf(12f.rf, 52f.rf),
        ),
      )
    var player: RemoteComposePlayer? = null
    val liveCoordinates = mutableSetOf<String>()
    val rounding =
      ((Animation.decodeFromString(animation("""{"ty":"rd","r":${animated("[0]","[12]")}}"""))
          .layers
          .single() as ShapeLayer)
        .shapes
        .single() as RoundedCorners)
    composeRule.setContent {
      val document =
        rememberRemoteDocument(profile = LottieProfiles.NoRuntimeShaders) {
          val progress = rememberNamedRemoteFloat("progress") { 0f.rf }
          val radius = animateScalar(rounding.radius, LottieSettings(progress * 40f))
          val output = roundRemoteBezier(roundRemoteBezier(source, radius), 4f.rf)
          fun named(kind: String, points: List<List<RemoteFloat>>) = points.mapIndexed { i, p ->
            p.mapIndexed { j, value ->
              if (value.constantValueOrNull == null) liveCoordinates.add("$kind-$i-$j")
              RemoteFloat.createNamedRemoteFloatExpression("$kind-$i-$j", RemoteState.Domain.User) {
                value
              }
            }
          }
          val path =
            RemoteLottiePath(
              listOf(
                output.copy(
                  vertices = named("v", output.vertices),
                  inTangents = named("i", output.inTangents),
                  outTangents = named("o", output.outTangents),
                )
              )
            )
          val red = RemotePaint { color = Color.Red.rc }
          RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
            usePaint(red) { path.draw(this, remoteCanvas) }
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
    assertWithMessage("recorded coordinates must be live").that(liveCoordinates).isNotEmpty()
    val errors = mutableListOf<String>()
    for (frame in 1..10) {
      val radius = frame * 1.2f
      composeRule.runOnIdle { checkNotNull(player).setUserLocalFloat("progress", frame / 40f) }
      capture("motion").recycle()
      val expected = roundRemoteBezier(roundRemoteBezier(source, radius.rf), 4f.rf)
      for ((kind, points) in
        listOf("v" to expected.vertices, "i" to expected.inTangents, "o" to expected.outTangents)) {
        for ((i, p) in points.withIndex()) for ((j, value) in p.withIndex()) {
          // Constant aliases need not each receive a named runtime value. Pixel tests cover
          // the complete path; this check specifically verifies its live dependencies.
          if ("$kind-$i-$j" !in liveCoordinates) continue
          val actual =
            checkNotNull(player).getNamedFloat(RemoteState.Domain.User.prefixed("$kind-$i-$j"))
          if (kotlin.math.abs(actual - value.constantValue) > 0.0002f)
            errors.add("$frame/$kind/$i/$j: expected=${value.constantValue}, actual=$actual")
        }
      }
    }
    assertWithMessage(errors.joinToString("\n")).that(errors).isEmpty()
  }
}
