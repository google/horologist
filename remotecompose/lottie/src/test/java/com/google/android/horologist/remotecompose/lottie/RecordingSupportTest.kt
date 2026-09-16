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

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.remote.creation.Rc
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.LineJoin
import com.google.android.horologist.remotecompose.lottie.format.layer.BlendMode
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierTopology
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.offsetCurves
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.requireStaticModifierGeometry
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.requireSupportedOffsetTopology
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordingSupportTest {
  @Test
  fun retainsCommonMetadataForOriginalLayerTypes() {
    for (type in listOf(1, 3, 4)) {
      val layer = decode(type, "\"st\":7,\"sr\":2,\"bm\":1,\"td\":1,\"ddd\":1").layers.single()
      assertThat(layer.startTime).isEqualTo(7f)
      assertThat(layer.timeStretch).isEqualTo(2f)
      assertThat(layer.blendMode).isEqualTo(BlendMode.Multiply)
      assertThat(layer.matteTarget).isEqualTo(1)
      assertThat(layer.is3d).isEqualTo(1)
    }
  }

  @Test
  fun reportsUnsupportedFeaturesBeforeRecording() {
    for ((properties, message) in
      listOf(
        "\"ddd\":1" to "3D",
        "\"ao\":1" to "auto-orientation",
        "\"tt\":3" to "Luminance",
        "\"tt\":4" to "Luminance",
        "\"bm\":17" to "Hard-mix",
      )) {
      val error =
        assertThrows(IllegalArgumentException::class.java) {
          decode(4, properties).validateForRecording()
        }
      assertThat(error).hasMessageThat().contains(message)
    }
  }

  @Test
  fun refusesToCollapseLiveGeometryToTheOrigin() {
    val path =
      RemoteBezierValue(
        closed = true,
        vertices = listOf(listOf(RemoteFloat(Rc.Time.ANIMATION_TIME), 0f.rf)),
        inTangents = listOf(listOf(0f.rf, 0f.rf)),
        outTangents = listOf(listOf(0f.rf, 0f.rf)),
      )
    val error =
      assertThrows(IllegalArgumentException::class.java) {
        path.requireStaticModifierGeometry("merge paths")
      }
    assertThat(error).hasMessageThat().contains("animated geometry")
  }

  @Test
  fun refusesVariableTopologyAndLiveSelfIntersectionOffsetCurves() {
    val live = RemoteFloat(Rc.Time.ANIMATION_TIME)
    val dynamicTopologyPath =
      RemoteBezierValue(
        closed = true,
        vertices = listOf(listOf(0f.rf, 0f.rf), listOf(10f.rf, 0f.rf), listOf(10f.rf, 10f.rf)),
        inTangents = listOf(listOf(0f.rf, 0f.rf), listOf(0f.rf, 0f.rf), listOf(0f.rf, 0f.rf)),
        outTangents = listOf(listOf(0f.rf, 0f.rf), listOf(0f.rf, 0f.rf), listOf(0f.rf, 0f.rf)),
        topology =
          RemoteBezierTopology(
            vertices = listOf(1f.rf, 1f.rf, live),
            segments = listOf(1f.rf, 1f.rf, live),
          ),
      )
    val dynamicTopoError =
      assertThrows(IllegalArgumentException::class.java) {
        dynamicTopologyPath.requireSupportedOffsetTopology(
          hasCurves = false,
          amount = 2f.rf,
          miterLimit = 4f.rf,
        )
      }
    assertThat(dynamicTopoError)
      .hasMessageThat()
      .contains(
        "OffsetPath does not support dynamic variable-topology or self-intersection removal on live-animated curves"
      )

    val animatedCurveWithTopology =
      RemoteBezierValue(
        closed = true,
        vertices = listOf(listOf(0f.rf, 0f.rf), listOf(10f.rf, 0f.rf)),
        inTangents = listOf(listOf(2f.rf, 2f.rf), listOf(2f.rf, 2f.rf)),
        outTangents = listOf(listOf(2f.rf, 2f.rf), listOf(2f.rf, 2f.rf)),
        topology =
          RemoteBezierTopology(vertices = listOf(1f.rf, 1f.rf), segments = listOf(1f.rf, 1f.rf)),
      )
    val animatedCurveError =
      assertThrows(IllegalArgumentException::class.java) {
        offsetCurves(
          animatedCurveWithTopology,
          amount = live,
          join = LineJoin.Miter,
          miterLimit = 4f.rf,
          strictOffsetTopology = true,
        )
      }
    assertThat(animatedCurveError)
      .hasMessageThat()
      .contains(
        "OffsetPath does not support dynamic variable-topology or self-intersection removal on live-animated curves"
      )

    // By default (strictOffsetTopology = false), offsetCurves degrades gracefully to single-piece
    // hodograph offsetting without throwing.
    val degraded =
      offsetCurves(
        animatedCurveWithTopology,
        amount = live,
        join = LineJoin.Miter,
        miterLimit = 4f.rf,
        strictOffsetTopology = false,
      )
    assertThat(degraded.vertices).isNotEmpty()
  }

  private fun decode(type: Int, extra: String): Animation =
    Animation.decodeFromString(
      """{"w":64,"h":64,"fr":30,"ip":0,"op":30,"layers":[
      {"ty":$type,"ip":0,"op":30,"shapes":[],"sw":64,"sh":64,"sc":"#ff0000",$extra}]}"""
    )
}
