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

package com.google.android.horologist.auth.sample.screens.oauth.pkce.prompt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.composables.chips.GuestModeChip
import com.google.android.horologist.auth.composables.chips.SignInChip
import com.google.android.horologist.auth.sample.R
import com.google.android.horologist.auth.sample.Screen
import com.google.android.horologist.auth.ui.common.screens.prompt.SignInPromptScreen
import com.google.android.horologist.auth.ui.common.screens.prompt.SignInPromptViewModel
import com.google.android.horologist.base.ui.components.ConfirmationDialog
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.ChipType

@Composable
fun PKCESignInPromptScreen(
    navController: NavHostController,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: SignInPromptViewModel = viewModel(factory = PKCESignInPromptViewModelFactory)
) {
    var showAlreadySignedInDialog by rememberSaveable { mutableStateOf(false) }

    SignInPromptScreen(
        message = stringResource(id = R.string.pkce_sign_in_prompt_message),
        onAlreadySignedIn = {
            showAlreadySignedInDialog = true
        },
        columnState = columnState,
        modifier = modifier,
        viewModel = viewModel
    ) {
        item {
            SignInChip(
                onClick = {
                    navController.navigate(Screen.PKCESignInScreen.route) {
                        popUpTo(Screen.MainScreen.route)
                    }
                },
                chipType = ChipType.Secondary
            )
        }
        item {
            GuestModeChip(
                onClick = navController::popBackStack,
                chipType = ChipType.Secondary
            )
        }
    }

    if (showAlreadySignedInDialog) {
        ConfirmationDialog(
            onTimeout = {
                showAlreadySignedInDialog = false
                navController.popBackStack()
            }
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.pkce_sign_in_prompt_already_signed_in_message)
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun PKCESignInPromptScreenPreview() {
    PKCESignInPromptScreen(navController = NavHostController(LocalContext.current), columnState = belowTimeTextPreview())
}
