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

package com.google.android.horologist.mediasample.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.domain.model.Settings
import com.google.android.horologist.mediasample.ui.navigation.navigateToAudioDebug
import com.google.android.horologist.mediasample.ui.navigation.navigateToSamples

@Composable
fun UampSettingsScreen(
    focusRequester: FocusRequester,
    state: ScalingLazyListState,
    settingsScreenViewModel: SettingsScreenViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val uiState by rememberStateWithLifecycle(settingsScreenViewModel.uiState)

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, state),
        state = state
    ) {
        item {
            Text(
                text = stringResource(id = R.string.horologist_sample_settings),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.title3,
            )
        }
        item {
            ActionSetting(
                stringResource(id = R.string.horologist_sample_audio_debug),
            ) {
                navController.navigateToAudioDebug()
            }
        }
        item {
            ActionSetting(
                stringResource(id = R.string.horologist_sample_samples),
            ) {
                navController.navigateToSamples()
            }
        }
        item {
            CheckedSetting(
                uiState.showTimeTextInfo,
                stringResource(id = R.string.horologist_show_time_text_info),
                enabled = uiState.writable
            ) {
                settingsScreenViewModel.setShowTimeTextInfo(it)
            }
        }
        item {
            CheckedSetting(
                uiState.debugOffload,
                stringResource(id = R.string.horologist_debug_offload),
                enabled = uiState.writable
            ) {
                settingsScreenViewModel.setDebugOffload(it)
            }
        }
        item {
            ActionSetting(
                stringResource(id = R.string.horologist_offload_mode, uiState.offloadMode.name),
                enabled = uiState.writable
            ) {
                val newMode = when (uiState.offloadMode) {
                    Settings.OffloadMode.Background -> Settings.OffloadMode.Never
                    Settings.OffloadMode.Never -> Settings.OffloadMode.Always
                    Settings.OffloadMode.Always -> Settings.OffloadMode.Background
                }
                settingsScreenViewModel.setOffloadMode(newMode)
            }
        }
        item {
            CheckedSetting(
                uiState.podcastControls,
                stringResource(id = R.string.horologist_podcast_controls),
                enabled = uiState.writable
            ) {
                settingsScreenViewModel.setPodcastControls(it)
            }
        }
        item {
            CheckedSetting(
                uiState.loadItemsAtStartup,
                stringResource(id = R.string.horologist_load_items),
                enabled = uiState.writable
            ) {
                settingsScreenViewModel.setLoadItemsAtStartup(it)
            }
        }
        item {
            CheckedSetting(
                uiState.animated,
                stringResource(id = R.string.horologist_animated),
                enabled = uiState.writable
            ) {
                settingsScreenViewModel.setAnimated(it)
            }
        }
        item {
            val message = stringResource(id = R.string.horologist_sample_error)
            ActionSetting(
                stringResource(id = R.string.horologist_show_test_dialog),
            ) {
                settingsScreenViewModel.showDialog(message)
            }
        }
        item {
            ActionSetting(
                text = stringResource(id = R.string.horologist_logout),
                enabled = false
            ) {
                settingsScreenViewModel.logout()
            }
        }
    }
}

@Composable
fun ActionSetting(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Chip(
        onClick = onClick,
        label = {
            Text(text)
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ToggleSetting(
    value: Boolean,
    text: String,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    ToggleChip(
        checked = value,
        toggleControl = {
            Icon(
                imageVector = ToggleChipDefaults.radioIcon(checked = value),
                contentDescription = if (value) stringResource(id = R.string.horologist_on) else stringResource(
                    id = R.string.horologist_off
                ),
            )
        },
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        label = {
            Text(text)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CheckedSetting(
    value: Boolean,
    text: String,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    ToggleChip(
        checked = value,
        toggleControl = {
            Icon(
                imageVector = ToggleChipDefaults.checkboxIcon(checked = value),
                contentDescription = if (value) stringResource(id = R.string.horologist_on) else stringResource(
                    id = R.string.horologist_off
                ),
            )
        },
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        label = {
            Text(text)
        }, modifier = Modifier.fillMaxWidth()
        )
    }
    