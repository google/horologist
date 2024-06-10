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

@file:OptIn(ExperimentalWearMaterialApi::class)

package com.google.android.horologist.ai.sample.wear.prompt.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.ai.ui.model.ModelInstanceUiModel
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberActivePlaceholderState
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl

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
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    )

    val placeholderState = rememberActivePlaceholderState { uiState.models != null }

    ScreenScaffold(scrollState = columnState, modifier = modifier) {
        ScalingLazyColumn(columnState = columnState) {
            if (uiState.models == null) {
                items(3) {
                    PlaceholderChip(
                        placeholderState = placeholderState,
                        icon = false,
                        secondaryLabel = false,
                    )
                }
            } else {
                item {
                    ResponsiveListHeader(modifier = Modifier.listTextPadding()) {
                        Text("Browse")
                    }
                }
                items(uiState.models) { model ->
                    key(model.id) {
                        ToggleChip(
                            checked = model == uiState.current,
                            onCheckedChanged = { selectModel(model) },
                            label = model.name,
                            toggleControl = ToggleChipToggleControl.Radio,
                        )
                    }
                }
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
