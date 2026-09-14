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

import org.junit.Ignore
import org.junit.Test

class StrokeMiterRegressionTest : FixtureParityHarness() {
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun solidLowMiterBevelsCorners() = verify(false, 0)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun solidHighMiterRetainsCorners() = verify(false, 4)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun gradientLowMiterBevelsCorners() = verify(true, 0)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun gradientHighMiterRetainsCorners() = verify(true, 4)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun solidAnimatedMiterUpdatesExistingDocument() = verifyMotion(false)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun gradientAnimatedMiterUpdatesExistingDocument() = verifyMotion(true)

  private fun verify(gradient: Boolean, limit: Int) =
    compare(
      fixture(gradient, limit.toString()),
      "miter_${if (gradient) "gradient" else "solid"}_$limit",
      animated = false,
      maxForegroundError = 0.001,
    )

  private fun verifyMotion(gradient: Boolean) =
    assertMatchesStaticFrames(
      fixture(gradient, "0", animatedLimit = animated("[0]", "[4]")),
      referenceProbeAt = { Probe(32, 20, 1f) },
      // A right-angle miter has ratio sqrt(2): limit 0 bevels, limits 2/4 retain it.
      // The oracle uses explicit bevel/miter joins, not the animated miter implementation.
      staticAt = { frame -> fixture(gradient, "4", join = if (frame == 0) 3 else 1) },
      artifactName = "miter-${if (gradient) "gradient" else "solid"}",
    )

  private fun fixture(
    gradient: Boolean,
    limit: String,
    join: Int = 1,
    animatedLimit: String? = null,
  ): String {
    val paint =
      if (gradient) {
        """"ty":"gs","t":1,"s":${fixed("[0,0]")},"e":${fixed("[64,0]")},
        "g":{"p":2,"k":${fixed("[0,1,0,0,1,1,0,0]")}}"""
      } else {
        """"ty":"st","c":${fixed("[1,0,0,1]")}"""
      }
    val live = animatedLimit?.let { ",\"ml2\":$it" }.orEmpty()
    return animation(
      """${rectangle(fixed("[32,32]"), fixed("[24,24]"))},
        {$paint,"o":${fixed("100")},"w":${fixed("10")},"lc":1,"lj":$join,"ml":$limit$live}"""
    )
  }
}
