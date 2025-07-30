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

package com.google.android.horologist.datalayer.sample.screens.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.datalayer.sample.R

@Composable
fun CounterScreen(
    modifier: Modifier = Modifier,
    viewModel: CounterScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == CounterScreenUiState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    CounterScreen(
        state = state,
        onPlusClick = { viewModel.updateCounter() },
        modifier = modifier,
    )
}

@Composable
fun CounterScreen(
    state: CounterScreenUiState,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when (state) {
            CounterScreenUiState.Idle,
            CounterScreenUiState.CheckingApiAvailability,
            CounterScreenUiState.Loading,
            -> {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                )
            }

            is CounterScreenUiState.Loaded -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(text = stringResource(R.string.app_helper_counter_increase_explanation))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            text = stringResource(R.string.app_helper_counter_message, state.counter),
                        )
                        Button(onClick = onPlusClick) {
                            Icon(imageVector = Icons.Default.PlusOne, contentDescription = "Plus 1")
                        }
                    }
                }
            }

            CounterScreenUiState.ApiNotAvailable -> {
                Text(
                    text = stringResource(R.string.wearable_message_api_unavailable),
                    modifier.fillMaxWidth(),
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
