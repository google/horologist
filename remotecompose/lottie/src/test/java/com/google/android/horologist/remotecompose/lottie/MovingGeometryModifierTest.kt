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

import org.junit.Test

class MovingGeometryModifierTest : MotionPixelHarness() {
  // [SP_GEOMETRY_01] Full trim preserves the animated rectangle at both endpoints and midpoint.
  @Test fun preservesMovingRectangleWithFullTrim() = moves("rc", trim(100))

  // [SP_GEOMETRY_02] Full trim preserves the animated ellipse at both endpoints and midpoint.
  @Test fun preservesMovingEllipseWithFullTrim() = moves("el", trim(100))

  // [SP_GEOMETRY_03] A nonzero partial trim translates the visible rectangle stroke.
  @Test fun movesHalfTrimmedRectangleStroke() = moves("rc", trim(50), stroke = true)

  // [SP_GEOMETRY_04] A nonzero partial trim translates the visible ellipse stroke.
  @Test fun movesHalfTrimmedEllipseStroke() = moves("el", trim(50), stroke = true)

  // [SP_GEOMETRY_05] A zero rounding modifier is an identity even on an animated rectangle.
  @Test fun preservesMovingRectangleWithZeroCornerRounding() = moves("rc", rounding(0))

  // [SP_GEOMETRY_06] A nonzero rounding modifier remains attached to the moving rectangle.
  @Test fun movesRectangleWithRoundedCorners() = moves("rc", rounding(4))

  // [SP_GEOMETRY_07] Rounding followed by trim works throughout playback, not just at frame zero.
  @Test
  fun movesRoundedRectangleWithHalfTrimmedStroke() =
    moves("rc", "${rounding(4)},${trim(50)}", stroke = true)

  // [SP_GEOMETRY_08] Both dimensions of a rectangle may change before a nonzero trim.
  @Test
  fun resizesHalfTrimmedRectangleDuringPlayback() {
    val position = fixed("[32,32]")
    val dynamic = rectangle(position, animated("[8,8]", "[24,24]"))
    assertMatchesStaticFrames(
      animation("$dynamic,${trim(50)},$redStroke"),
      referenceProbeAt = { frame -> Probe(32 + (8 + frame * 16 / 10) / 2, 32, 1f) },
    ) { frame ->
      val size = 8 + frame * 16 / 10
      animation("${rectangle(position, fixed("[$size,$size]"))},${trim(50)},$redStroke")
    }
  }

  // [SP_GEOMETRY_09] The radius removes the outer corner while retaining the filled interior.
  @Test
  fun removesRectangleCornerPixelsWhileRoundedShapeMoves() {
    val shape = rectangle(animated("[16,32]", "[48,32]"))
    val progress = show(animation("$shape,${rounding(4)},$redFill"))
    for (frame in listOf(0, 5, 10)) {
      advance(progress, frame.toFloat())
      val x = 16 + frame * 32 / 10
      assertPixels(Probe(x, 32, 1f), Probe(x - 8, 24, 0f))
    }
  }

  // [SP_GEOMETRY_10] An intrinsic animated radius changes corners before full trim is applied.
  @Test
  fun updatesRectangleCornerRadiusDuringTrimmedPlayback() {
    val shape = rectangle(fixed("[32,32]"), radius = animated("[0]", "[8]"))
    val progress = show(animation("$shape,${trim(100)},$redFill"))
    assertPixels(Probe(24, 24, 1f), Probe(32, 32, 1f))
    advance(progress, 5f)
    assertPixels(Probe(24, 24, 0f), Probe(32, 32, 1f))
    advance(progress, 10f)
    assertPixels(Probe(25, 25, 0f), Probe(32, 32, 1f))
  }

  // [SP_GEOMETRY_11] A trim endpoint updates from empty to half to full while geometry moves.
  @Test
  fun revealsMovingRectangleAsTrimEndpointAnimates() {
    val shape = rectangle(animated("[16,32]", "[48,32]"))
    val modifier =
      """{"ty":"tm","s":${fixed("0")},"e":${animated("[0]", "[100]")},"o":${fixed("0")},"m":1}"""
    val progress = show(animation("$shape,$modifier,$redStroke"))
    assertPixels(Probe(24, 32, 0f), Probe(8, 32, 0f))
    advance(progress, 5f)
    assertPixels(Probe(40, 32, 1f), Probe(24, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(56, 32, 1f), Probe(40, 32, 1f))
  }

  private fun moves(type: String, modifier: String, stroke: Boolean = false) {
    val paint = if (stroke) redStroke else redFill
    assertMatchesStaticFrames(
      animation("${shape(type, animated("[16,32]", "[48,32]"))},$modifier,$paint"),
      referenceProbeAt = { frame -> Probe(16 + frame * 32 / 10 + if (stroke) 8 else 0, 32, 1f) },
    ) { frame ->
      val x = 16 + frame * 32 / 10
      animation("${shape(type, fixed("[$x,32]"))},$modifier,$paint")
    }
  }

  private fun shape(type: String, position: String): String =
    if (type == "rc") rectangle(position)
    else """{"ty":"el","d":1,"p":$position,"s":${fixed("[16,16]")}}"""

  private fun trim(end: Int): String =
    """{"ty":"tm","s":${fixed("0")},"e":${fixed("$end")},"o":${fixed("0")},"m":1}"""

  private fun rounding(radius: Int): String = """{"ty":"rd","r":${fixed("$radius")}}"""

  private val redStroke =
    """{"ty":"st","lc":1,"lj":1,"ml":4,"c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("4")}}"""
}
