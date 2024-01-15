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

package com.google.android.horologist.datalayer.sample.screens.inappprompts

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
fun InstallSampleAppPromptDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: InstallSampleAppPromptDemoViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    InstallSampleAppPromptDemoScreen(
        state = state,
        onRunDemoClick = viewModel::onRunDemoClick,
        getInstallPromptIntent = { watchName ->
            viewModel.phoneUiDataLayerHelper.getInstallPromptIntent(
                context = context,
                appName = context.getString(R.string.app_name),
                appPackageName = context.packageName,
                watchName = watchName,
                message = context.getString(R.string.install_sample_app_prompt_demo_prompt_message),
                image = R.drawable.watch_app_screenshot,
            )
        },
        onInstallPromptLaunched = viewModel::onInstallPromptLaunched,
        onInstallPromptInstallClick = viewModel::onInstallPromptInstallClick,
        onInstallPromptCancel = viewModel::onInstallPromptCancel,
        modifier = modifier,
    )
}

@Composable
fun InstallSampleAppPromptDemoScreen(
    state: InstallSampleAppPromptDemoScreenState,
    onRunDemoClick: () -> Unit,
    getInstallPromptIntent: (watchName: String) -> Intent,
    onInstallPromptLaunched: () -> Unit,
    onInstallPromptInstallClick: () -> Unit,
    onInstallPromptCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            onInstallPromptInstallClick()
        } else {
            onInstallPromptCancel()
        }
    }

    Column(
        modifier = modifier.padding(all = 10.dp),
    ) {
        Text(text = stringResource(id = R.string.install_sample_app_prompt_api_call_demo_message))

        Button(
            onClick = onRunDemoClick,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Text(text = stringResource(id = R.string.install_sample_app_prompt_run_demo_button_label))
        }

        when (state) {
            InstallSampleAppPromptDemoScreenState.Idle -> {
                /* do nothing */
            }

            InstallSampleAppPromptDemoScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is InstallSampleAppPromptDemoScreenState.WatchFound -> {
                SideEffect { launcher.launch(getInstallPromptIntent(state.watchName)) }

                onInstallPromptLaunched()
            }

            InstallSampleAppPromptDemoScreenState.WatchNotFound -> {
                Text(
                    stringResource(
                        id = R.string.install_sample_app_prompt_demo_result_label,
                        stringResource(id = R.string.install_sample_app_prompt_demo_no_watches_found_label),
                    ),
                    modifier = Modifier.padding(16.dp),
                )
            }

            InstallSampleAppPromptDemoScreenState.InstallPromptInstallClicked -> {
                Text(
                    stringResource(
                        id = R.string.install_sample_app_prompt_demo_result_label,
                        stringResource(id = R.string.install_sample_app_prompt_demo_prompt_install_result_label),
                    ),
                    modifier = Modifier.padding(16.dp),
                )
            }

            InstallSampleAppPromptDemoScreenState.InstallPromptInstallCancelled -> {
                Text(
                    stringResource(
                        id = R.string.install_sample_app_prompt_demo_result_label,
                        stringResource(id = R.string.install_sample_app_prompt_demo_prompt_cancel_result_label),
                    ),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InstallAppPromptDemo2ScreenPreview() {
    InstallSampleAppPromptDemoScreen(
        state = InstallSampleAppPromptDemoScreenState.Idle,
        onRunDemoClick = { },
        getInstallPromptIntent = { Intent() },
        onInstallPromptLaunched = { },
        onInstallPromptInstallClick = { },
        onInstallPromptCancel = { },
    )
}
