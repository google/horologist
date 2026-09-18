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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.remote.creation.compose.state.RemoteFloat
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.renderer.gatherShapesForTest
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import kotlin.math.abs
import kotlin.math.floor
import org.junit.Assert.assertThrows
import org.junit.Test

/** Low-level native path algebra is independent of the Lottie parser and RC renderer helpers. */
class NestedBooleanTest : MotionPixelHarness() {
  private val a
    get() = rectangle(animated("[24,32]", "[30,32]"), fixed("[28,28]"))

  private val b
    get() = rectangle(fixed("[36,28]"), fixed("[24,32]"))

  private val c
    get() = rectangle(fixed("[32,38]"), fixed("[38,18]"))

  private fun merge(mode: Int) = """{"ty":"mm","mm":$mode}"""

  private fun rect(l: Float, t: Float, r: Float, b: Float) =
    Path().apply { addRect(l, t, r, b, Path.Direction.CW) }

  private fun combine(a: Path, b: Path, mode: Int): Path =
    Path().apply {
      check(
        op(
          a,
          b,
          when (mode) {
            2 -> Path.Op.UNION
            3 -> Path.Op.DIFFERENCE
            4 -> Path.Op.INTERSECT
            5 -> Path.Op.XOR
            else -> error("mode")
          },
        )
      )
    }

  private fun verify(inner: Int, outer: Int, right: Boolean = false, rightLive: Boolean = false) {
    val second = if (rightLive) rectangle(animated("[36,28]", "[32,28]"), fixed("[24,32]")) else b
    val shapes =
      if (right) "$a,{\"ty\":\"gr\",\"it\":[$second,$c,${merge(inner)}]},${merge(outer)}"
      else "$a,$b,${merge(inner)},$c,${merge(outer)}"
    val side = if (rightLive) "right-live" else if (right) "right" else "left"
    checkFrames("$side-$inner-$outer", "$shapes,$redFill") { frame ->
      val first = rect(10 + frame * 0.6f, 18f, 38 + frame * 0.6f, 46f)
      val shift = if (rightLive) frame * 0.4f else 0f
      val second = rect(24f - shift, 12f, 48f - shift, 44f)
      val third = rect(13f, 29f, 51f, 47f)
      listOf(
        if (right) combine(first, combine(second, third, inner), outer)
        else combine(combine(first, second, inner), third, outer)
      )
    }
  }

  private fun checkFrames(name: String, shapes: String, expectedPaths: (Int) -> List<Path>) {
    val progress = show(animation(shapes))
    val folder = File("build/outputs/lottie-motion/nestedboolean-$name").apply { mkdirs() }
    val errors = mutableListOf<String>()
    for (frame in 0..10) {
      advance(progress, frame.toFloat())
      val actual = capture("motion")
      val expected = Bitmap.createBitmap(actual.width, actual.height, Bitmap.Config.ARGB_8888)
      try {
        Canvas(expected).apply {
          drawColor(Color.BLACK)
          scale(actual.width / 64f, actual.height / 64f)
          for (path in expectedPaths(frame)) drawPath(
            path,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED },
          )
        }
        var sum = 0.0
        var peak = 0.0
        for (y in 0 until actual.height) for (x in 0 until actual.width) {
          val error =
            abs(Color.red(actual.getPixel(x, y)) - Color.red(expected.getPixel(x, y))) / 255.0
          sum += error
          peak = maxOf(peak, error)
        }
        val mean = sum / (actual.width * actual.height)
        if (mean > 0.0001 || peak > 0.25) errors.add("$frame: mean=$mean peak=$peak")
        for ((kind, bitmap) in listOf("rc" to actual, "reference" to expected)) {
          File(folder, "frame$frame-$kind.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
          }
        }
      } finally {
        actual.recycle()
        expected.recycle()
      }
    }
    assertWithMessage(errors.joinToString("\n")).that(errors).isEmpty()
  }

  @Test fun unionThenUnion() = verify(2, 2)

  @Test fun unionThenSubtract() = verify(2, 3)

  @Test fun unionThenIntersect() = verify(2, 4)

  @Test fun unionThenXor() = verify(2, 5)

  @Test fun subtractThenUnion() = verify(3, 2)

  @Test fun subtractThenSubtract() = verify(3, 3)

  @Test fun subtractThenIntersect() = verify(3, 4)

  @Test fun subtractThenXor() = verify(3, 5)

  @Test fun intersectThenUnion() = verify(4, 2)

  @Test fun intersectThenSubtract() = verify(4, 3)

  @Test fun intersectThenIntersect() = verify(4, 4)

  @Test fun intersectThenXor() = verify(4, 5)

  @Test fun xorThenUnion() = verify(5, 2)

  @Test fun xorThenSubtract() = verify(5, 3)

  @Test fun xorThenIntersect() = verify(5, 4)

  @Test fun xorThenXor() = verify(5, 5)

  @Test fun unionWithRightDifference() = verify(3, 2, true)

  @Test fun subtractRightDifference() = verify(3, 3, true)

  @Test fun intersectRightDifference() = verify(3, 4, true)

  @Test fun xorWithRightDifference() = verify(3, 5, true)

  @Test fun unionWithLiveRightDifference() = verify(3, 2, true, true)

  @Test fun subtractLiveRightDifference() = verify(3, 3, true, true)

  @Test fun intersectLiveRightDifference() = verify(3, 4, true, true)

  @Test fun xorWithLiveRightDifference() = verify(3, 5, true, true)

  @Test fun unionWithRightXor() = verify(5, 2, true)

  @Test fun subtractRightXor() = verify(5, 3, true)

  @Test fun intersectRightXor() = verify(5, 4, true)

  @Test fun xorWithRightXor() = verify(5, 5, true)

  @Test fun unionWithLiveRightXor() = verify(5, 2, true, true)

  @Test fun subtractLiveRightXor() = verify(5, 3, true, true)

  @Test fun intersectLiveRightXor() = verify(5, 4, true, true)

  @Test fun xorWithLiveRightXor() = verify(5, 5, true, true)

  @Test
  fun bothOperandsRetainLiveTrees() {
    val left = """{"ty":"gr","it":[$a,$b,${merge(3)}]}"""
    val right = """{"ty":"gr","it":[$a,$c,${merge(5)}]}"""
    checkFrames("both-live", "$left,$right,${merge(4)},$redFill") { frame ->
      val first = rect(10 + frame * 0.6f, 18f, 38 + frame * 0.6f, 46f)
      listOf(
        combine(
          combine(first, rect(24f, 12f, 48f, 44f), 3),
          combine(first, rect(13f, 29f, 51f, 47f), 5),
          4,
        )
      )
    }
  }

  private val tree
    get() = "$a,$b,${merge(3)},$c,${merge(5)}"

  private fun treePath(frame: Int) =
    combine(
      combine(rect(10 + frame * 0.6f, 18f, 38 + frame * 0.6f, 46f), rect(24f, 12f, 48f, 44f), 3),
      rect(13f, 29f, 51f, 47f),
      5,
    )

  @Test
  fun singleOperandConcatenationPreservesLiveTree() {
    checkFrames("passthrough", "$tree,${merge(1)},$redFill") { listOf(treePath(it)) }
  }

  @Test
  fun nestedGroupTransformsReachEveryLeaf() {
    val transform =
      """{"ty":"tr","a":${fixed("[0,0]")},"p":${animated("[2,3]","[8,9]")},"s":${fixed("[-80,90]")},"r":${fixed("0")},"o":${fixed("100")}}"""
    val outer =
      """{"ty":"tr","a":${fixed("[0,0]")},"p":${fixed("[64,0]")},"s":${fixed("[100,100]")},"r":${fixed("0")},"o":${fixed("100")}}"""
    val group = """{"ty":"gr","it":[{"ty":"gr","it":[$tree,$transform]},$outer]}"""
    checkFrames("transformed", "$group,$redFill") { frame ->
      listOf(
        treePath(frame).apply {
          transform(
            Matrix().apply {
              setValues(
                floatArrayOf(-0.8f, 0f, 66 + frame * 0.6f, 0f, 0.9f, 3 + frame * 0.6f, 0f, 0f, 1f)
              )
            }
          )
        }
      )
    }
  }

  private fun repeated(pathOnly: Boolean) {
    val copies = if (pathOnly) animated("[1]", "[3]") else fixed("2")
    val repeater =
      """{"ty":"rp","c":$copies,"o":${fixed("0")},"tr":{"a":${fixed("[0,0]")},"p":${fixed("[0,24]")},"s":${fixed("[50,50]")},"r":${fixed("0")},"so":${fixed("100")},"eo":${fixed("100")}}}"""
    val shapes = if (pathOnly) "$tree,$repeater,$redFill" else "$tree,$redFill,$repeater"
    checkFrames(if (pathOnly) "path-repeater" else "painted-repeater", shapes) { frame ->
      val count = if (pathOnly) floor(1 + frame / 5.0).toInt() else 2
      (count - 1 downTo 0).map { i ->
        treePath(frame).apply {
          transform(
            Matrix().apply {
              val scale = if (i == 0) 1f else if (i == 1) 0.5f else 0.25f
              setValues(floatArrayOf(scale, 0f, 0f, 0f, scale, 24f * i, 0f, 0f, 1f))
            }
          )
        }
      }
    }
  }

  @Test fun paintedRepeaterRetainsTree() = repeated(false)

  @Test fun livePathRepeaterMasksEveryLeaf() = repeated(true)

  private fun assertUnsupportedConcatenation(shapes: String) {
    val layer =
      Animation.decodeFromString(animation("$shapes,$redFill")).layers.single() as ShapeLayer
    val error =
      assertThrows(IllegalArgumentException::class.java) {
        gatherShapesForTest(
          layer.shapes,
          LottieSettings(RemoteFloat(androidx.compose.remote.creation.Rc.Time.ANIMATION_TIME)),
        )
      }
    assertThat(error).hasMessageThat().contains("Runtime Boolean contour concatenation")
  }

  @Test
  fun mergeDoesNotSilentlyUnionAResultWithContours() =
    assertUnsupportedConcatenation("$tree,$a,${merge(1)}")

  @Test
  fun threeOperandsDoNotSilentlyUnionTheRemainder() =
    assertUnsupportedConcatenation("$tree,$a,$b,${merge(2)}")
}
