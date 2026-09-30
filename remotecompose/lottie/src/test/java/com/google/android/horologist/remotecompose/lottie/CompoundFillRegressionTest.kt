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

class CompoundFillRegressionTest : FixtureParityHarness() {
  @Test fun nonzeroWindingKeepsHole() = verify("nonzero", rule = 1)

  @Test fun evenOddKeepsHole() = verify("evenodd", rule = 2)

  @Test fun gradientKeepsHole() = verify("gradient", rule = 2, gradient = true)

  @Test fun overlapUsesOneOpacityApplication() = verify("opacity", overlap = true)

  @Test fun nestedGroupKeepsHole() = verify("nested", rule = 2, nested = true)

  @Test
  fun parentFillCombinesDirectAndGroupContours() = verify("inherited", rule = 2, inherited = true)

  private fun verify(
    name: String,
    rule: Int = 1,
    gradient: Boolean = false,
    overlap: Boolean = false,
    nested: Boolean = false,
    inherited: Boolean = false,
  ) {
    fun ellipse(size: Int, reverse: Boolean = false) =
      """{"ty":"el","d":${if (reverse) 3 else 1},"p":${fixed("[32,32]")},"s":${fixed("[$size,$size]")}}"""
    val outer = if (overlap) rectangle(fixed("[27,32]")) else ellipse(46)
    val inner = if (overlap) rectangle(fixed("[37,32]")) else ellipse(20, rule == 1)
    val contours = outer + "," + if (inherited) """{"ty":"gr","it":[$inner]}""" else inner
    val paint =
      if (gradient)
        """"ty":"gf","t":1,"s":${fixed("[0,0]")},"e":${fixed("[64,0]")},"g":{"p":2,"k":${fixed("[0,1,0,0,1,0,0,1]")}}"""
      else """"ty":"fl","c":${fixed("[1,0,0,1]")}"""
    val shapes = """$contours,{$paint,"r":$rule,"o":${fixed(if (overlap) "50" else "100")}}"""
    compare(
      animation(if (nested) """{"ty":"gr","it":[$shapes]}""" else shapes),
      "compoundfill_$name",
      animated = false,
      maxForegroundError = 0.01,
    )
  }
}
