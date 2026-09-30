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

/** A repeater's drawing content and inherited path content have different opacity semantics. */
class RepeaterScopeTest : FixtureParityHarness() {
  private val source
    get() = rectangle(fixed("[12,22]"), fixed("[16,16]"))

  private val fill
    get() = """{"ty":"fl","r":1,"c":${fixed("[1,0,0,1]")},"o":${fixed("50")}}"""

  private val stroke
    get() =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("3")},"lc":1,"lj":1,"ml":4}"""

  private fun group(content: String, scaled: Boolean = false) =
    """{"ty":"gr","it":[$content,{"ty":"tr","a":${fixed("[0,0]")},"p":${fixed("[4,5]")},"s":${fixed(if(scaled) "[80,120]" else "[100,100]")},"r":${fixed(if(scaled) "-10" else "0")},"o":${fixed("100")}}]}"""

  private fun repeat(
    copies: String = fixed("3"),
    scale: String = fixed("[100,100]"),
    position: String = animated("[12,0]", "[16,0]"),
  ) =
    """{"ty":"rp","c":$copies,"o":${fixed("0")},"m":1,"tr":{"a":${fixed("[0,0]")},"p":$position,"s":$scale,"r":${fixed("0")},"so":${fixed("100")},"eo":${fixed("0")}}}"""

  private fun verify(name: String, shapes: String) =
    compare(
      animation(shapes).replace("\"op\":40", "\"op\":10.01"),
      "repeaterscope_$name",
      maxForegroundError = 0.02,
    )

  @Test fun ownedFillUsesOpacityRamp() = verify("owned-fill", "$source,$fill,${repeat()}")

  @Test
  fun inheritedFillIgnoresOpacityRamp() = verify("inherited-fill", "$source,${repeat()},$fill")

  @Test
  fun groupedInheritedFillIsRepeated() =
    verify("group-inherited", "${group(source)},${repeat()},$fill")

  @Test
  fun nestedInheritedRepeaterPathIgnoresOpacity() =
    verify("nested-inherited", "${group("$source,${repeat()}")},$fill")

  @Test
  fun styledGroupUnderParentRepeater() =
    verify("styled-group", "${group("$source,$fill",true)},${repeat()}")

  @Test
  fun repeaterScalesOwnedStroke() =
    verify("scaled-stroke", "$source,$stroke,${repeat(scale=animated("[100,100]","[75,75]"))}")

  @Test
  fun repeatedGroupScalesOwnedStroke() =
    verify(
      "scaled-group-stroke",
      "${group("$source,$stroke",true)},${repeat(scale=animated("[100,100]","[75,75]"))}",
    )

  @Test
  fun inheritedLiveCopiesShareFillOpacity() =
    verify(
      "live-copies",
      "$source,${repeat(copies=animated("[1]","[4]"),position=fixed("[8,0]"))},$fill",
    )

  @Test
  fun groupedLiveCopiesShareFillOpacity() =
    verify(
      "group-live-copies",
      "${group(source)},${repeat(copies=animated("[1]","[4]"),position=fixed("[8,0]"))},$fill",
    )

  @Test
  fun consecutiveRepeatersPreserveOwnedOpacity() =
    verify(
      "consecutive",
      "$source,$fill,${repeat(copies=fixed("2"))},${repeat(copies=fixed("2"),position=animated("[0,14]","[0,20]"))}",
    )

  @Test
  fun inheritedOpenStrokeHidesUnusedLiveCopies() {
    val line =
      """{"ty":"sh","ks":${fixed("""{"c":false,"v":[[8,22],[16,22]],"i":[[0,0],[0,0]],"o":[[0,0],[0,0]]}""")}}"""
    verify(
      "open-live-copies",
      "$line,${repeat(copies=animated("[1]","[4]"))},${stroke.replace("\"lc\":1","\"lc\":2")}",
    )
  }

  @Test
  fun inheritedLiveCopiesStayHiddenAfterRounding() =
    verify(
      "rounded-live-copies",
      "${group(source)},${repeat(copies=animated("[1]","[4]"),position=fixed("[8,0]"))},{\"ty\":\"rd\",\"r\":${fixed("4")}},$fill",
    )

  @Test
  fun parentRepeaterTransformsOwnedGradient() {
    val gradient =
      """{"ty":"gf","t":1,"s":${fixed("[4,14]")},"e":${fixed("[20,30]")},"o":${fixed("100")},"g":{"p":2,"k":${fixed("[0,1,0,0,1,0,0,1]")}}}"""
    verify(
      "gradient",
      "${group("$source,$gradient",true)},${repeat(scale=animated("[100,100]","[75,75]"))}",
    )
  }

  @Test
  fun repeaterPathIncludesEarlierPaintedGeometry() =
    verify(
      "earlier-painted-path",
      "$source,$fill,${rectangle(fixed("[12,44]"),fixed("[12,10]"))},${repeat()},$stroke",
    )
}
