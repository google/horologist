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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.installtile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.datalayer.sample.R

@Composable
fun InstallTileCustomPromptDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: InstallTilePromptDemoViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == InstallTileCustomPromptDemoScreenState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    val context = LocalContext.current

    InstallTileCustomPromptDemoScreen(
        state = state,
        onRunDemoClick = viewModel::onRunDemoClick,
        onInstallPromptInstallClick = {
            viewModel.installTilePrompt.performAction(
                context = context,
            )
            viewModel.onInstallPromptInstallClick()
        },
        onInstallPromptCancel = viewModel::onInstallPromptCancel,
        modifier = modifier,
    )
}

@Composable
fun InstallTileCustomPromptDemoScreen(
    state: InstallTileCustomPromptDemoScreenState,
    onRunDemoClick: () -> Unit,
    onInstallPromptInstallClick: () -> Unit,
    onInstallPromptCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(all = 10.dp),
    ) {
        Text(text = stringResource(id = R.string.install_tile_custom_prompt_api_call_demo_message))

        Button(
            onClick = onRunDemoClick,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            enabled = state != InstallTileCustomPromptDemoScreenState.ApiNotAvailable,
        ) {
            Text(text = stringResource(id = R.string.install_tile_custom_prompt_run_demo_button_label))
        }

        when (state) {
            InstallTileCustomPromptDemoScreenState.Idle,
            InstallTileCustomPromptDemoScreenState.Loaded,
            -> {
                /* do nothing */
            }

            InstallTileCustomPromptDemoScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is InstallTileCustomPromptDemoScreenState.WatchFound -> {
                AlertDialog(
                    icon = {
                        Icon(imageVector = Icons.Default.Watch, contentDescription = null)
                    },
                    title = {
                        Text(text = stringResource(id = R.string.install_tile_custom_prompt_demo_prompt_top_message))
                    },
                    text = {
                        Text(text = stringResource(id = R.string.install_tile_custom_prompt_demo_prompt_bottom_message))
                    },
                    onDismissRequest = onInstallPromptCancel,
                    confirmButton = {
                        TextButton(
                            onClick = onInstallPromptInstallClick,
                        ) {
                            Text(text = stringResource(id = R.string.install_tile_custom_prompt_demo_prompt_confirm_button_label))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = onInstallPromptCancel,
                        ) {
                            Text(text = stringResource(id = R.string.install_app_custom_prompt_demo_prompt_dismiss_button_label))
                        }
                    },
                )
            }

            InstallTileCustomPromptDemoScreenState.WatchNotFound -> {
                Text(
                    stringResource(
                        id = R.string.install_tile_custom_prompt_demo_result_label,
                        stringResource(id = R.string.install_tile_custom_prompt_demo_no_watches_found_label),
                    ),
                )
            }

            InstallTileCustomPromptDemoScreenState.InstallPromptInstallClicked -> {
                Text(
                    stringResource(
                        id = R.string.install_tile_custom_prompt_demo_result_label,
                        stringResource(id = R.string.install_tile_custom_prompt_demo_prompt_install_result_label),
                    ),
                )
            }

            InstallTileCustomPromptDemoScreenState.InstallPromptInstallCancelled -> {
                Text(
                    stringResource(
                        id = R.string.install_tile_custom_prompt_demo_result_label,
                        stringResource(id = R.string.install_tile_custom_prompt_demo_prompt_cancel_result_label),
                    ),
                )
            }

            InstallTileCustomPromptDemoScreenState.ApiNotAvailable -> {
                Text(stringResource(id = R.string.wearable_message_api_unavailable))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InstallTileCustomPromptDemoScreenPreview() {
    InstallTileCustomPromptDemoScreen(
        state = InstallTileCustomPromptDemoScreenState.Idle,
        onRunDemoClick = { },
        onInstallPromptInstallClick = { },
        onInstallPromptCancel = { },
    )
}

@Preview(showBackground = true)
@Composable
fun InstallTileCustomPromptDemoScreenPreviewWithPrompt() {
    InstallTileCustomPromptDemoScreen(
        state = InstallTileCustomPromptDemoScreenState.WatchFound,
        onRunDemoClick = { },
        onInstallPromptInstallClick = { },
        onInstallPromptCancel = { },
    )
}
