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

package com.google.android.horologist.mediasample.ui.auth.signout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ConfirmationDialogDefaults
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.google.android.horologist.media.ui.material3.navigation.CustomRoute
import com.google.android.horologist.mediasample.R
import kotlinx.coroutines.delay

@Composable
fun GoogleSignOutScreen(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<CustomRoute>,
    viewModel: UampGoogleSignOutViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        GoogleSignOutScreenState.Idle -> {
            SideEffect {
                viewModel.onIdleStateObserved()
            }

            LoadingView(modifier = modifier)
        }

        GoogleSignOutScreenState.Loading -> {
            LoadingView(modifier = modifier)
        }

        GoogleSignOutScreenState.Success -> {
            val durationMillis = ConfirmationDialogDefaults.DurationMillis
            var showConfirmation by rememberSaveable { mutableStateOf(true) }
            LaunchedEffect(showConfirmation) {
                if (showConfirmation) {
                    delay(durationMillis)
                    showConfirmation = false
                    backStack.removeLastOrNull()
                }
            }

            Dialog(
                visible = showConfirmation,
                onDismissRequest = {
                    showConfirmation = false
                    backStack.removeLastOrNull()
                },
                modifier = modifier,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.google_sign_out_success_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        GoogleSignOutScreenState.Failed -> {
            SideEffect {
                backStack.removeLastOrNull()
            }
        }
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    ScreenScaffold(
        modifier = modifier,
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
