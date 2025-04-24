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

import android.os.Vibrator
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeRepository
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.model.R
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.audio.ui.material3.components.toAudioOutputUi
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
    private lateinit var volumeState: VolumeState
    private lateinit var audioOutput: AudioOutput
    private lateinit var volumeRepository: VolumeRepository
    private val audioOutputRepository = FakeAudioOutputRepository()
    private lateinit var model: VolumeViewModel

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        volumeState = VolumeState(current = 5, max = 10)
        audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")
        volumeRepository = FakeVolumeRepository(volumeState)
        vibrator =
            context.applicationContext.getSystemService(Vibrator::class.java)
        model = VolumeViewModel(volumeRepository, audioOutputRepository, onCleared = {
            volumeRepository.close()
            audioOutputRepository.close()
        }, vibrator)
    }

    @Test
    fun clickVolumeUp_increaseVolume() {
        composeTestRule.setContent {
            val focusRequester = remember { FocusRequester() }
            VolumeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                volumeViewModel = model,
            )
        }

        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.horologist_volume_screen_volume_up_content_description),
        ).performClick()

        assertThat(volumeRepository.volumeState.value.current).isEqualTo(6)
    }

    @Test
    fun clickVolumeDown_decreasesVolume() {
        composeTestRule.setContent {
            val focusRequester = remember { FocusRequester() }
            VolumeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                volumeViewModel = model,
            )
        }

        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.horologist_volume_screen_volume_down_content_description),
        ).performClick()

        assertThat(volumeRepository.volumeState.value.current).isEqualTo(4)
    }

    @Test
    fun testLabelOrdering() {
        composeTestRule.setContent {
            TestVolumeScreen(
                volumeState = volumeState,
                audioOutput = audioOutput,
            )
        }

        composeTestRule.onNodeWithContentDescription("Volume set to 50%")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Increase Volume")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithText("Pixelbuds")
            .assertIsDisplayed()
            .assertHasClickAction()
            .assertHasStateDescription("Connected")
            .assertHasClickLabel("Change Audio Output")

        composeTestRule.onNodeWithContentDescription("Decrease Volume")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    companion object {
        fun SemanticsNodeInteraction.assertHasStateDescription(value: String): SemanticsNodeInteraction =
            assert(hasStateDescription(value))

        fun SemanticsNodeInteraction.assertHasClickLabel(expectedValue: String): SemanticsNodeInteraction =
            assert(
                SemanticsMatcher("${SemanticsActions.OnClick.name} = '$expectedValue'") {
                    it.config.getOrNull(SemanticsActions.OnClick)?.label == expectedValue
                },
            )

        @Composable
        fun TestVolumeScreen(
            volumeState: VolumeState,
            audioOutput: AudioOutput,
        ) {
            val volumeUiState = VolumeUiStateMapper.map(volumeState = volumeState)
            VolumeScreen(
                volume = { volumeUiState },
                audioOutputUi = audioOutput.toAudioOutputUi(),
                increaseVolume = {},
                decreaseVolume = {},
                onAudioOutputClick = {},
            )
        }
    }
}
