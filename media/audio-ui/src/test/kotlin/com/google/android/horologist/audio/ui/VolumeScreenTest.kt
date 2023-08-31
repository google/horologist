/*
 * Copyright 2023 The Android Open Source Project
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.audio.VolumeState
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@MediumTest
@RunWith(RobolectricTestRunner::class)
class VolumeScreenTest {
    private lateinit var vibrator: Vibrator
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var volumeState: VolumeState = VolumeState(current = 5, max = 25)
    private val volumeRepository = FakeVolumeRepository(volumeState)
    private val audioOutputRepository = FakeAudioOutputRepository()
    private lateinit var model: VolumeViewModel

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        volumeState = VolumeState(current = 5, max = 25)
        vibrator =
            context.applicationContext.getSystemService(Vibrator::class.java)
        model = VolumeViewModel(volumeRepository, audioOutputRepository, onCleared = {
            volumeRepository.close()
            audioOutputRepository.close()
        }, vibrator)

        composeTestRule.setContent {
            val focusRequester = remember { FocusRequester() }
            VolumeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                model,
            )
        }
    }

    @Test
    fun clickVolumeUp_increaseVolume() {
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.horologist_volume_screen_volume_up_content_description),
        ).performClick()

        assertThat(volumeRepository.volumeState.value.current).isEqualTo(6)
    }

    @Test
    fun clickVolumeDown_decreasesVolume() {
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.horologist_volume_screen_volume_down_content_description),
        ).performClick()

        assertThat(volumeRepository.volumeState.value.current).isEqualTo(4)
    }
}
