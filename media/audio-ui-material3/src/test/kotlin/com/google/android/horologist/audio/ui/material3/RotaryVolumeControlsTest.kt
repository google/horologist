/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.audio.ui.material3

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performRotaryScrollInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.ScreenScaffold
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class RotaryVolumeControlsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var view: View
    private var volumeState: VolumeState = VolumeState(5, 25)
    private val volumeRepository = FakeVolumeRepository(volumeState)

    @Test
    fun rotatePositivelyRotaryInVolumeRange_triggerKeyboardPressHapticFeedback() {
        volumeState = VolumeState(current = 2, max = MAX_VOLUME)
        setUpViewWithRotaryVolumeModifier(volumeState = volumeState, isLowRes = false)

        composeTestRule.onNodeWithTag(ROTARY_TEST_TAG).performRotaryScrollInput {
            rotateToScrollVertically(50.0f)
        }

        assertThat(shadowOf(view).lastHapticFeedbackPerformed())
            .isEqualTo(HapticFeedbackConstants.KEYBOARD_TAP)
        assertThat(volumeRepository.volumeState.value.current).isEqualTo(3)
    }

    @Test
    fun rotateNegativelyRotaryAtMinVolume_doesNotTriggerHapticFeedback() {
        volumeState = VolumeState(current = 0, max = MAX_VOLUME)
        setUpViewWithRotaryVolumeModifier(volumeState = volumeState, isLowRes = false)
        composeTestRule.onNodeWithTag(ROTARY_TEST_TAG).performRotaryScrollInput {
            rotateToScrollVertically(-50.0f)
        }

        assertThat(shadowOf(view).lastHapticFeedbackPerformed()).isEqualTo(-1)
        assertThat(volumeRepository.volumeState.value.current).isEqualTo(0)
    }

    @Test
    fun rotatePositivelyRotaryAtMaxVolume_doesNotTriggerHapticFeedback() {
        volumeState = VolumeState(current = MAX_VOLUME, max = MAX_VOLUME)
        setUpViewWithRotaryVolumeModifier(volumeState, isLowRes = false)
        composeTestRule.onNodeWithTag(ROTARY_TEST_TAG).performRotaryScrollInput {
            rotateToScrollVertically(50.0f)
        }

        assertThat(shadowOf(view).lastHapticFeedbackPerformed()).isEqualTo(-1)
        assertThat(volumeRepository.volumeState.value.current).isEqualTo(MAX_VOLUME)
    }

    @Test
    fun rotatePositivelyRotaryTowardsMaxVolume_triggerLongPressHapticFeedback() {
        volumeState = VolumeState(current = MAX_VOLUME - 1, max = MAX_VOLUME)
        setUpViewWithRotaryVolumeModifier(volumeState, isLowRes = false)
        composeTestRule.onNodeWithTag(ROTARY_TEST_TAG).performRotaryScrollInput {
            rotateToScrollVertically(50.0f)
        }

        assertThat(shadowOf(view).lastHapticFeedbackPerformed())
            .isEqualTo(HapticFeedbackConstants.LONG_PRESS)
        assertThat(volumeRepository.volumeState.value.current).isEqualTo(MAX_VOLUME)
    }

    @Test
    fun rotateNegativelyRotaryTowardsMinVolume_triggerLongPressHapticFeedback() {
        volumeState = VolumeState(current = 1, max = MAX_VOLUME)
        setUpViewWithRotaryVolumeModifier(volumeState, isLowRes = false)
        composeTestRule.onNodeWithTag(ROTARY_TEST_TAG).performRotaryScrollInput {
            rotateToScrollVertically(-50.0f)
        }

        assertThat(shadowOf(view).lastHapticFeedbackPerformed())
            .isEqualTo(HapticFeedbackConstants.LONG_PRESS)
        assertThat(volumeRepository.volumeState.value.current).isEqualTo(0)
    }

    @Test
    fun lowResRotary_converts2fChangesToVolume_coercedToMax() {
        volumeState = VolumeState(current = 24, max = MAX_VOLUME)
        setUpViewWithRotaryVolumeModifier(volumeState, isLowRes = true)
        composeTestRule.onNodeWithTag(ROTARY_TEST_TAG).performRotaryScrollInput {
            rotateToScrollVertically(2f)
        }

        assertThat(shadowOf(view).lastHapticFeedbackPerformed())
            .isEqualTo(HapticFeedbackConstants.LONG_PRESS)
        assertThat(volumeRepository.volumeState.value.current).isEqualTo(MAX_VOLUME)
    }

    @Test
    fun highResRotary_with15Max_converts30Pixels_getsOneVolumeIncrease() {
        val actual =
            convertPixelToVolume(
                change = 30f,
                volumeUiStateProvider = { VolumeUiState(current = 2, max = 15) },
            )

        assertThat(actual).isEqualTo(3)
    }

    @Test
    fun highResRotary_with15Max_convertsNegative30Pixels_getsOneVolumeDecrease() {
        val actual =
            convertPixelToVolume(
                change = -30f,
                volumeUiStateProvider = { VolumeUiState(current = 2, max = 15) },
            )

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun highResRotary_increasesBeyondMax_getsMax() {
        val actual =
            convertPixelToVolume(
                change = 100f,
                volumeUiStateProvider = { VolumeUiState(current = 25, max = 25) },
            )

        assertThat(actual).isEqualTo(25)
    }

    @Test
    fun highResRotary_decreasesBeyondMin_getsMin() {
        val actual =
            convertPixelToVolume(
                change = -100f,
                volumeUiStateProvider = { VolumeUiState(current = 0, max = 25, min = 0) },
            )

        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun highResRotary_converts48Pixels_withSmallMax_getsNewVolumeCorrectly() {
        val actual =
            convertPixelToVolume(
                change = 48f,
                volumeUiStateProvider = { VolumeUiState(current = 0, max = 5, min = 0) },
            )

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun highResRotary_converts23Pixels_withSmallMax_getsNoChange() {
        // 23/48 = 0.47916 which would round to 0
        val actual =
            convertPixelToVolume(
                change = 23f,
                volumeUiStateProvider = { VolumeUiState(current = 0, max = 5, min = 0) },
            )

        assertThat(actual).isEqualTo(0)
    }

    private fun setUpViewWithRotaryVolumeModifier(
        volumeState: VolumeState,
        isLowRes: Boolean,
    ) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val packageManager = context.packageManager

        shadowOf(packageManager).setSystemFeature("android.hardware.rotaryencoder.lowres", isLowRes)

        this.volumeState = volumeState
        composeTestRule.setContent {
            val focusRequester = rememberActiveFocusRequester()
            view = LocalView.current

            ScreenScaffold(
                modifier =
                    Modifier
                        .rotaryScrollable(
                            behavior = volumeRotaryBehavior(
                                volumeUiStateProvider = { VolumeUiStateMapper.map(volumeState) },
                                onRotaryVolumeInput = { newVolume ->
                                    volumeRepository.setVolume(
                                        newVolume,
                                    )
                                },
                            ),
                            focusRequester = focusRequester,
                        )
                        .testTag(ROTARY_TEST_TAG),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {}
            }
        }
    }

    companion object {
        private const val ROTARY_TEST_TAG = "TestScreenForVolumeRotary"
        private const val MAX_VOLUME = 25
    }
}
