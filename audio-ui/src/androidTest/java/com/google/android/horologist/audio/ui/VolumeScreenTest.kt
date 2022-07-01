/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.audio.ui

import android.os.Vibrator
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performRotaryScrollInput
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.audio.VolumeState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalHorologistAudioApi::class, ExperimentalHorologistAudioUiApi::class, ExperimentalTestApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@MediumTest
class VolumeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHaptics() = runTest {

        val rotaryPixelsForVolume = 136
        val volumeRepository = FakeVolumeRepository(VolumeState(50, 100))
        val audioOutputRepository = FakeAudioOutputRepository()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val vibrator =
            context.applicationContext.getSystemService(Vibrator::class.java)

        val model = VolumeViewModel(volumeRepository, audioOutputRepository, onCleared = {
            volumeRepository.close()
            audioOutputRepository.close()
        }, vibrator)
        val focusRequester = FocusRequester()

        composeTestRule.setContent {
            CompositionLocalProvider() {
                VolumeScreen(
                    modifier = Modifier
                        .fillMaxSize().focusRequester(focusRequester),
                    model
                )
            }
        }

        composeTestRule.runOnIdle {
            focusRequester.requestFocus()
        }

        composeTestRule.onRoot().performRotaryScrollInput {
            rotateToScrollVertically(1.25f * rotaryPixelsForVolume)
        }
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().performRotaryScrollInput {
            rotateToScrollVertically(1.25f * rotaryPixelsForVolume)
        }
        composeTestRule.waitForIdle()

        assertThat(volumeRepository.volumeState.value.current).isEqualTo(52)
    }
}
