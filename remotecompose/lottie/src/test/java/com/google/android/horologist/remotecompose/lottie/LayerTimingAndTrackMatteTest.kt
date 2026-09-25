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

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.ri
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.layer.MatteMode
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.SolidColorLayer
import com.google.android.horologist.remotecompose.lottie.renderer.layers.MatteContext
import com.google.android.horologist.remotecompose.lottie.renderer.layers.calculateEffectiveEndFrame
import com.google.android.horologist.remotecompose.lottie.renderer.layers.calculateLayerVisibility
import com.google.android.horologist.remotecompose.lottie.renderer.layers.calculateLocalFrame
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("RestrictedApi")
@RunWith(AndroidJUnit4::class)
class LayerTimingAndTrackMatteTest {

  @Test
  fun calculateLocalFrame_returnsIdenticalFrameWhenDefaultTimingProvided() {
    val currentFrame = 25f.rf
    val localFrame = calculateLocalFrame(currentFrame, startTime = null, timeStretch = null)
    assertThat(localFrame.constantValueOrNull).isWithin(0.001f).of(25f)
  }

  @Test
  fun calculateLocalFrame_shiftsFrameWhenStartTimeOffsetConfigured() {
    val currentFrame = 30f.rf
    val localFrame = calculateLocalFrame(currentFrame, startTime = 10f, timeStretch = null)
    // t_local = 30 - 10 = 20
    assertThat(localFrame.constantValueOrNull).isWithin(0.001f).of(20f)
  }

  @Test
  fun calculateLocalFrame_scalesFrameRateWhenTimeStretchConfigured() {
    val currentFrame = 40f.rf
    val localFrame = calculateLocalFrame(currentFrame, startTime = 0f, timeStretch = 2f)
    // t_local = (40 - 0) / 2 = 20
    assertThat(localFrame.constantValueOrNull).isWithin(0.001f).of(20f)
  }

  @Test
  fun calculateLocalFrame_evaluatesCombinedOffsetAndStretchWhenBothProvided() {
    val currentFrame = 50f.rf
    val localFrame = calculateLocalFrame(currentFrame, startTime = 10f, timeStretch = 2f)
    // t_local = (50 - 10) / 2 = 20
    assertThat(localFrame.constantValueOrNull).isWithin(0.001f).of(20f)
  }

  @Test
  fun calculateLocalFrame_supportsReversedPlaybackWhenNegativeTimeStretchConfigured() {
    val currentFrame = 10f.rf
    val localFrame = calculateLocalFrame(currentFrame, startTime = 50f, timeStretch = -1f)
    // t_local = (10 - 50) / -1 = 40
    assertThat(localFrame.constantValueOrNull).isWithin(0.001f).of(40f)
  }

  @Test
  fun calculateLocalFrame_guardsAgainstDivisionByZeroWhenZeroTimeStretchProvided() {
    val currentFrame = 25f.rf
    val localFrame = calculateLocalFrame(currentFrame, startTime = 0f, timeStretch = 0f)
    // Falls back to safeSr = 1f
    assertThat(localFrame.constantValueOrNull).isWithin(0.001f).of(25f)
  }

  @Test
  fun calculateEffectiveEndFrame_extendsPaddingWhenEndFrameReachesOrExceedsCompositionEnd() {
    val paddedEnd = calculateEffectiveEndFrame(endFrame = 60f, compositionEndFrame = 60f)
    assertThat(paddedEnd).isWithin(0.001f).of(60.01f)

    val unpaddedEnd = calculateEffectiveEndFrame(endFrame = 30f, compositionEndFrame = 60f)
    assertThat(unpaddedEnd).isWithin(0.001f).of(30.0f)
  }

  @Test
  fun calculateLayerVisibility_returnsActiveVisibilityWhenCurrentFrameEqualsExactStartFrame() {
    val startFrame = 10f
    val endFrame = 50f
    val currentFrame = 10f.rf // t = ip
    val visibility = calculateLayerVisibility(currentFrame, startFrame, endFrame)
    assertThat(visibility.constantValueOrNull).isWithin(0.001f).of(1.0f)
  }

  @Test
  fun calculateLayerVisibility_returnsInactiveVisibilityWhenCurrentFrameEqualsExclusiveEndFrame() {
    val startFrame = 10f
    val endFrame = 50f
    val currentFrame = 50f.rf // t = op (exclusive)
    val visibility = calculateLayerVisibility(currentFrame, startFrame, endFrame)
    assertThat(visibility.constantValueOrNull).isWithin(0.001f).of(0.0f)
  }

  @Test
  fun calculateLayerVisibility_returnsActiveVisibilityAtCompositionEndBoundaryWhenPadded() {
    val startFrame = 0f
    val endFrame = 60f
    val compositionEndFrame = 60f
    val effectiveEnd = calculateEffectiveEndFrame(endFrame, compositionEndFrame)
    val currentFrame = 60f.rf // t = compEnd
    val visibility = calculateLayerVisibility(currentFrame, startFrame, effectiveEnd)
    assertThat(visibility.constantValueOrNull).isWithin(0.001f).of(1.0f)
  }

  @Test
  fun calculateLayerVisibility_returnsInactiveVisibilityWhenOutsideIntervalBounds() {
    val startFrame = 10f
    val endFrame = 50f

    val beforeStart = calculateLayerVisibility(5f.rf, startFrame, endFrame)
    assertThat(beforeStart.constantValueOrNull).isWithin(0.001f).of(0.0f)

    val afterEnd = calculateLayerVisibility(55f.rf, startFrame, endFrame)
    assertThat(afterEnd.constantValueOrNull).isWithin(0.001f).of(0.0f)
  }

  @Test
  fun matteContext_preservesConfiguredMatteMode() {
    val matteLayer =
      ShapeLayer(index = 1, startFrame = 0f.rf, endFrame = 60f.rf, shapes = emptyList())
    val alphaContext = MatteContext(matteLayer, emptyList(), MatteMode.Alpha)
    assertThat(alphaContext.matteMode).isEqualTo(MatteMode.Alpha)

    val invertedContext = MatteContext(matteLayer, emptyList(), MatteMode.InvertedAlpha)
    assertThat(invertedContext.matteMode).isEqualTo(MatteMode.InvertedAlpha)
  }

  @Test
  fun matteContext_preservesHiddenMatteLayerAndMode() {
    val hiddenMatteLayer =
      ShapeLayer(
        index = 5,
        hidden = true.rb,
        startFrame = 0f.rf,
        endFrame = 60f.rf,
        shapes = emptyList(),
      )
    val context = MatteContext(hiddenMatteLayer, emptyList(), MatteMode.InvertedAlpha)
    assertThat(context.matteLayer.hidden.constantValue).isTrue()
    assertThat(context.matteMode).isEqualTo(MatteMode.InvertedAlpha)
  }

  @Test
  fun matteContext_preservesSolidColorMatteLayerDimensions() {
    val solidMatteLayer =
      SolidColorLayer(
        index = 2,
        startFrame = 0f.rf,
        endFrame = 60f.rf,
        solidWidth = 100.ri,
        solidHeight = 200.ri,
        solidColor = Color.White.rc,
      )
    val context = MatteContext(solidMatteLayer, emptyList(), MatteMode.Alpha)
    assertThat(context.matteLayer).isEqualTo(solidMatteLayer)
  }
}
