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

/** Compare local modifier geometry and final paint-space composition with Lottie Android. */
class GroupModifierSpaceTest : FixtureParityHarness() {
  private fun path(vertices: String) =
    """{"ty":"sh","ks":${fixed("""{"c":true,"v":$vertices,"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}""")}}"""

  private val source
    get() = path("[[12,12],[52,12],[52,20],[12,52]]")

  private val stroke
    get() =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""

  private val rounding
    get() = """{"ty":"rd","r":${animated("[0]","[12]")}}"""

  private fun group(shapes: String) = """{"ty":"gr","it":[$shapes]}"""

  private fun transform(scale: String, rotation: String = fixed("0")) =
    """{"ty":"tr","a":${fixed("[32,32]")},"p":${fixed("[32,32]")},"s":$scale,"r":$rotation,"o":${fixed("100")}}"""

  private fun verify(name: String, shapes: String) =
    compare(
      animation(shapes).replace("\"op\":40", "\"op\":10.01"),
      "groupmod_$name",
      maxForegroundError = 0.02,
    )

  @Test
  fun uniformScaleBeforeOuterRound() =
    verify("uniform", "${group("$source,${transform(fixed("[50,50]"))}")},$rounding,$stroke")

  @Test
  fun nonuniformScaleBeforeOuterRound() =
    verify("nonuniform", "${group("$source,${transform(fixed("[60,110]"))}")},$rounding,$stroke")

  @Test
  fun animatedScaleBeforeOuterRound() =
    verify(
      "live-scale",
      "${group("$source,${transform(animated("[40,90]","[100,60]"))}")},$rounding,$stroke",
    )

  @Test
  fun mirroredScaleBeforeOuterRound() =
    verify("mirrored", "${group("$source,${transform(fixed("[-70,110]"))}")},$rounding,$stroke")

  @Test
  fun nestedTransformsBeforeOuterRound() {
    val inner = group("$source,${transform(fixed("[70,110]"), fixed("30"))}")
    verify(
      "nested",
      "${group("$inner,${transform(fixed("[80,60]"), fixed("-20"))}")},$rounding,$stroke",
    )
  }

  @Test
  fun inheritedPaintBeforeOuterRound() =
    verify("paint-before", "${group("$source,${transform(fixed("[60,110]"))}")},$stroke,$rounding")

  @Test
  fun styledGroupControl() =
    verify("styled", "${group("$source,$stroke,${transform(fixed("[60,110]"))}")},$rounding")

  @Test
  fun transformedHoleSharesParentFill() {
    val outer = path("[[8,8],[56,8],[56,56],[8,56]]")
    val inner = group("${path("[[12,12],[52,12],[52,52],[12,52]]")},${transform(fixed("[50,70]"))}")
    val fill = """{"ty":"fl","r":2,"c":${fixed("[1,0,0,1]")},"o":${fixed("50")}}"""
    verify("compound-hole", "$outer,$inner,$rounding,$fill")
  }
}
