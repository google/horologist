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

package com.google.android.horologist.ai.sample.wear.prompt.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListSubHeader
import androidx.wear.compose.material3.RadioButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.material3.placeholder
import androidx.wear.compose.material3.placeholderShimmer
import androidx.wear.compose.material3.rememberPlaceholderState
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.ai.sample.wear.geminilib.BuildConfig
import com.google.android.horologist.ai.ui.model.ModelInstanceUiModel
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        modifier = modifier,
        selectModel = viewModel::selectModel,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    selectModel: (ModelInstanceUiModel) -> Unit,
) {
    val transformationSpec = rememberTransformationSpec()
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )

    val placeholderState = rememberPlaceholderState(uiState.models == null)

    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier,
        contentPadding = contentPadding,
    ) { contentPadding ->
        TransformingLazyColumn(state = columnState, contentPadding = contentPadding) {
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Text("Browse")
                }
            }
            if (uiState.models == null) {
                items(3) {
                    RadioButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec)
                            .placeholderShimmer(placeholderState),
                        selected = false,
                        onSelect = {},
                        label = {
                            Text(
                                "      ",
                                modifier = Modifier.placeholder(placeholderState),
                            )
                        },
                        transformation = SurfaceTransformation(transformationSpec),
                    )
                }
            } else {
                items(uiState.models) { model ->
                    key(model.id) {
                        RadioButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            selected = model == uiState.current,
                            onSelect = { selectModel(model) },
                            label = { Text(model.name) },
                            secondaryLabel = model.service?.let { { Text(it) } },
                            transformation = SurfaceTransformation(transformationSpec),
                        )
                    }
                }
            }
            item {
                ListSubHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Text("Gemini Key")
                }
            }
            item {
                Text(BuildConfig.GEMINI_API_KEY)
            }
        }
    }
}

@WearPreviewLargeRound
@Composable
fun SettingsScreenPreview() {
    val current = ModelInstanceUiModel("dummy", "Dummy Model")
    val other1 = ModelInstanceUiModel("1", "Dummy Model 1")
    val other2 = ModelInstanceUiModel("2", "Dummy Model 2")

    val uiState = SettingsUiState(current, listOf(current, other1, other2))

    SettingsScreen(
        uiState = uiState,
        selectModel = {},
    )
}
