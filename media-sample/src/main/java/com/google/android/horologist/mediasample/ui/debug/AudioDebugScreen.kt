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

package com.google.android.horologist.mediasample.ui.debug

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.mediasample.R
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AudioDebugScreen(
    focusRequester: FocusRequester,
    state: ScalingLazyListState,
    audioDebugScreenViewModel: AudioDebugScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by rememberStateWithLifecycle(audioDebugScreenViewModel.uiState)

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
            val format = uiState.format?.run {
                "$sampleMimeType $sampleRate"
            }.orEmpty()
            Text(
                text = stringResource(id = R.string.horologist_sample_debug_format, format),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            Text(
                text = stringResource(
                    id = R.string.horologist_sample_debug_offload_sleeping,
                    uiState.sleepingForOffload
                ),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            Text(
                text = stringResource(
                    id = R.string.horologist_sample_debug_offload_scheduled,
                    uiState.offloadSchedulingEnabled
                ),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            Text(
                text = stringResource(
                    id = R.string.horologist_sample_debug_offload_percent,
                    uiState.times.percent
                ),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            val time = uiState.times.enabled.milliseconds.toString()
            Text(
                text = stringResource(id = R.string.horologist_sample_debug_offload_time, time),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
