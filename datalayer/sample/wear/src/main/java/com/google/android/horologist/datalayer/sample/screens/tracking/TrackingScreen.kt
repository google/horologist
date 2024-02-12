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

package com.google.android.horologist.datalayer.sample.screens.tracking

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.SplitToggleChip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.google.android.horologist.datalayer.sample.R

@Composable
fun TrackingScreen(
    onDisplayInfoClicked: (info: String) -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: TrackingScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == TrackingScreenUiState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    TrackingScreen(
        state = state,
        onActivityLaunchedOnceCheckedChanged = viewModel::onActivityLaunchedOnceCheckedChanged,
        onSetupCompletedCheckedChanged = viewModel::onSetupCompletedCheckedChanged,
        onTileCheckedChanged = viewModel::onTileCheckedChanged,
        onComplicationCheckedChanged = viewModel::onComplicationCheckedChanged,
        onDisplayInfoClicked = onDisplayInfoClicked,
        columnState = columnState,
        modifier = modifier,
    )
}

@Composable
fun TrackingScreen(
    state: TrackingScreenUiState,
    onActivityLaunchedOnceCheckedChanged: (Boolean) -> Unit,
    onSetupCompletedCheckedChanged: (Boolean) -> Unit,
    onTileCheckedChanged: (tile: String, Boolean) -> Unit,
    onComplicationCheckedChanged: (complication: String, Boolean) -> Unit,
    onDisplayInfoClicked: (info: String) -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier,
    ) {
        item {
            Title(text = stringResource(id = R.string.apphelper_tracking_title))
        }

        item {
            Text(
                text = stringResource(id = R.string.apphelper_tracking_message),
                modifier = Modifier.padding(vertical = 10.dp),
            )
        }

        when (state) {
            TrackingScreenUiState.Idle,
            TrackingScreenUiState.Loading,
            -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is TrackingScreenUiState.Loaded -> {
                item {
                    val info =
                        stringResource(id = R.string.apphelper_tracking_activity_launched_info)
                    SplitToggleChip(
                        checked = state.activityLaunchedOnce,
                        onCheckedChanged = onActivityLaunchedOnceCheckedChanged,
                        label = stringResource(id = R.string.apphelper_tracking_activity_launched_chip_label),
                        onClick = { onDisplayInfoClicked(info) },
                        toggleControl = ToggleChipToggleControl.Switch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item {
                    val info = stringResource(id = R.string.apphelper_tracking_setup_completed_info)
                    SplitToggleChip(
                        checked = state.setupCompleted,
                        onCheckedChanged = onSetupCompletedCheckedChanged,
                        label = stringResource(id = R.string.apphelper_tracking_setup_completed_chip_label),
                        onClick = { onDisplayInfoClicked(info) },
                        toggleControl = ToggleChipToggleControl.Switch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item {
                    Title(
                        text = stringResource(id = R.string.apphelper_tracking_tile_header),
                        modifier = Modifier.padding(vertical = 10.dp),
                    )
                }

                for (tileEntry in state.tilesInstalled) {
                    item {
                        val info = stringResource(
                            id = R.string.apphelper_tracking_tile_installation_info,
                            tileEntry.key,
                        )
                        SplitToggleChip(
                            checked = tileEntry.value,
                            onCheckedChanged = { onTileCheckedChanged(tileEntry.key, it) },
                            label = tileEntry.key,
                            onClick = { onDisplayInfoClicked(info) },
                            toggleControl = ToggleChipToggleControl.Switch,
                            modifier = Modifier.fillMaxWidth(),
                            secondaryLabel = stringResource(id = R.string.apphelper_tracking_tile_installation_chip_label),
                        )
                    }
                }

                item {
                    Title(
                        text = stringResource(id = R.string.apphelper_tracking_complication_header),
                        modifier = Modifier.padding(vertical = 10.dp),
                    )
                }

                for (complicationEntry in state.complicationsInstalled) {
                    item {
                        val info = stringResource(
                            id = R.string.apphelper_tracking_complication_installation_info,
                            complicationEntry.key,
                        )
                        SplitToggleChip(
                            checked = complicationEntry.value,
                            onCheckedChanged = {
                                onComplicationCheckedChanged(
                                    complicationEntry.key,
                                    it,
                                )
                            },
                            label = complicationEntry.key,
                            onClick = { onDisplayInfoClicked(info) },
                            toggleControl = ToggleChipToggleControl.Switch,
                            modifier = Modifier.fillMaxWidth(),
                            secondaryLabel = stringResource(id = R.string.apphelper_tracking_complication_installation_chip_label),
                        )
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun TrackingScreenPreview() {
    TrackingScreen(
        state = TrackingScreenUiState.Loaded(
            activityLaunchedOnce = true,
            setupCompleted = false,
            tilesInstalled = mapOf("Tile 1" to true, "Tile 2" to false),
            complicationsInstalled = mapOf("Comp 1" to true, "Comp 2" to true),
        ),
        onActivityLaunchedOnceCheckedChanged = { },
        onSetupCompletedCheckedChanged = { },
        onTileCheckedChanged = { _, _ -> },
        onComplicationCheckedChanged = { _, _ -> },
        onDisplayInfoClicked = { },
        columnState = belowTimeTextPreview(),
    )
}
