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

import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Ellipse
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Path
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Rectangle
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticColorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateColor
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.ellipse
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.path
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.rectangle
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EvaluatorModularizationTest {
  private val settings = LottieSettings(0f.rf, SlotMap.Empty)

  @Test
  fun animateScalar_evaluatesStaticScalar() {
    val scalar = StaticScalarProperty(value = 42f)
    val result = animateScalar(scalar, settings)
    assertThat(result.constantValue).isEqualTo(42f)
  }

  @Test
  fun animatePosition_evaluatesStaticPosition() {
    val position = StaticPositionProperty(value = floatArrayOf(10f, 20f))
    val result = animatePosition(position, settings)
    assertThat(result.x.constantValue).isEqualTo(10f)
    assertThat(result.y.constantValue).isEqualTo(20f)
  }

  @Test
  fun animateVector_evaluatesStaticVector() {
    val vector = StaticVectorProperty(value = floatArrayOf(30f, 40f))
    val result = animateVector(vector, settings)
    assertThat(result.map { it.constantValue }.toFloatArray()).isEqualTo(floatArrayOf(30f, 40f))
  }

  @Test
  fun animateColor_evaluatesStaticColor() {
    val color = StaticColorProperty(colorInt = 0xFF112233.toInt())
    val result = animateColor(color, settings)
    assertThat(result.constantValue).isEqualTo(Color(color.colorInt))
  }

  @Test
  fun animateBezier_evaluatesStaticBezier() {
    val bezier =
      StaticBezierProperty(
        value =
          BezierValue(
            closed = true,
            inTangents =
              listOf(
                com.google.android.horologist.remotecompose.lottie.format.values.Point(0f, 0f)
              ),
            outTangents =
              listOf(
                com.google.android.horologist.remotecompose.lottie.format.values.Point(0f, 0f)
              ),
            vertices =
              listOf(
                com.google.android.horologist.remotecompose.lottie.format.values.Point(10f, 20f)
              ),
          )
      )
    val result = animateBezier(bezier, settings)
    assertThat(result.closed).isTrue()
    assertThat(result.vertices[0].x.constantValueOrNull).isEqualTo(10f)
    assertThat(result.vertices[0].y.constantValueOrNull).isEqualTo(20f)
  }

  @Test
  fun shapes_evaluateToRemoteLottiePath() {
    val pathShape =
      Path(
        shape =
          StaticBezierProperty(
            value =
              BezierValue(
                closed = true,
                inTangents =
                  listOf(
                    com.google.android.horologist.remotecompose.lottie.format.values.Point(0f, 0f)
                  ),
                outTangents =
                  listOf(
                    com.google.android.horologist.remotecompose.lottie.format.values.Point(0f, 0f)
                  ),
                vertices =
                  listOf(
                    com.google.android.horologist.remotecompose.lottie.format.values.Point(0f, 0f)
                  ),
              )
          )
      )
    val rectShape = Rectangle()
    val ellipseShape = Ellipse()

    assertThat(path(pathShape, settings)).isNotNull()
    assertThat(rectangle(rectShape, settings)).isNotNull()
    assertThat(ellipse(ellipseShape, settings)).isNotNull()
  }
}
