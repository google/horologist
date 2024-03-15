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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.installtile

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.datalayer.sample.R

@Composable
fun InstallTilePromptDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: InstallTilePromptDemoViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == InstallTilePromptDemoScreenState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    val context = LocalContext.current

    InstallTilePromptDemoScreen(
        state = state,
        onRunDemoClick = viewModel::onRunDemoClick,
        getInstallTilePromptIntent = {
            viewModel.installTilePrompt.getIntent(
                context = context,
                appPackageName = context.packageName,
                image = R.drawable.watch_app_screenshot,
                topMessage = context.getString(R.string.install_tile_prompt_demo_prompt_top_message),
                bottomMessage = context.getString(R.string.install_tile_prompt_demo_prompt_bottom_message),
            )
        },
        onPromptLaunched = viewModel::onPromptLaunched,
        onPromptPositiveButtonClick = viewModel::onPromptPositiveButtonClick,
        onPromptDismiss = viewModel::onPromptDismiss,
        modifier = modifier,
    )
}

@Composable
fun InstallTilePromptDemoScreen(
    state: InstallTilePromptDemoScreenState,
    onRunDemoClick: () -> Unit,
    getInstallTilePromptIntent: (nodeId: String) -> Intent,
    onPromptLaunched: () -> Unit,
    onPromptPositiveButtonClick: () -> Unit,
    onPromptDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            onPromptPositiveButtonClick()
        } else {
            onPromptDismiss()
        }
    }

    Column(
        modifier = modifier.padding(all = 10.dp),
    ) {
        Text(text = stringResource(id = R.string.install_tile_prompt_api_call_demo_message))

        Button(
            onClick = onRunDemoClick,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            enabled = state != InstallTilePromptDemoScreenState.ApiNotAvailable,
        ) {
            Text(text = stringResource(id = R.string.install_tile_prompt_run_demo_button_label))
        }

        when (state) {
            InstallTilePromptDemoScreenState.Idle,
            InstallTilePromptDemoScreenState.Loaded,
            -> {
                /* do nothing */
            }

            InstallTilePromptDemoScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is InstallTilePromptDemoScreenState.WatchFound -> {
                SideEffect { launcher.launch(getInstallTilePromptIntent(state.nodeId)) }

                onPromptLaunched()
            }

            InstallTilePromptDemoScreenState.WatchNotFound -> {
                Text(
                    stringResource(
                        id = R.string.reengage_prompt_demo_result_label,
                        stringResource(id = R.string.install_tile_prompt_demo_no_watches_found_label),
                    ),
                )
            }

            InstallTilePromptDemoScreenState.PromptPositiveButtonClicked -> {
                Text(
                    stringResource(
                        id = R.string.reengage_prompt_demo_result_label,
                        stringResource(id = R.string.install_tile_prompt_demo_prompt_positive_result_label),
                    ),
                )
            }

            InstallTilePromptDemoScreenState.PromptDismissed -> {
                Text(
                    stringResource(
                        id = R.string.reengage_prompt_demo_result_label,
                        stringResource(id = R.string.install_tile_prompt_demo_prompt_dismiss_result_label),
                    ),
                )
            }

            InstallTilePromptDemoScreenState.ApiNotAvailable -> {
                Text(stringResource(id = R.string.wearable_message_api_unavailable))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InstallTilePromptDemoScreenPreview() {
    InstallTilePromptDemoScreen(
        state = InstallTilePromptDemoScreenState.Idle,
        onRunDemoClick = { },
        getInstallTilePromptIntent = { Intent() },
        onPromptLaunched = { },
        onPromptPositiveButtonClick = { },
        onPromptDismiss = { },
    )
}
