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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.ai.ui.model.FailedResponseUiModel
import com.google.android.horologist.ai.ui.model.InProgressResponseUiModel
import com.google.android.horologist.ai.ui.model.TextResponseUiModel

@Composable
public fun FailedResponseChip(
    answer: FailedResponseUiModel,
    modifier: Modifier = Modifier,
) {
    Text(
        text = answer.message,
        modifier = modifier,
        color = MaterialTheme.colors.error,
    )
}

@Composable
public fun TextResponseCard(
    textResponseUiModel: TextResponseUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            Color(0xffbdc1c6),
            Color(0xffbdc1c6),
        ),
    ) {
        Text(text = textResponseUiModel.text, color = MaterialTheme.colors.surface)
    }
}

@Composable
public fun ResponseInProgressCard(
    @Suppress("UNUSED_PARAMETER") inProgress: InProgressResponseUiModel,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        CircularProgressIndicator()
    }
}

@WearPreviewLargeRound
@Composable
internal fun TextResponseCardPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        PromptResponseDisplay(
            TextResponseUiModel(LoremIpsum(20).values.joinToString("\n")),
        )
    }
}

@WearPreviewLargeRound
@Composable
internal fun FailedResponseChipPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PromptResponseDisplay(
            FailedResponseUiModel("Error"),
        )
    }
}

@WearPreviewLargeRound
@Composable
internal fun ResponseInProgressCardPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PromptResponseDisplay(
            InProgressResponseUiModel,
        )
    }
}
