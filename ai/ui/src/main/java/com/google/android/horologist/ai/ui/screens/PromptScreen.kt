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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.material3.touchTargetAwareSize
import com.google.android.horologist.ai.ui.R
import com.google.android.horologist.ai.ui.components.PromptOrResponseDisplay
import com.google.android.horologist.ai.ui.components.ResponseInProgressCard
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
    promptDisplay: @Composable (PromptOrResponseUiModel, Modifier, SurfaceTransformation) -> Unit = { model, modifier, transformation ->
        PromptOrResponseDisplay(
            promptResponse = model,
            onClick = {},
            modifier = modifier,
            transformation = transformation,
        )
    },
    promptEntry: @Composable (Boolean) -> Unit,
) {
    val transformationSpec: TransformationSpec = rememberTransformationSpec()
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )

    ScreenScaffold(
        scrollState = columnState,
        contentPadding = contentPadding,
        edgeButton = { promptEntry(uiState.pending) },
    ) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            modifier = modifier,
            contentPadding = contentPadding,
        ) {
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.modelInfo?.name
                                ?: stringResource(R.string.horologist_unknown_model),
                            modifier = Modifier.fillMaxWidth(0.6f),
                        )

                        if (onSettingsClick != null) {
                            IconButton(
                                onClick = onSettingsClick,
                                modifier = Modifier
                                    .touchTargetAwareSize(IconButtonDefaults.ExtraSmallButtonSize)
                                    .align(Alignment.CenterEnd),
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
            uiState.messages.forEach {
                item {
                    val padding = when (it) {
                        is PromptUiModel -> PaddingValues(end = 20.dp)
                        is ResponseUiModel -> PaddingValues(start = 20.dp)
                        else -> PaddingValues()
                    }
                    promptDisplay(
                        it,
                        Modifier
                            .fillMaxWidth()
                            .padding(padding)
                            .transformedHeight(this, transformationSpec),
                        SurfaceTransformation(transformationSpec),
                    )
                }
            }
            val pending = uiState.pending
            if (pending) {
                item {
                    ResponseInProgressCard(
                        InProgressResponseUiModel,
                        transformation = SurfaceTransformation(transformationSpec),
                        modifier = Modifier
                            .transformedHeight(this, transformationSpec),
                    )
                }
            }
        }
    }
}
