/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.ai.composables.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import com.google.android.horologist.ai.ui.model.ModelInstanceUiModel
import com.google.android.horologist.ai.ui.model.TextPromptUiModel
import com.google.android.horologist.ai.ui.model.TextResponseUiModel
import com.google.android.horologist.ai.ui.screens.PromptScreen
import com.google.android.horologist.ai.ui.screens.PromptUiState
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class PromptScreenTest : WearLegacyScreenTest() {
    @Test
    fun empty() {
        runTest {
            PromptScreen(
                uiState = PromptUiState(
                    modelInfo = ModelInstanceUiModel("simple", "Simple Model"),
                    messages = listOf(
                        TextPromptUiModel("Why did the chicken cross the road?"),
                        TextResponseUiModel("To get to the other side!"),
                    ),
                ),
                promptEntry = {
                    EdgeButton(
                        onClick = { /*TODO*/ },
                        buttonSize = EdgeButtonSize.ExtraSmall,
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardVoice,
                            contentDescription = "Voice Prompt",
                        )
                    }
                },
            )
        }
    }
}
