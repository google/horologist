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

class RepeaterOpacityTest : MotionPixelHarness() {
  // [SP_REPEATER_01] A standard repeater transform has so/eo and does not require ordinary o.
  @Test
  fun drawsStandardRepeaterWithoutOrdinaryTransformOpacity() {
    show(repeated(3, fixed("100"), fixed("100")))
    assertCopies(1f, 1f, 1f)
  }

  // [SP_REPEATER_02] Lottie-Android v6.7.1 RepeaterContent.draw uses copyIndex/copies.
  // Three copies therefore use 100%, 66.7%, 33.3% for a 100% to 0% ramp.
  @Test
  fun fadesEachCopyUsingStartAndEndOpacity() {
    show(repeated(3, fixed("100"), fixed("0")))
    assertCopies(1f, 2f / 3f, 1f / 3f)
  }

  // [SP_REPEATER_03] Named progress updates both ramp endpoints without rebuilding the document.
  @Test
  fun updatesEachCopyWhenStartAndEndOpacityAnimate() {
    val progress = show(repeated(3, animated("[100]", "[0]"), animated("[0]", "[100]")))
    assertCopies(1f, 2f / 3f, 1f / 3f)
    advance(progress, 5f)
    assertCopies(0.5f, 0.5f, 0.5f)
    advance(progress, 10f)
    assertCopies(0f, 1f / 3f, 2f / 3f)
  }

  // [SP_REPEATER_04] Zero copies renders no source geometry and does not divide by zero.
  @Test
  fun leavesBackgroundUntouchedWhenRepeaterHasZeroCopies() {
    show(repeated(0, fixed("100"), fixed("0")))
    assertCopies(0f, 0f, 0f)
  }

  // [SP_REPEATER_05] A sole copy uses start opacity even when end opacity differs.
  @Test
  fun givesOnlyCopyTheStartOpacity() {
    show(repeated(1, fixed("40"), fixed("0")))
    assertCopies(0.4f, 0f, 0f)
  }

  // [SP_REPEATER_06] Fully transparent endpoint controls remove all copies from the image.
  @Test
  fun leavesBackgroundUntouchedWhenBothOpacityEndpointsAreZero() {
    show(repeated(3, fixed("0"), fixed("0")))
    assertCopies(0f, 0f, 0f)
  }

  // [SP_REPEATER_07] Missing opacity endpoints default to fully opaque copies.
  @Test
  fun drawsOpaqueCopiesWhenOpacityEndpointsAreOmitted() {
    show(repeated(3))
    assertCopies(1f, 1f, 1f)
  }

  // [SP_REPEATER_08] Legacy ordinary opacity remains the fallback when endpoints are omitted.
  @Test
  fun preservesLegacyOpacityWhenStartAndEndOpacityAreOmitted() {
    show(repeated(3, legacyOpacity = fixed("40")))
    assertCopies(0.4f, 0.4f, 0.4f)
  }

  // [SP_REPEATER_09] Explicit endpoint opacity overrides the legacy ordinary opacity field.
  @Test
  fun usesExplicitOpacityRampInsteadOfLegacyOrdinaryOpacity() {
    show(repeated(3, fixed("100"), fixed("0"), legacyOpacity = fixed("20")))
    assertCopies(1f, 2f / 3f, 1f / 3f)
  }

  // [SP_REPEATER_10] Fractional counts floor the number of copies but retain the ramp denominator.
  @Test
  fun drawsTwoCopiesUsingFractionalCountForOpacityInterpolation() {
    show(repeated(2.5, fixed("100"), fixed("0")))
    assertCopies(1f, 0.6f, 0f)
  }

  private fun assertCopies(first: Float, second: Float, third: Float) {
    assertPixels(
      Probe(8, 32, first),
      Probe(28, 32, second),
      Probe(48, 32, third),
      Probe(18, 32, 0f),
    )
  }

  private fun repeated(
    copies: Number,
    startOpacity: String? = null,
    endOpacity: String? = null,
    legacyOpacity: String? = null,
  ): String {
    val opacityFields =
      listOfNotNull(
          startOpacity?.let { "\"so\":$it" },
          endOpacity?.let { "\"eo\":$it" },
          legacyOpacity?.let { "\"o\":$it" },
        )
        .joinToString(separator = "", transform = { ",$it" })
    val repeater =
      """{"ty":"rp","c":${fixed("$copies")},"o":${fixed("0")},"m":1,
        "tr":{"a":${fixed("[0,0]")},"p":${fixed("[20,0]")},"r":${fixed("0")},
          "s":${fixed("[100,100]")}$opacityFields}}"""
    return animation("${rectangle(fixed("[8,32]"), fixed("[8,16]"))},$redFill,$repeater")
  }
}
