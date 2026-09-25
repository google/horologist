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

class TrimScopeRegressionTest : FixtureParityHarness() {
  private val outline: String
    get() =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("3")},"lc":1,"lj":1}"""

  private fun trim(end: Int): String =
    """{"ty":"tm","s":${fixed("0")},"e":${fixed(end.toString())},"o":${fixed("0")},"m":1}"""

  private val rounding: String
    get() = """{"ty":"rd","r":${fixed("6")}}"""

  @Test
  fun roundingDoesNotAffectFollowingGeometry() =
    verify("$rounding,${rectangle(fixed("[32,32]"))},$outline", "round_before_geometry")

  @Test
  fun roundingBeforeAGroupDoesNotLeakIntoIt() =
    verify(
      "$rounding,{\"ty\":\"gr\",\"it\":[${rectangle(fixed("[32,32]"))},$outline]}",
      "round_before_group",
    )

  @Test
  fun roundingAfterAGroupReachesItsInheritedStroke() =
    verify(
      "{\"ty\":\"gr\",\"it\":[${rectangle(fixed("[32,32]"))}]},$rounding,$outline",
      "round_group_inherited_stroke",
    )

  @Test
  fun trimDoesNotAffectFollowingGeometry() =
    verify("${trim(25)},${rectangle(fixed("[32,32]"))},$outline", "trim_before_geometry")

  @Test
  fun trimOnlyAffectsEarlierOfTwoShapes() =
    verify(
      "${rectangle(fixed("[16,32]"))},${trim(25)},${rectangle(fixed("[48,32]"))},$outline",
      "trim_between_geometry",
    )

  @Test
  fun trimScopeStaysCorrectWhenGeometryMoves() =
    verify(
      "${rectangle(animated("[16,32]", "[24,32]"))},${trim(25)},${rectangle(fixed("[48,32]"))},$outline",
      "trim_scope_moving",
      true,
    )

  @Test
  fun trimBeforeAGroupDoesNotLeakIntoIt() =
    verify(
      "${trim(25)},{\"ty\":\"gr\",\"it\":[${rectangle(fixed("[32,32]"))},$outline]}",
      "trim_before_group",
    )

  @Test
  fun trimAfterAGroupReachesItsInheritedStroke() =
    verify(
      "{\"ty\":\"gr\",\"it\":[${rectangle(fixed("[32,32]"))}]},${trim(25)},$outline",
      "trim_group_inherited_stroke",
    )

  @Test
  fun compoundTrimsAreBothApplied() =
    verify("${rectangle(fixed("[32,32]"))},${trim(75)},${trim(50)},$outline", "trim_compound")

  @Test
  fun compoundTrimsStayLiveOnMovingGeometry() =
    verify(
      "${rectangle(animated("[24,32]", "[40,32]"))},${trim(75)},${trim(50)},$outline",
      "trim_compound_moving",
      true,
    )

  @Test
  fun localAndParentTrimsBothApply() =
    verify(
      "{\"ty\":\"gr\",\"it\":[${rectangle(fixed("[32,32]"))},${trim(75)},$outline]},${trim(50)}",
      "trim_compound_parent",
    )

  @Test
  fun laterAnimatedTrimMeasuresTheEarlierVisibleExtent() {
    val later =
      """{"ty":"tm","s":${fixed("0")},"e":${animated("[25]", "[75]")},"o":${fixed("0")},"m":1}"""
    verify(
      "${rectangle(fixed("[32,32]"))},${trim(75)},$later,$outline",
      "trim_compound_animated",
      true,
    )
  }

  @Test
  fun laterOffsetUsesTheEarlierVisibleExtent() {
    val later = """{"ty":"tm","s":${fixed("0")},"e":${fixed("50")},"o":${fixed("90")},"m":1}"""
    verify("${rectangle(fixed("[32,32]"))},${trim(75)},$later,$outline", "trim_compound_offset")
  }

  private fun verify(shapes: String, name: String, moving: Boolean = false) =
    compare(animation(shapes), name, animated = moving, maxForegroundError = 0.03)
}
