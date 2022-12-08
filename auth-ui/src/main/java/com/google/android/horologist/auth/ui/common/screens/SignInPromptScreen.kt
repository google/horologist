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

package com.google.android.horologist.auth.ui.common.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyListScope
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.auth.composables.dialogs.SignedInConfirmationDialog
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.base.ui.components.Title
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

@ExperimentalHorologistAuthUiApi
@Composable
public fun SignInPromptScreen(
    message: String,
    onAlreadySignedIn: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.horologist_signin_prompt_title),
    viewModel: SignInPromptViewModel = viewModel(),
    columnState: ScalingLazyColumnState,
    content: ScalingLazyListScope.() -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        SignInPromptScreenState.Idle -> {
            SideEffect {
                viewModel.startFlow()
            }

            LoadingView(modifier = modifier)
        }

        SignInPromptScreenState.Loading -> {
            LoadingView(modifier = modifier)
        }

        is SignInPromptScreenState.SignedIn -> {
            val signedInState = state as SignInPromptScreenState.SignedIn

            SignedInConfirmationDialog(
                name = signedInState.displayName,
                email = signedInState.email,
                avatarUri = signedInState.avatarUri
            ) {
                onAlreadySignedIn()
            }
        }

        SignInPromptScreenState.SignedOut -> {
            ScalingLazyColumn(
                modifier = modifier,
                columnState = columnState
            ) {
                item { Title(text = title) }
                item {
                    Text(
                        text = message,
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 12.dp,
                            start = 10.dp,
                            end = 10.dp
                        ),
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2
                    )
                }
                apply(content)
            }
        }
    }
}
