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

package com.google.android.horologist.datalayer.sample.screens.inappprompts.custom.signin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.datalayer.sample.R

@Composable
fun SignInCustomPromptDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInCustomPromptDemoViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == SignInCustomPromptDemoScreenState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    SignInCustomPromptDemoScreen(
        state = state,
        onRunDemoClick = viewModel::onRunDemoClick,
        onPromptSignInClick = viewModel::onPromptSignInClick,
        onPromptDismiss = viewModel::onPromptDismiss,
        modifier = modifier,
    )
}

@Composable
fun SignInCustomPromptDemoScreen(
    state: SignInCustomPromptDemoScreenState,
    onRunDemoClick: () -> Unit,
    onPromptSignInClick: (nodeId: String) -> Unit,
    onPromptDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(all = 10.dp),
    ) {
        Text(text = stringResource(id = R.string.signin_custom_prompt_api_call_demo_message))

        Button(
            onClick = onRunDemoClick,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            enabled = state != SignInCustomPromptDemoScreenState.ApiNotAvailable,
        ) {
            Text(text = stringResource(id = R.string.signin_custom_prompt_run_demo_button_label))
        }

        when (state) {
            SignInCustomPromptDemoScreenState.Idle,
            SignInCustomPromptDemoScreenState.Loaded,
            -> {
                /* do nothing */
            }

            SignInCustomPromptDemoScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is SignInCustomPromptDemoScreenState.WatchFound -> {
                Row(
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Watch,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 20.dp, end = 20.dp),
                    )
                    Column {
                        Text(text = stringResource(id = R.string.signin_custom_prompt_demo_prompt_top_message))
                        Text(text = stringResource(id = R.string.signin_custom_prompt_demo_prompt_bottom_message))
                        Row {
                            TextButton(
                                onClick = onPromptDismiss,
                            ) {
                                Text(text = stringResource(id = R.string.signin_custom_prompt_demo_prompt_dismiss_button_label))
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            TextButton(
                                onClick = { onPromptSignInClick(state.nodeId) },
                            ) {
                                Text(text = stringResource(id = R.string.signin_custom_prompt_demo_prompt_confirm_button_label))
                            }
                        }
                    }
                }
            }

            SignInCustomPromptDemoScreenState.WatchNotFound -> {
                Text(
                    stringResource(
                        id = R.string.signin_custom_prompt_demo_result_label,
                        stringResource(id = R.string.signin_custom_prompt_demo_no_watches_found_label),
                    ),
                )
            }

            SignInCustomPromptDemoScreenState.PromptSignInClicked -> {
                Text(
                    stringResource(
                        id = R.string.signin_custom_prompt_demo_result_label,
                        stringResource(id = R.string.signin_custom_prompt_demo_prompt_positive_result_label),
                    ),
                )
            }

            SignInCustomPromptDemoScreenState.PromptDismissed -> {
                Text(
                    stringResource(
                        id = R.string.signin_custom_prompt_demo_result_label,
                        stringResource(id = R.string.signin_custom_prompt_demo_prompt_dismiss_result_label),
                    ),
                )
            }

            SignInCustomPromptDemoScreenState.ApiNotAvailable -> {
                Text(stringResource(id = R.string.wearable_message_api_unavailable))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInCustomPromptDemoScreenPreview() {
    SignInCustomPromptDemoScreen(
        state = SignInCustomPromptDemoScreenState.Idle,
        onRunDemoClick = { },
        onPromptSignInClick = { },
        onPromptDismiss = { },
    )
}

@Preview(showBackground = true)
@Composable
fun SignInCustomPromptDemoScreenPreviewWithPrompt() {
    SignInCustomPromptDemoScreen(
        state = SignInCustomPromptDemoScreenState.WatchFound("nodeId"),
        onRunDemoClick = { },
        onPromptSignInClick = { },
        onPromptDismiss = { },
    )
}
