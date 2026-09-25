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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import org.junit.Test

@OptIn(ExperimentalRoborazziApi::class)
class PlaybackEffectsRegressionTest : WearScreenshotTest() {
  // [SP_MASK_01] A static add mask exposes only its rectangle throughout playback.
  @Test
  fun keepsStaticMaskAtItsDeclaredPositionDuringPlayback() {
    val progress = show(animation(maskedSolid("""{"a":0,"k":${maskPath(0)}}""")))
    assertPixels(Probe(8, 32, Color.Red), Probe(40, 32, Color.Black))
    advance(progress, 10f)
    assertPixels(Probe(8, 32, Color.Red), Probe(40, 32, Color.Black))
  }

  // [SP_MASK_02] Updating named progress changes the path of an animated add mask.
  @Test
  fun movesVisibleMaskedRegionWhenProgressReachesNextPathKeyframe() {
    val path =
      """{"a":1,"k":[
        {"t":0,"s":[${maskPath(0)}],"o":{"x":0,"y":0},"i":{"x":1,"y":1}},
        {"t":10,"s":[${maskPath(32)}]}
      ]}"""
    val progress = show(animation(maskedSolid(path)))
    assertPixels(Probe(8, 32, Color.Red), Probe(40, 32, Color.Black))
    advance(progress, 5f)
    assertPixels(Probe(24, 32, Color.Red), Probe(8, 32, Color.Black), Probe(40, 32, Color.Black))
    advance(progress, 10f)
    assertPixels(Probe(8, 32, Color.Black), Probe(40, 32, Color.Red))
  }

  // [SP_GRADIENT_00] RGB stops alone form an opaque gradient over the background.
  @Test
  fun keepsGradientOpaqueWhenNoOpacityStopsArePresent() {
    show(gradientAnimation(2, """{"a":0,"k":[$redBlue]}"""), Color.Green)
    assertPixels(
      Probe(32, 32, Color(0.5f, 0f, 0.5f), 0.04f),
      Probe(2, 32, Color.Red, 0.06f),
      Probe(61, 32, Color.Blue, 0.06f),
    )
  }

  // [SP_GRADIENT_05] A single RGB stop extends its opaque color across the gradient.
  @Test
  fun fillsEntireGradientWithTheOnlyColorStop() {
    show(gradientAnimation(1, """{"a":0,"k":[0.5,1,0,0]}"""), Color.Green)
    assertPixels(Probe(8, 32, Color.Red), Probe(32, 32, Color.Red), Probe(56, 32, Color.Red))
  }

  // [SP_GRADIENT_06] Coincident RGB stops preserve a discontinuous red-to-blue boundary.
  @Test
  fun preservesHardColorEdgeWhenRgbStopsShareAPosition() {
    val rgb = "0,1,0,0,0.5,1,0,0,0.5,0,0,1,1,0,0,1"
    show(gradientAnimation(4, """{"a":0,"k":[$rgb]}"""), Color.Green)
    assertPixels(Probe(28, 32, Color.Red), Probe(36, 32, Color.Blue))
  }

  // [SP_GRADIENT_07] Opacity merging preserves both colors at a coincident-stop hard edge.
  @Test
  fun preservesHardColorEdgeWhenIndependentOpacityStopsAreMerged() {
    val rgb = "0,1,0,0,0.5,1,0,0,0.5,0,0,1,1,0,0,1"
    show(gradientAnimation(4, """{"a":0,"k":[$rgb,0,0.5,1,0.5]}"""), Color.Green)
    assertPixels(
      Probe(28, 32, Color(0.5f, 0.5f, 0f), 0.02f),
      Probe(36, 32, Color(0f, 0.5f, 0.5f), 0.02f),
    )
  }

  // [SP_GRADIENT_01] Three opacity stops preserve a transparent center between two RGB stops.
  @Test
  fun revealsBackgroundAtOpacityStopMissingFromRgbStops() {
    show(gradientAnimation(2, """{"a":0,"k":[$redBlue,0,1,0.5,0,1,1]}"""), Color.Green)
    assertTransparentCenterAndOpaqueEdges()
  }

  // [SP_GRADIENT_02] Equal stop counts do not imply matching color and opacity positions.
  @Test
  fun mergesEqualNumbersOfColorAndOpacityStopsAtDifferentPositions() {
    val rgb = "0,1,0,0,0.25,1,0,0,1,0,0,1"
    show(gradientAnimation(3, """{"a":0,"k":[$rgb,0,1,0.5,0,1,1]}"""), Color.Green)
    assertTransparentCenterAndOpaqueEdges()
  }

  // [SP_GRADIENT_03] Animated opacity values update without recreating the composition.
  @Test
  fun revealsBackgroundWhenAnimatedMiddleOpacityReachesZero() {
    val property =
      """{"a":1,"k":[
        {"t":0,"s":[$redBlue,0,1,0.5,1,1,1],"o":{"x":0,"y":0},"i":{"x":1,"y":1}},
        {"t":10,"s":[$redBlue,0,1,0.5,0,1,1]}
      ]}"""
    val progress = show(gradientAnimation(2, property), Color.Green)
    assertPixels(Probe(32, 32, Color(0.5f, 0f, 0.5f), 0.04f))
    advance(progress, 10f)
    assertTransparentCenterAndOpaqueEdges()
  }

  // [SP_GRADIENT_04] An opacity stop can cross an RGB stop while moving the transparent region.
  @Test
  fun movesTransparentRegionWhenAnimatedOpacityStopChangesPosition() {
    val rgb = "0,1,0,0,0.5,1,0,0,1,1,0,0"
    val property =
      """{"a":1,"k":[
        {"t":0,"s":[$rgb,0,1,0.25,0,1,1],"o":{"x":0,"y":0},"i":{"x":1,"y":1}},
        {"t":10,"s":[$rgb,0,1,0.75,0,1,1]}
      ]}"""
    val progress = show(gradientAnimation(3, property), Color.Green)
    assertPixels(Probe(16, 32, Color.Green, 0.05f), Probe(48, 32, Color(2f / 3, 1f / 3, 0f), 0.05f))
    advance(progress, 5f)
    assertPixels(
      Probe(32, 32, Color.Green, 0.05f),
      Probe(16, 32, Color(0.5f, 0.5f, 0f), 0.05f),
      Probe(48, 32, Color(0.5f, 0.5f, 0f), 0.05f),
    )
    advance(progress, 10f)
    assertPixels(Probe(16, 32, Color(2f / 3, 1f / 3, 0f), 0.05f), Probe(48, 32, Color.Green, 0.05f))
  }

  // [SP_PRECOMP_TIMING_00] An unshifted, unstretched precomp keeps both timelines aligned.
  @Test
  fun rendersUnshiftedPrecompWithAlignedParentAndChildMotion() {
    val asset = """{"id":"child","layers":[${movingRectangle()}]}"""
    val progress = show(animation(precomp("child", start = 0, stretch = 1), asset))
    assertRectangleAt(0)
    advance(progress, 10f)
    assertRectangleAt(20)
  }

  // [SP_PRECOMP_TIMING_01] At root frame 20, outer x=20 plus child x=(20-10)/2=5.
  @Test
  fun evaluatesPrecompTransformInParentTimeAndChildMotionInShiftedStretchedTime() {
    val asset = """{"id":"child","layers":[${movingRectangle()}]}"""
    val progress = show(animation(precomp("child", start = 10, stretch = 2), asset))
    advance(progress, 20f)
    assertRectangleAt(25)
    advance(progress, 30f)
    assertRectangleAt(40)
  }

  // [SP_PRECOMP_TIMING_02] Nested precomp transforms each use their containing timeline.
  @Test
  fun composesNestedPrecompTransformsUsingEachContainingTimeline() {
    val inner = precomp("leaf", start = 1, stretch = 2)
    val assets =
      """{"id":"child","layers":[$inner]},
        {"id":"leaf","layers":[${movingRectangle()}]}"""
    val progress = show(animation(precomp("child", start = 10, stretch = 2), assets))
    advance(progress, 20f)
    // Outer x=20, inner x=5, leaf x=(5-1)/2=2.
    assertRectangleAt(27)
  }

  // [SP_PRECOMP_TIMING_03] A remap in seconds changes child time without changing outer time.
  @Test
  fun appliesTimeRemapSecondsToChildrenWhileOuterTransformUsesParentFrame() {
    val asset = """{"id":"child","layers":[${movingRectangle()}]}"""
    val layer = precomp("child", start = 10, stretch = 2, extra = """, "tm":{"a":0,"k":0.5}""")
    val progress = show(animation(layer, asset))
    advance(progress, 20f)
    // Root fr=20: remap 0.5 seconds gives child frame 10; outer x=20 remains unchanged.
    assertRectangleAt(30)
  }

  // [SP_PRECOMP_TIMING_04] A root parent retains root time when its child is a shifted precomp.
  @Test
  fun evaluatesRootParentTransformOutsideShiftedPrecompTimeline() {
    val asset = """{"id":"child","layers":[${movingRectangle()}]}"""
    val parent =
      """{"ty":3,"ind":1,"ip":0,"op":100,"st":0,"sr":1,"ks":${transform(movingPosition())}}"""
    val layer = precomp("child", start = 10, stretch = 2, extra = """, "parent":1""", index = 2)
    val progress = show(animation("$layer,$parent", asset))
    advance(progress, 20f)
    // Root parent x=20 plus precomp x=20 plus child-local x=5.
    assertRectangleAt(45)
  }

  private fun assertTransparentCenterAndOpaqueEdges() {
    assertPixels(
      Probe(32, 32, Color.Green, 0.05f),
      Probe(2, 32, Color.Red, 0.15f),
      Probe(61, 32, Color.Blue, 0.15f),
    )
  }

  private fun assertRectangleAt(left: Int) {
    val probes = mutableListOf(Probe(left + 2, 32, Color.Red), Probe(left + 6, 32, Color.Black))
    if (left >= 2) probes += Probe(left - 2, 32, Color.Black)
    assertPixels(*probes.toTypedArray())
  }

  private fun show(json: String, background: Color = Color.Black): MutableFloatState {
    val decoded = Animation.decodeFromString(json)
    val progress = mutableFloatStateOf(0f)
    composeRule.setContent {
      Box(Modifier.size(64.dp).background(background).testTag("effects")) {
        LottiePreview(decoded, modifier = Modifier.size(64.dp), progress = progress.floatValue)
      }
    }
    return progress
  }

  private fun advance(progress: MutableFloatState, frame: Float) {
    composeRule.runOnIdle { progress.floatValue = frame / 40f }
  }

  private data class Probe(val x: Int, val y: Int, val color: Color, val tolerance: Float = 0.01f)

  private fun assertPixels(vararg probes: Probe) {
    composeRule.waitForIdle()
    val screenshot = File.createTempFile("playback-effects-", ".png")
    try {
      composeRule
        .onNodeWithTag("effects")
        .captureRoboImage(
          screenshot.absolutePath,
          roborazziOptions = RoborazziOptions(taskType = RoborazziTaskType.Record),
        )
      val bitmap = checkNotNull(BitmapFactory.decodeFile(screenshot.absolutePath))
      try {
        for (probe in probes) {
          val pixel =
            Color(bitmap.getPixel(probe.x * bitmap.width / 64, probe.y * bitmap.height / 64))
          for ((actual, expected) in
            listOf(
              pixel.red to probe.color.red,
              pixel.green to probe.color.green,
              pixel.blue to probe.color.blue,
            )) {
            assertWithMessage("pixel (${probe.x}, ${probe.y}), expected ${probe.color}, got $pixel")
              .that(actual)
              .isWithin(probe.tolerance)
              .of(expected)
          }
        }
      } finally {
        bitmap.recycle()
      }
    } finally {
      screenshot.delete()
    }
  }

  private fun animation(layers: String, assets: String = ""): String =
    """{"v":"5.7.4","fr":20,"ip":0,"op":40,"w":64,"h":64,"ddd":0,
      "assets":[$assets],"layers":[$layers]}"""

  private fun transform(position: String = """{"a":0,"k":[0,0,0]}"""): String =
    """{"a":{"a":0,"k":[0,0,0]},"p":$position,"r":{"a":0,"k":0},
      "s":{"a":0,"k":[100,100,100]},"o":{"a":0,"k":100}}"""

  private fun movingPosition(end: Int = 40): String =
    """{"a":1,"k":[
      {"t":0,"s":[0,0,0],"o":{"x":0,"y":0},"i":{"x":1,"y":1}},
      {"t":$end,"s":[$end,0,0]}]}"""

  private fun maskPath(left: Int): String =
    """{"c":true,"v":[[$left,0],[${left + 16},0],[${left + 16},64],[$left,64]],
      "i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}"""

  private fun maskedSolid(path: String): String =
    """{"ty":1,"ind":1,"ip":0,"op":100,"st":0,"sr":1,"sw":64,"sh":64,"sc":"#FF0000",
      "ks":${transform()},"hasMask":true,
      "masksProperties":[{"mode":"a","inv":false,"pt":$path,"o":{"a":0,"k":100},"x":{"a":0,"k":0}}]}"""

  private val redBlue = "0,1,0,0,1,0,0,1"

  private fun gradientAnimation(count: Int, property: String): String =
    animation(
      """{"ty":4,"ind":1,"ip":0,"op":100,"st":0,"sr":1,"ks":${transform()},
      "shapes":[${rectangle(32, 64)},
        {"ty":"gf","t":1,"r":1,"o":{"a":0,"k":100},
          "s":{"a":0,"k":[0,32]},"e":{"a":0,"k":[64,32]},"g":{"p":$count,"k":$property}}]}"""
    )

  private fun rectangle(centerX: Int, width: Int): String =
    """{"ty":"rc","d":1,"p":{"a":0,"k":[$centerX,32]},"s":{"a":0,"k":[$width,64]},"r":{"a":0,"k":0}}"""

  private fun movingRectangle(): String =
    """{"ty":4,"ind":1,"ip":0,"op":100,"st":0,"sr":1,"ks":${transform(movingPosition(20))},
      "shapes":[${rectangle(2, 4)}, {"ty":"fl","r":1,"c":{"a":0,"k":[1,0,0,1]},"o":{"a":0,"k":100}}]}"""

  private fun precomp(
    refId: String,
    start: Int,
    stretch: Int,
    extra: String = "",
    index: Int = 1,
  ): String =
    """{"ty":0,"ind":$index,"refId":"$refId","w":64,"h":64,"ip":0,"op":100,"st":$start,"sr":$stretch,
      "ks":${transform(movingPosition())}$extra}"""
}
