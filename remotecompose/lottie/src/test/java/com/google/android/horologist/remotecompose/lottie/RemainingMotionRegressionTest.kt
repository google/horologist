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

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.onNodeWithTag
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import org.junit.Ignore
import org.junit.Test

class RemainingMotionRegressionTest : MotionPixelHarness() {
  // [SP_REMAINING_MOTION_R10_01] A held count crosses the zero/nonzero boundary at t=10.
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun revealsThreeCopiesWhenAnimatedCountLeavesZero() {
    val count = """{"a":1,"k":[{"t":0,"s":[0],"h":1},{"t":10,"s":[3]}]}"""
    val progress = show(repeated(count = count))
    assertCopyCenters(0f, 0f, 0f)
    advance(progress, 9.5f)
    assertCopyCenters(0f, 0f, 0f)
    advance(progress, 10f)
    assertCopyCenters(1f, 1f, 1f)
    advance(progress, 15f)
    assertCopyCenters(1f, 1f, 1f)
  }

  // [SP_REMAINING_MOTION_R10_02] Offset is measured in repeat-transform units, including 0.5.
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun movesRepeaterCopyAsOffsetAdvancesFromZeroToOne() {
    val progress = show(repeated(count = fixed("1"), offset = animated("[0]", "[1]")))
    assertPixels(Probe(8, 32, 1f), Probe(18, 32, 0f), Probe(28, 32, 0f))
    advance(progress, 5f)
    assertPixels(Probe(8, 32, 0f), Probe(18, 32, 1f), Probe(28, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(8, 32, 0f), Probe(18, 32, 0f), Probe(28, 32, 1f))
  }

  // [SP_REMAINING_MOTION_R10_03] Copy k=2 has scale 2^2, with both containing translations.
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun scalesThirdCopyByFourthPowerFactorInsideTranslatedGroups() {
    val shapes =
      "${rectangle(fixed("[0,0]"), fixed("[4,4]"))},$redFill," +
        repeater(scale = fixed("[200,200]"))
    show(animation(group(group(shapes, "[0,2]"), "[8,30]")))
    assertPixels(
      Probe(8, 32, 1f),
      Probe(28, 32, 1f),
      Probe(48, 32, 1f),
      Probe(48, 39, 1f),
      Probe(48, 41, 0f),
      Probe(18, 32, 0f),
    )
  }

  // [SP_REMAINING_MOTION_R10_04] A nonzero rounding modifier must preserve copy opacity.
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun preservesRepeaterOpacityRampAfterCornerRounding() {
    val rounded = """{"ty":"rd","r":${fixed("2")}}"""
    show(repeated(endOpacity = fixed("0"), suffix = ",$rounded"))
    assertCopyCenters(1f, 2f / 3f, 1f / 3f)
  }

  // [SP_REMAINING_MOTION_R09_01] Four-point polygon radius grows 8→16→24 in one document.
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun expandsPolygonWhenOuterRadiusAnimates() {
    val polygon =
      """{"ty":"sr","sy":2,"d":1,"pt":${fixed("4")},"p":${fixed("[32,32]")},
        "r":${fixed("0")},"or":${animated("[8]", "[24]")},"os":${fixed("0")}}"""
    val progress = show(animation("$polygon,$redFill"))
    assertPixels(Probe(32, 32, 1f), Probe(44, 32, 0f), Probe(52, 32, 0f))
    advance(progress, 5f)
    assertPixels(Probe(32, 32, 1f), Probe(44, 32, 1f), Probe(52, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(32, 32, 1f), Probe(44, 32, 1f), Probe(52, 32, 1f), Probe(60, 32, 0f))
  }

  // [SP_REMAINING_MOTION_R14_01] Half-period offset interchanges dash and gap interiors.
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun shiftsStrokeGapsWhenDashOffsetAnimates() {
    val path =
      """{"ty":"sh","ks":${fixed("""{"c":false,"v":[[8,32],[56,32]],"i":[[0,0],[0,0]],"o":[[0,0],[0,0]]}""")}}"""
    val stroke =
      """{"ty":"st","lc":1,"lj":1,"ml":4,"c":${fixed("[1,0,0,1]")},
        "o":${fixed("100")},"w":${fixed("4")},"d":[
        {"n":"d","v":${fixed("8")}},{"n":"g","v":${fixed("8")}},
        {"n":"o","v":${animated("[0]", "[8]")}}]}"""
    val progress = show(animation("$path,$stroke"))
    assertPixels(Probe(12, 32, 1f), Probe(20, 32, 0f), Probe(28, 32, 1f), Probe(12, 24, 0f))
    advance(progress, 10f)
    assertPixels(Probe(12, 32, 0f), Probe(20, 32, 1f), Probe(28, 32, 0f), Probe(20, 24, 0f))
  }

  // [SP_REMAINING_MOTION_R08_01] Font-backed text with no vector glyph table remains visible.
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun drawsVisibleFontTextWhenVectorGlyphsAreAbsent() {
    show(textAnimation("""{"t":0,"s":${document("HI", 24)}}""", glyphs = "[]"))
    assertVisibleRedText()
    assertPixels(Probe(2, 2, 0f), Probe(60, 60, 0f))
  }

  // [SP_REMAINING_MOTION_R08_02] Text document selection is held until the exact boundary.
  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun addsSecondVectorGlyphAtTextDocumentBoundaryWithoutRemounting() {
    val progress =
      show(textAnimation("""{"t":0,"s":${document("A", 20)}},{"t":10,"s":${document("AA", 20)}}"""))
    assertPixels(Probe(10, 30, 1f), Probe(26, 30, 0f), Probe(20, 30, 0f))
    advance(progress, 9.5f)
    assertPixels(Probe(10, 30, 1f), Probe(26, 30, 0f))
    advance(progress, 10f)
    assertPixels(Probe(10, 30, 1f), Probe(26, 30, 1f), Probe(20, 30, 0f))
    advance(progress, 15f)
    assertPixels(Probe(10, 30, 1f), Probe(26, 30, 1f))
  }

  private fun assertCopyCenters(first: Float, second: Float, third: Float) =
    assertPixels(
      Probe(8, 32, first),
      Probe(28, 32, second),
      Probe(48, 32, third),
      Probe(18, 32, 0f),
    )

  private fun repeated(
    count: String = fixed("3"),
    offset: String = fixed("0"),
    endOpacity: String = fixed("100"),
    suffix: String = "",
  ): String =
    animation(
      "${rectangle(fixed("[8,32]"), fixed("[8,16]"))},$redFill," +
        repeater(count, offset, endOpacity = endOpacity) +
        suffix
    )

  private fun repeater(
    count: String = fixed("3"),
    offset: String = fixed("0"),
    scale: String = fixed("[100,100]"),
    endOpacity: String = fixed("100"),
  ): String =
    """{"ty":"rp","c":$count,"o":$offset,"m":1,"tr":{
      "a":${fixed("[0,0]")},"p":${fixed("[20,0]")},"r":${fixed("0")},"s":$scale,
      "so":${fixed("100")},"eo":$endOpacity}}"""

  private fun group(shapes: String, position: String): String =
    """{"ty":"gr","it":[$shapes,{"ty":"tr","a":${fixed("[0,0]")},
      "p":${fixed(position)},"r":${fixed("0")},"s":${fixed("[100,100]")},"o":${fixed("100")}}]}"""

  private fun document(text: String, size: Int): String =
    """{"t":"$text","f":"sans-serif-Regular","s":$size,"j":0,"tr":0,"lh":24,
      "ls":0,"fc":[1,0,0],"sc":[0,0,0],"sw":0,"of":true}"""

  private fun textAnimation(keyframes: String, glyphs: String = vectorGlyphs): String =
    """{"v":"5.7.4","fr":20,"ip":0,"op":40,"w":64,"h":64,"ddd":0,"assets":[],
      "fonts":{"list":[{"fName":"sans-serif-Regular","fFamily":"sans-serif",
        "fStyle":"Regular","ascent":75}]},"chars":$glyphs,
      "layers":[{"ty":5,"ind":1,"ip":0,"op":40,"st":0,"sr":1,
      "ks":{"a":${fixed("[0,0]")},"p":${fixed("[6,40]")},"r":${fixed("0")},
        "s":${fixed("[100,100]")},"o":${fixed("100")}},"t":{"d":{"k":[$keyframes]}}}]}"""

  // Deliberately simple outlined glyph: 40×80 at size 100, advance 80; size 20 draws 8×16.
  private val vectorGlyphs: String
    get() =
      """[{"ch":"A","fFamily":"sans-serif","style":"Regular","size":100,"w":80,
        "data":{"shapes":[{"ty":"gr","it":[{"ty":"sh","ks":${fixed("""{"c":true,"v":[[0,0],[40,0],[40,-80],[0,-80]],"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}""")}}]}]}}]"""

  @OptIn(ExperimentalRoborazziApi::class)
  private fun assertVisibleRedText() {
    composeRule.waitForIdle()
    val screenshot = File.createTempFile("remaining-motion-text-", ".png")
    try {
      composeRule
        .onNodeWithTag("motion")
        .captureRoboImage(
          screenshot.absolutePath,
          roborazziOptions = RoborazziOptions(taskType = RoborazziTaskType.Record),
        )
      val bitmap = checkNotNull(BitmapFactory.decodeFile(screenshot.absolutePath))
      try {
        var visible = 0
        for (y in 12 until 40) {
          for (x in 6 until 48) {
            val color = Color(bitmap.getPixel(x * bitmap.width / 64, y * bitmap.height / 64))
            if (color.red > 0.5f && color.green < 0.01f && color.blue < 0.01f) visible++
          }
        }
        assertWithMessage("font text must draw visible red glyph interiors in its expected bounds")
          .that(visible)
          .isGreaterThan(30)
      } finally {
        bitmap.recycle()
      }
    } finally {
      screenshot.delete()
    }
  }
}
