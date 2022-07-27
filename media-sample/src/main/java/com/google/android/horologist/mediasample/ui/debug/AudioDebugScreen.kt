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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.mediasample.R
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.seconds

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
                text = stringResource(id = R.string.horologist_sample_audio_debug),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.title3,
            )
        }
        item {
            val format = uiState?.formatDetails?.format?.run {
                "$sampleMimeType $sampleRate"
            }.orEmpty()
            Text(
                text = stringResource(id = R.string.horologist_sample_debug_format, format),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            val supported = uiState?.formatDetails?.formatSupported.toString()
            Text(
                text = stringResource(id = R.string.horologist_sample_offload_supported, supported),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            Text(
                text = stringResource(
                    id = R.string.horologist_sample_debug_offload_sleeping,
                    uiState?.offloadState?.sleepingForOffload?.toString().orEmpty()
                ),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            Text(
                text = stringResource(
                    id = R.string.horologist_sample_debug_offload_scheduled,
                    uiState?.offloadState?.offloadSchedulingEnabled.toString().orEmpty()
                ),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            val enabled = uiState?.times?.run { formatDuration(enabled) }.orEmpty()
            val disabled = uiState?.times?.run { formatDuration(disabled) }.orEmpty()
            Text(
                text = stringResource(
                    id = R.string.horologist_sample_debug_offload_percent,
                    uiState?.times?.percent + "($enabled/$disabled)"
                ),
                style = MaterialTheme.typography.body2,
            )
        }
        item {
            Text(
                text = stringResource(id = R.string.horologist_sample_audio_debug_errors),
                modifier = Modifier.padding(vertical = 12.dp),
                style = MaterialTheme.typography.title3,
            )
        }
        items(uiState?.errors.orEmpty().reversed()) {
            val message = remember(it.time) {
                val time = Instant.ofEpochMilli(it.time).atZone(ZoneId.systemDefault())
                    .toLocalTime()
                "$time ${it.message}"
            }
            Text(
                text = message,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption3,
            )
        }
    }
}

fun formatDuration(millis: Long): String {
    return (millis / 1000).seconds.toString()
}
