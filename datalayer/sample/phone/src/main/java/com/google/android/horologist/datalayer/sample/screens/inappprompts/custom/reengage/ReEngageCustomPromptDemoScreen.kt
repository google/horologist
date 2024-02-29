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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.reengage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.datalayer.sample.R

@Composable
fun ReEngageCustomPromptDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: ReEngageCustomPromptDemoViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == ReEngageCustomPromptDemoScreenState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    ReEngageCustomPromptDemoScreen(
        state = state,
        onRunDemoClick = viewModel::onRunDemoClick,
        onPromptPositiveButtonClick = viewModel::onPromptPositiveButtonClick,
        onPromptDismiss = viewModel::onPromptDismiss,
        modifier = modifier,
    )
}

@Composable
fun ReEngageCustomPromptDemoScreen(
    state: ReEngageCustomPromptDemoScreenState,
    onRunDemoClick: () -> Unit,
    onPromptPositiveButtonClick: (nodeId: String) -> Unit,
    onPromptDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(all = 10.dp),
    ) {
        Text(text = stringResource(id = R.string.reengage_custom_prompt_api_call_demo_message))

        Button(
            onClick = onRunDemoClick,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            enabled = state != ReEngageCustomPromptDemoScreenState.ApiNotAvailable,
        ) {
            Text(text = stringResource(id = R.string.reengage_custom_prompt_run_demo_button_label))
        }

        when (state) {
            ReEngageCustomPromptDemoScreenState.Idle,
            ReEngageCustomPromptDemoScreenState.Loaded,
            -> {
                /* do nothing */
            }

            ReEngageCustomPromptDemoScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is ReEngageCustomPromptDemoScreenState.WatchFound -> {
                val sheetState = rememberModalBottomSheetState()

                ModalBottomSheet(
                    onDismissRequest = onPromptDismiss,
                    modifier = modifier,
                    sheetState = sheetState,
                    dragHandle = null,
                ) {
                    Column(
                        modifier = modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .align(Alignment.CenterHorizontally),
                        ) {
                            Icon(imageVector = Icons.Default.Watch, contentDescription = null)
                        }

                        Text(
                            text = stringResource(id = R.string.reengage_custom_prompt_demo_prompt_top_message),
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Text(
                            text = stringResource(id = R.string.reengage_custom_prompt_demo_prompt_bottom_message),
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                onClick = onPromptDismiss,
                                modifier = Modifier
                                    .padding(end = 12.dp),
                            ) {
                                Text(text = stringResource(id = R.string.reengage_custom_prompt_demo_prompt_dismiss_button_label))
                            }

                            Button(
                                onClick = {
                                    onPromptPositiveButtonClick(state.nodeId)
                                },
                            ) {
                                Text(text = stringResource(id = R.string.reengage_custom_prompt_demo_prompt_confirm_button_label))
                            }
                        }
                    }
                }
            }

            ReEngageCustomPromptDemoScreenState.WatchNotFound -> {
                Text(
                    stringResource(
                        id = R.string.reengage_custom_prompt_demo_result_label,
                        stringResource(id = R.string.reengage_custom_prompt_demo_no_watches_found_label),
                    ),
                )
            }

            ReEngageCustomPromptDemoScreenState.PromptPositiveButtonClicked -> {
                Text(
                    stringResource(
                        id = R.string.reengage_custom_prompt_demo_result_label,
                        stringResource(id = R.string.reengage_custom_prompt_demo_prompt_positive_result_label),
                    ),
                )
            }

            ReEngageCustomPromptDemoScreenState.PromptDismissed -> {
                Text(
                    stringResource(
                        id = R.string.reengage_custom_prompt_demo_result_label,
                        stringResource(id = R.string.reengage_custom_prompt_demo_prompt_dismiss_result_label),
                    ),
                )
            }

            ReEngageCustomPromptDemoScreenState.ApiNotAvailable -> {
                Text(stringResource(id = R.string.wearable_message_api_unavailable))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReEngageCustomPromptDemoScreenPreview() {
    ReEngageCustomPromptDemoScreen(
        state = ReEngageCustomPromptDemoScreenState.Idle,
        onRunDemoClick = { },
        onPromptPositiveButtonClick = { },
        onPromptDismiss = { },
    )
}
