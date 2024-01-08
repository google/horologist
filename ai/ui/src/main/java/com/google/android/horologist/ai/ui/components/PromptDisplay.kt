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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.ai.ui.model.TextPromptUiModel

/**
 * A component to display a Prompt.
 */
@Composable
public fun TextPromptDisplay(
    prompt: TextPromptUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            MaterialTheme.colors.primaryVariant,
            MaterialTheme.colors.primaryVariant,
        ),
    ) {
        Text(text = prompt.prompt, color = MaterialTheme.colors.surface)
    }
}

@WearPreviewLargeRound
@Composable
internal fun TextPromptDisplayPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TextPromptDisplay(
            TextPromptUiModel("why did the chicken cross the road?"),
        )
    }
}
