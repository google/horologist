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

/** Uses lottie-android with merge paths explicitly enabled, including three-operand ordering. */
class MergePathsRegressionTest : FixtureParityHarness() {
  @Test fun staticUnion() = checkMerge(2, false)

  @Test fun staticSubtract() = checkMerge(3, false)

  @Test fun staticIntersection() = checkMerge(4, false)

  @Test fun staticXor() = checkMerge(5, false)

  @Test fun movingUnion() = checkMerge(2, true)

  @Test fun movingSubtract() = checkMerge(3, true)

  @Test fun movingIntersection() = checkMerge(4, true)

  @Test fun movingXor() = checkMerge(5, true)

  @Test fun staticNonZeroXorKeepsBothExcludedOverlapsEmpty() = checkXorRegions(false)

  @Test fun movingNonZeroXorKeepsBothExcludedOverlapsEmpty() = checkXorRegions(true)

  @Test
  fun movesMergedGeometryThroughAParentGroupTransform() {
    val group =
      """{"ty":"gr","it":[${operands(2, true)},
      {"ty":"tr","a":${fixed("[0,0]")},"p":${animated("[0,0]", "[8,8]")},
      "s":${fixed("[75,75]")},"r":${fixed("0")},"o":${fixed("100")}}]}"""
    compare(
      animation("$group,$redFill"),
      "merge_group_transform",
      enableMergePaths = true,
      maxForegroundError = 0.01,
    )
  }

  @Test
  fun repeatsMergedGeometryWithScaling() {
    val repeater =
      """{"ty":"rp","c":${fixed("2")},"o":${fixed("0")},
      "tr":{"a":${fixed("[0,0]")},"p":${fixed("[0,16]")},"s":${fixed("[75,75]")},
      "r":${fixed("0")},"so":${fixed("100")},"eo":${fixed("100")}}}"""
    compare(
      animation("${operands(2, true)},$redFill,$repeater"),
      "merge_repeater",
      enableMergePaths = true,
      maxForegroundError = 0.01,
    )
  }

  private fun checkXorRegions(moving: Boolean) {
    val progress = show(mergeAnimation(5, moving))
    for (frame in listOf(0f, 5f, 10f)) {
      advance(progress, frame)
      assertPixels(Probe(20, 16, 1f), Probe(20, 26, 0f), Probe(44, 26, 0f), Probe(32, 40, 1f))
    }
  }

  private fun checkMerge(mode: Int, moving: Boolean) {
    // lottie-android appends the op result to a path using the fill's winding. For XOR, r=1
    // incorrectly fills one excluded region; r=2 preserves the actual set exclusion.
    val json =
      mergeAnimation(mode, moving).let {
        if (mode == 5) it.replace("\"r\":1,\"c\"", "\"r\":2,\"c\"") else it
      }
    compare(
      json,
      "merge_${mode}_${if (moving) "moving" else "static"}",
      animated = moving,
      maxForegroundError = 0.01,
      enableMergePaths = true,
    )
  }

  private fun mergeAnimation(mode: Int, moving: Boolean): String {
    return animation("${operands(mode, moving)},$redFill")
  }

  private fun operands(mode: Int, moving: Boolean): String {
    val first =
      rectangle(if (moving) animated("[16,24]", "[22,24]") else fixed("[16,24]"), fixed("[20,20]"))
    val second = rectangle(fixed("[48,24]"), fixed("[20,20]"))
    val last = rectangle(fixed("[32,32]"), fixed("[40,24]"))
    return "$first,$second,$last,{\"ty\":\"mm\",\"mm\":$mode}"
  }
}
