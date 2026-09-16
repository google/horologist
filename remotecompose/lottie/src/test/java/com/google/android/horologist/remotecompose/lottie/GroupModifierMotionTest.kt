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
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.gatherShapesForTest
import com.google.common.truth.Truth.assertWithMessage
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test

class GroupModifierMotionTest : MotionPixelHarness() {
  private data class Point(val x: Double, val y: Double) {
    operator fun plus(p: Point) = Point(x + p.x, y + p.y)

    operator fun minus(p: Point) = Point(x - p.x, y - p.y)

    operator fun times(f: Double) = Point(x * f, y * f)
  }

  private data class Geometry(val v: List<Point>, val i: List<Point>, val o: List<Point>) {
    fun transform(map: (Point) -> Point): Geometry {
      val vertices = v.map(map)
      fun controls(points: List<Point>) = points.mapIndexed { j, p -> map(v[j] + p) - vertices[j] }
      return Geometry(vertices, controls(i), controls(o))
    }

    fun json(): String {
      fun points(p: List<Point>) = p.joinToString(",") { "[${it.x},${it.y}]" }
      return """{"c":true,"v":[${points(v)}],"i":[${points(i)}],"o":[${points(o)}]}"""
    }
  }

  private fun square(radius: Double = 20.0): Geometry {
    val vertices =
      listOf(-1 to -1, 1 to -1, 1 to 1, -1 to 1).map { (x, y) ->
        Point(32 + x * radius, 32 + y * radius)
      }
    return Geometry(vertices, List(4) { Point(0.0, 0.0) }, List(4) { Point(0.0, 0.0) })
  }

  private fun affine(
    p: Point,
    sx: Double = 0.6,
    sy: Double = 0.9,
    degrees: Double = -15.0,
    dx: Double = 0.0,
    dy: Double = 0.0,
  ): Point {
    val q = p - Point(32.0, 32.0)
    val angle = degrees * PI / 180
    return Point(
      32 + dx + q.x * sx * cos(angle) - q.y * sy * sin(angle),
      32 + dy + q.x * sx * sin(angle) + q.y * sy * cos(angle),
    )
  }

  private val stroke
    get() =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""

  private val transform
    get() =
      """{"ty":"tr","p":${fixed("[32,32]")},"a":${fixed("[32,32]")},"s":${fixed("[60,90]")},"r":${fixed("-15")},"o":${fixed("100")}}"""

  private fun path(json: String) = """{"ty":"sh","ks":${fixed(json)}}"""

  private fun group(shapes: String) = """{"ty":"gr","it":[$shapes,$transform]}"""

  private fun verify(
    name: String,
    input: String,
    modifier: String,
    maxMeanRedError: Float = 0.0001f,
    expected: (Int) -> Geometry,
  ) {
    assertMatchesStaticFrames(
      animation("${group(input)},$modifier,$stroke"),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "groupmod-motion-$name",
      frames = (0..10).toList(),
      maxMeanRedError = maxMeanRedError,
      staticAt = { animation("${path(expected(it).transform { p -> affine(p) }.json())},$stroke") },
    )
  }

  @Test
  fun offsetUsesLocalDistance() =
    verify(
      "offset",
      path(square().json()),
      """{"ty":"op","a":${animated("[0]","[6]")},"lj":1,"ml":${fixed("100")}}""",
      // Coordinate tests independently bound every live vertex to 0.0002 units. Rotated
      // native strokes retain a small edge-coverage difference from the double-precision oracle.
      maxMeanRedError = 0.0006f,
    ) {
      square(20 + it * 0.6)
    }

  @Test
  fun twistUsesLocalCenterAndDistance() =
    verify(
      "twist",
      path(square().json()),
      """{"ty":"tw","a":${animated("[0]","[160]")},"c":${fixed("[32,32]")}}""",
    ) { frame ->
      square().transform { p ->
        val q = p - Point(32.0, 32.0)
        val angle = frame * 16 * hypot(q.x, q.y) * PI / 18000
        Point(32 + q.x * cos(angle) - q.y * sin(angle), 32 + q.x * sin(angle) + q.y * cos(angle))
      }
    }

  @Test
  fun puckerPreservesDeferredTransform() =
    verify("pucker", path(square().json()), """{"ty":"pb","a":${animated("[0]","[40]")}}""") { frame
      ->
      val f = frame * 0.04
      val v = square().v.map { it + (Point(32.0, 32.0) - it) * f }
      val controls = square().v.map { (it - Point(32.0, 32.0)) * (2 * f) }
      Geometry(v, controls, controls)
    }

  @Test
  fun zigzagUsesLocalAmplitude() {
    val fixture =
      Json.parseToJsonElement(
          checkNotNull(javaClass.getResource("/zigzag-reference.json")).readText()
        )
        .jsonObject
        .getValue("motion")
        .jsonArray
        .map { it.jsonObject }
        .single { it.getValue("name").jsonPrimitive.content == "square-size-1" }
    verify(
      "zigzag",
      path(fixture.getValue("start").toString()),
      """{"ty":"zz","s":${animated("[-4]","[6]")},"r":${fixed("3")},"pt":1}""",
    ) { frame ->
      val expected =
        fixture.getValue("frames").jsonArray[frame].jsonObject.getValue("expected").jsonObject
      fun points(key: String) =
        expected.getValue(key).jsonArray.map { p ->
          Point(p.jsonArray[0].jsonPrimitive.double, p.jsonArray[1].jsonPrimitive.double)
        }
      Geometry(points("v"), points("i"), points("o"))
    }
  }

  @Test fun offsetLiveCoordinatesMatchIndependentGeometry() = verifyOffsetCoordinates(false)

  @Test fun repeatedOffsetLiveCoordinatesMatchIndependentGeometry() = verifyOffsetCoordinates(true)

  private fun verifyOffsetCoordinates(repeated: Boolean) {
    val input = path(square().json()) + if (repeated) ",$repeater" else ""
    val offset = """{"ty":"op","a":${animated("[0]","[6]")},"lj":1,"ml":${fixed("100")}}"""
    val source =
      (Animation.decodeFromString(animation("${group(input)},$offset,$stroke")).layers.single()
          as ShapeLayer)
        .shapes
    var player: RemoteComposePlayer? = null
    val live = mutableSetOf<String>()
    composeRule.setContent {
      val document =
        rememberRemoteDocument(profile = LottieProfiles.NoRuntimeShaders) {
          val progress = rememberNamedRemoteFloat("progress") { 0f.rf }
          val output =
            (gatherShapesForTest(source, LottieSettings(progress * 40f))
                .flatMap { it.shapes }
                .single() as RemoteLottiePath)
              .path
              .single()
          check(output.vertices.size == 12)
          fun named(kind: String, points: List<List<RemoteFloat>>) = points.mapIndexed { i, p ->
            p.mapIndexed { j, value ->
              if (value.constantValueOrNull == null) live.add("$kind-$i-$j")
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
    assertWithMessage("live vertex coordinates")
      .that(live.count { it.startsWith("v-") })
      .isEqualTo(24)
    val errors = mutableListOf<String>()
    for (frame in 0..10) {
      composeRule.runOnIdle { checkNotNull(player).setUserLocalFloat("progress", frame / 40f) }
      capture("motion").recycle()
      val local = square(20 + frame * 0.6)
      val repeatedGeometry =
        if (repeated) local.transform { affine(it, 0.8, 0.8, 20.0, 1.0, -3.0) } else local
      val expected = repeatedGeometry.transform { affine(it) }
      for (name in live) {
        val (kind, i, j) = name.split("-")
        val point = if (kind == "v") expected.v[i.toInt() / 3] else Point(0.0, 0.0)
        val target = if (j == "0") point.x else point.y
        val actual = checkNotNull(player).getNamedFloat(RemoteState.Domain.User.prefixed(name))
        if (kotlin.math.abs(actual - target) > 0.0002)
          errors.add("$frame/$name: expected=$target, actual=$actual")
      }
    }
    assertWithMessage(errors.joinToString("\n")).that(errors).isEmpty()
  }

  private val repeater
    get() =
      """{"ty":"rp","c":${fixed("1")},"o":${fixed("1")},"tr":{"a":${fixed("[32,32]")},"p":${fixed("[1,-3]")},"s":${fixed("[80,80]")},"r":${fixed("20")},"so":${fixed("100")},"eo":${fixed("100")}}}"""

  @Test
  fun repeaterTransformComposesBeforeOuterGroupTransform() {
    verify(
      "repeater",
      "${path(square().json())},$repeater",
      """{"ty":"op","a":${animated("[0]","[6]")},"lj":1,"ml":${fixed("100")}}""",
      maxMeanRedError = 0.0006f,
    ) { frame ->
      square(20 + frame * 0.6).transform { affine(it, 0.8, 0.8, 20.0, 1.0, -3.0) }
    }
  }
}
