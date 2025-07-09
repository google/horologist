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

package com.google.android.horologist.ai.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.google.android.horologist.ai.ui.R
import com.google.android.horologist.ai.ui.components.PromptOrResponseDisplay
import com.google.android.horologist.ai.ui.components.ResponseInProgressCard
import com.google.android.horologist.ai.ui.components.TextPromptDisplay
import com.google.android.horologist.ai.ui.model.InProgressResponseUiModel
import com.google.android.horologist.ai.ui.model.PromptOrResponseUiModel
import com.google.android.horologist.ai.ui.model.PromptUiModel
import com.google.android.horologist.ai.ui.model.ResponseUiModel
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

/**
 * A screen to display metrics, e.g. workout metrics.
 * It can display up to four metrics, and it's recommended that at least two metrics should be
 * displayed.
 */
@Composable
public fun PromptScreen(
    uiState: PromptUiState,
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
    promptDisplay: @Composable (PromptOrResponseUiModel) -> Unit = {
        PromptOrResponseDisplay(
            promptResponse = it,
            onClick = {},
        )
    },
    promptEntry: @Composable () -> Unit,
) {
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )

    ScreenScaffold(scrollState = columnState, contentPadding = contentPadding) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            modifier = modifier,
            contentPadding = contentPadding,
        ) {
            item {
                ListHeader {
                    Text(
                        text = uiState.modelInfo?.name
                            ?: stringResource(R.string.horologist_unknown_model),
                    )
                }
            }
            uiState.messages.forEach {
                item {
                    val padding = when (it) {
                        is PromptUiModel -> PaddingValues(end = 20.dp)
                        is ResponseUiModel -> PaddingValues(start = 20.dp)
                        else -> PaddingValues()
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding),
                    ) {
                        promptDisplay(it)
                    }
                }
            }
            val inProgress = uiState.inProgress
            if (inProgress != null) {
                item {
                    TextPromptDisplay(
                        prompt = inProgress,
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 25.dp),
                    )
                }
                item {
                    ResponseInProgressCard(InProgressResponseUiModel)
                }
            }
            item {
                promptEntry()
            }
            if (onSettingsClick != null) {
                item {
                    IconButton(
                        onClick = onSettingsClick,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.horologist_settings_content_description),
                        )
                    }
                }
            }
        }
    }
}
