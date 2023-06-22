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

package com.google.android.horologist.auth.ui.common.screens.prompt

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
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.composables.screens.SignInPlaceholderScreen
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Title

/**
 * A screen to prompt users to sign in.
 *
 * Through the [message] the
 * [sign-in benefits](https://developer.android.com/training/wearables/design/sign-in#benefits) are
 * explained to the user.
 *
 * The [viewModel] will take care of
 * [streamlining](https://developer.android.com/training/wearables/design/sign-in#streamline) the
 * process when the user is already signed in. [onAlreadySignedIn] should be used to navigate away
 * from this screen in that scenario.
 *
 * The [content] should provide
 * [sign-in alternatives](https://developer.android.com/training/wearables/design/sign-in#alternatives).
 *
 * @sample com.google.android.horologist.auth.sample.screens.googlesignin.prompt.GoogleSignInPromptSampleScreen
 * @sample com.google.android.horologist.auth.sample.screens.oauth.devicegrant.prompt.DeviceGrantSignInPromptScreen
 * @sample com.google.android.horologist.auth.sample.screens.oauth.pkce.prompt.PKCESignInPromptScreen
 */
@ExperimentalHorologistApi
@Composable
public fun SignInPromptScreen(
    message: String,
    onAlreadySignedIn: (account: AccountUiModel) -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.horologist_signin_prompt_title),
    viewModel: SignInPromptViewModel = viewModel(),
    loadingContent: @Composable () -> Unit = { SignInPlaceholderScreen(modifier = modifier) },
    content: ScalingLazyListScope.() -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SignInPromptScreen(
        state = state,
        title = title,
        message = message,
        onIdleStateObserved = { viewModel.onIdleStateObserved() },
        onAlreadySignedIn = onAlreadySignedIn,
        columnState = columnState,
        loadingContent = loadingContent,
        modifier = modifier,
        content = content
    )
}

@Composable
internal fun SignInPromptScreen(
    state: SignInPromptScreenState,
    title: String,
    message: String,
    onIdleStateObserved: () -> Unit,
    onAlreadySignedIn: (account: AccountUiModel) -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { SignInPlaceholderScreen(modifier = modifier) },
    content: ScalingLazyListScope.() -> Unit
) {
    when (state) {
        SignInPromptScreenState.Idle -> {
            SideEffect {
                onIdleStateObserved()
            }

            loadingContent()
        }

        SignInPromptScreenState.Loading -> {
            loadingContent()
        }

        is SignInPromptScreenState.SignedIn -> {
            SignInPlaceholderScreen(modifier = modifier)

            onAlreadySignedIn(state.account)
        }

        SignInPromptScreenState.SignedOut -> {
            ScalingLazyColumn(
                columnState = columnState,
                modifier = modifier
            ) {
                item { Title(title, Modifier) }
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
