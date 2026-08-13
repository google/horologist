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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.BezierValue
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.Layer
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.StaticBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticColorProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticScalarProperty
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Screenshot tests comparing scaling behavior between lottie-android and Remote Compose across 16
 * combinations of Lottie content sizes and Compose UI container box sizes:
 *
 * Lottie Content Sizes:
 * - 64x64px (Square, small)
 * - 64x96px (Tall / portrait, 2:3 aspect ratio)
 * - 128x128px (Square, medium)
 * - 192x128px (Wide / landscape, 3:2 aspect ratio)
 *
 * Containing Box Sizes:
 * - 64x64dp
 * - 64x96dp
 * - 128x128dp
 * - 192x128dp
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(qualifiers = "w600dp-h400dp-xhdpi")
class LottieScalingDiffScreenshotTest(
  private val lottieSize: Pair<Int, Int>,
  private val boxSize: Pair<Int, Int>,
) : LottieDiffScreenshotTest() {

  override fun screenshotFilePath(suffix: String): String {
    return "src/test/screenshots/LottieScalingDiffScreenshotTest_lottie${lottieSize.first}x${lottieSize.second}_box${boxSize.first}x${boxSize.second}$suffix.png"
  }

  @Test
  fun test() {
    runScalingTest(lottieSize.first, lottieSize.second, boxSize.first.dp, boxSize.second.dp)
  }

  private fun runScalingTest(lottieWidth: Int, lottieHeight: Int, boxWidth: Dp, boxHeight: Dp) {
    val json = createLottieJson(lottieWidth, lottieHeight)

    composeRule.setContent {
      Column(
        modifier = Modifier.background(Color(0xFF1E1E1E)).padding(8.dp).testTag("LottieDiff"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        BasicText(
          text =
            "Lottie: ${lottieWidth}x${lottieHeight}px | Box: ${boxWidth.value.toInt()}x${boxHeight.value.toInt()}dp",
          style = TextStyle(color = Color.White, fontSize = 10.sp),
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          LottieAndroidPreview(json = json, boxWidth = boxWidth, boxHeight = boxHeight)
          LottieRcPreview(json = json, boxWidth = boxWidth, boxHeight = boxHeight)
        }
      }
    }

    composeRule.onNodeWithTag("LottieDiff").captureRoboImage(screenshotFilePath(""))
  }

  companion object {
    val sizes = listOf(64 to 64, 64 to 96, 128 to 128, 192 to 128)

    @JvmStatic
    @ParameterizedRobolectricTestRunner.Parameters(name = "lottie_{0}_box_{1}")
    fun parameters(): List<Array<Any>> {
      return sizes.flatMap { lottie -> sizes.map { box -> arrayOf(lottie, box) } }
    }

    /**
     * Generates a Lottie animation object using kotlinx.serialization data classes. Contains an
     * outer bounding rectangle and a centered circle to clearly highlight scaling, aspect ratio
     * preservation, letterboxing/pillarboxing, and centering.
     */
    internal fun createLottieAnimation(width: Int, height: Int): Animation {
      val w = width.toFloat()
      val h = height.toFloat()
      val cx = w / 2f
      val cy = h / 2f
      val r = minOf(w, h) / 4f
      val handle = r * 0.55228475f

      val circleShape =
        GraphicElement.Group(
          name = "Center Circle",
          shapes =
            listOf(
              GraphicElement.Path(
                name = "Circle Path",
                shape =
                  StaticBezierProperty(
                    value =
                      BezierValue(
                        closed = true,
                        vertices =
                          listOf(listOf(0f, -r), listOf(r, 0f), listOf(0f, r), listOf(-r, 0f)),
                        inTangents =
                          listOf(
                            listOf(-handle, 0f),
                            listOf(0f, -handle),
                            listOf(handle, 0f),
                            listOf(0f, handle),
                          ),
                        outTangents =
                          listOf(
                            listOf(handle, 0f),
                            listOf(0f, handle),
                            listOf(-handle, 0f),
                            listOf(0f, -handle),
                          ),
                      )
                  ),
              ),
              GraphicElement.Fill(
                name = "Circle Fill",
                color = StaticColorProperty.fromColor(Color(0.95f, 0.25f, 0.2f, 1.0f)),
                opacity = StaticScalarProperty(value = 100f),
              ),
              GraphicElement.Transform(
                name = "Transform",
                positionTranslation = StaticPositionProperty(value = floatArrayOf(cx, cy)),
              ),
            ),
        )

      val rectShape =
        GraphicElement.Group(
          name = "Outer Rect",
          shapes =
            listOf(
              GraphicElement.Path(
                name = "Rect Path",
                shape =
                  StaticBezierProperty(
                    value =
                      BezierValue(
                        closed = true,
                        vertices =
                          listOf(
                            listOf(2f, 2f),
                            listOf(w - 2f, 2f),
                            listOf(w - 2f, h - 2f),
                            listOf(2f, h - 2f),
                          ),
                        inTangents =
                          listOf(listOf(0f, 0f), listOf(0f, 0f), listOf(0f, 0f), listOf(0f, 0f)),
                        outTangents =
                          listOf(listOf(0f, 0f), listOf(0f, 0f), listOf(0f, 0f), listOf(0f, 0f)),
                      )
                  ),
              ),
              GraphicElement.Fill(
                name = "Rect Fill",
                color = StaticColorProperty.fromColor(Color(0.2f, 0.5f, 0.9f, 1.0f)),
                opacity = StaticScalarProperty(value = 100f),
              ),
              GraphicElement.Transform(
                name = "Transform",
                positionTranslation = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
              ),
            ),
        )

      return Animation(
        name = "test_${width}x${height}",
        version = "5.9.6",
        frameRate = 60,
        startFrame = 0,
        endFrame = 60,
        width = width,
        height = height,
        layers =
          listOf(
            Layer.ShapeLayer(
              name = "Shape Layer",
              index = 1,
              startFrame = 0,
              endFrame = 60,
              transform = GraphicElement.Transform(),
              shapes = listOf(circleShape, rectShape),
            )
          ),
      )
    }

    /** Generates a Lottie animation JSON string for the given canvas dimensions. */
    fun createLottieJson(width: Int, height: Int): String {
      return LottieDecoder.json.encodeToString(
        Animation.serializer(),
        createLottieAnimation(width, height),
      )
    }
  }
}
