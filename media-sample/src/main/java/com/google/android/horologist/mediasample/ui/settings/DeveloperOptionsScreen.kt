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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.domain.proto.SettingsProto.OffloadMode
import com.google.android.horologist.mediasample.ui.navigation.navigateToAudioDebug
import com.google.android.horologist.mediasample.ui.navigation.navigateToSamples

@Composable
fun DeveloperOptionsScreen(
    columnState: ScalingLazyColumnState,
    developerOptionsScreenViewModel: DeveloperOptionsScreenViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val uiState by developerOptionsScreenViewModel.uiState.collectAsStateWithLifecycle()

    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(id = R.string.sample_developer_options),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.title3
            )
        }
        item {
            CheckedSetting(
                uiState.networkRequest != null,
                stringResource(id = R.string.request_network),
                enabled = uiState.writable
            ) {
                developerOptionsScreenViewModel.toggleNetworkRequest()
            }
        }
        item {
            ActionSetting(
                stringResource(id = R.string.sample_audio_debug)
            ) {
                navController.navigateToAudioDebug()
            }
        }
        item {
            ActionSetting(
                stringResource(id = R.string.sample_samples)
            ) {
                navController.navigateToSamples()
            }
        }
        item {
            CheckedSetting(
                uiState.showTimeTextInfo,
                stringResource(id = R.string.show_time_text_info),
                enabled = uiState.writable
            ) {
                developerOptionsScreenViewModel.setShowTimeTextInfo(it)
            }
        }
        item {
            CheckedSetting(
                uiState.debugOffload,
                stringResource(id = R.string.debug_offload),
                enabled = uiState.writable
            ) {
                developerOptionsScreenViewModel.setDebugOffload(it)
            }
        }
        item {
            ActionSetting(
                stringResource(id = R.string.offload_mode, uiState.offloadMode.name),
                enabled = uiState.writable
            ) {
                val newMode = when (uiState.offloadMode) {
                    OffloadMode.BACKGROUND -> OffloadMode.NEVER
                    OffloadMode.NEVER -> OffloadMode.ALWAYS
                    OffloadMode.ALWAYS -> OffloadMode.BACKGROUND
                    OffloadMode.UNRECOGNIZED -> OffloadMode.BACKGROUND
                }
                developerOptionsScreenViewModel.setOffloadMode(newMode)
            }
        }
        item {
            CheckedSetting(
                uiState.podcastControls,
                stringResource(id = R.string.podcast_controls),
                enabled = uiState.writable
            ) {
                developerOptionsScreenViewModel.setPodcastControls(it)
            }
        }
        item {
            CheckedSetting(
                uiState.loadItemsAtStartup,
                stringResource(id = R.string.load_items),
                enabled = uiState.writable
            ) {
                developerOptionsScreenViewModel.setLoadItemsAtStartup(it)
            }
        }
        item {
            CheckedSetting(
                uiState.streamingMode,
                stringResource(id = R.string.streaming_mode),
                enabled = uiState.writable
            ) {
                developerOptionsScreenViewModel.setStreamingMode(it)
            }
        }
        item {
            CheckedSetting(
                uiState.animated,
                stringResource(id = R.string.animated),
                enabled = uiState.writable
            ) {
                developerOptionsScreenViewModel.setAnimated(it)
            }
        }
        item {
            ActionSetting(
                text = stringResource(id = R.string.force_stop)
            ) {
                developerOptionsScreenViewModel.forceStop()
            }
        }
        item {
            val message = stringResource(id = R.string.sample_error)
            ActionSetting(
                stringResource(id = R.string.show_test_dialog)
            ) {
                developerOptionsScreenViewModel.showDialog(message)
            }
        }
    }
}
