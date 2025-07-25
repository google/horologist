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

package com.google.android.horologist.ai.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.SurfaceTransformation
import com.google.android.horologist.ai.ui.model.FailedResponseUiModel
import com.google.android.horologist.ai.ui.model.ImageResponseUiModel
import com.google.android.horologist.ai.ui.model.InProgressResponseUiModel
import com.google.android.horologist.ai.ui.model.PromptOrResponseUiModel
import com.google.android.horologist.ai.ui.model.TextPromptUiModel
import com.google.android.horologist.ai.ui.model.TextResponseUiModel

/**
 * A component to display an Answer.
 */
@Composable
public fun PromptOrResponseDisplay(
    promptResponse: PromptOrResponseUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    transformation: SurfaceTransformation? = null,
) {
    when (promptResponse) {
        is TextResponseUiModel -> {
            TextResponseCard(promptResponse, onClick = onClick, modifier = modifier, transformation = transformation)
        }

        is ImageResponseUiModel -> {
            ImageResponseCard(promptResponse, onClick = onClick, modifier = modifier, transformation = transformation)
        }

        is FailedResponseUiModel -> {
            FailedResponseChip(promptResponse, onClick = onClick, modifier = modifier, transformation = transformation)
        }

        is InProgressResponseUiModel -> {
            ResponseInProgressCard(promptResponse, onClick = onClick, modifier = modifier, transformation = transformation)
        }

        is TextPromptUiModel -> {
            TextPromptDisplay(prompt = promptResponse, onClick = onClick, modifier = modifier, transformation = transformation)
        }
    }
}
