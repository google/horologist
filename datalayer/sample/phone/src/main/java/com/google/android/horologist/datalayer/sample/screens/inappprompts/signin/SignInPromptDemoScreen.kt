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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.signin

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.datalayer.sample.R

@Composable
fun SignInPromptDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInPromptDemoViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == SignInPromptDemoScreenState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    val context = LocalContext.current

    SignInPromptDemoScreen(
        state = state,
        onRunDemoClick = viewModel::onRunDemoClick,
        getSignInPromptIntent = { nodeId ->
            viewModel.signInPrompt.getIntent(
                context = context,
                nodeId = nodeId,
                image = R.drawable.watch_app_screenshot,
                topMessage = context.getString(R.string.signin_prompt_demo_prompt_top_message),
                bottomMessage = context.getString(R.string.signin_prompt_demo_prompt_bottom_message),
            )
        },
        onPromptLaunched = viewModel::onPromptLaunched,
        onPromptSignInClick = viewModel::onPromptSignInClick,
        onPromptDismiss = viewModel::onPromptDismiss,
        modifier = modifier,
    )
}

@Composable
fun SignInPromptDemoScreen(
    state: SignInPromptDemoScreenState,
    onRunDemoClick: () -> Unit,
    getSignInPromptIntent: (nodeId: String) -> Intent,
    onPromptLaunched: () -> Unit,
    onPromptSignInClick: () -> Unit,
    onPromptDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            onPromptSignInClick()
        } else {
            onPromptDismiss()
        }
    }

    Column(
        modifier = modifier.padding(all = 10.dp),
    ) {
        Text(text = stringResource(id = R.string.signin_prompt_api_call_demo_message))

        Button(
            onClick = onRunDemoClick,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            enabled = state != SignInPromptDemoScreenState.ApiNotAvailable,
        ) {
            Text(text = stringResource(id = R.string.signin_prompt_run_demo_button_label))
        }

        when (state) {
            SignInPromptDemoScreenState.Idle,
            SignInPromptDemoScreenState.Loaded,
            -> {
                /* do nothing */
            }

            SignInPromptDemoScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is SignInPromptDemoScreenState.WatchFound -> {
                SideEffect { launcher.launch(getSignInPromptIntent(state.nodeId)) }

                onPromptLaunched()
            }

            SignInPromptDemoScreenState.WatchNotFound -> {
                Text(
                    stringResource(
                        id = R.string.signin_prompt_demo_result_label,
                        stringResource(id = R.string.signin_prompt_demo_no_watches_found_label),
                    ),
                )
            }

            SignInPromptDemoScreenState.PromptSignInClicked -> {
                Text(
                    stringResource(
                        id = R.string.signin_prompt_demo_result_label,
                        stringResource(id = R.string.signin_prompt_demo_prompt_positive_result_label),
                    ),
                )
            }

            SignInPromptDemoScreenState.PromptDismissed -> {
                Text(
                    stringResource(
                        id = R.string.signin_prompt_demo_result_label,
                        stringResource(id = R.string.signin_prompt_demo_prompt_dismiss_result_label),
                    ),
                )
            }

            SignInPromptDemoScreenState.ApiNotAvailable -> {
                Text(stringResource(id = R.string.wearable_message_api_unavailable))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPromptDemoScreenPreview() {
    SignInPromptDemoScreen(
        state = SignInPromptDemoScreenState.Idle,
        onRunDemoClick = { },
        getSignInPromptIntent = { Intent() },
        onPromptLaunched = { },
        onPromptSignInClick = { },
        onPromptDismiss = { },
    )
}
