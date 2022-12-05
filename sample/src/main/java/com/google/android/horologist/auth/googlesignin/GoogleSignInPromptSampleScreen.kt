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

package com.google.android.horologist.auth.googlesignin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.horologist.auth.composables.chips.GuestModeChip
import com.google.android.horologist.auth.composables.chips.SignInChip
import com.google.android.horologist.auth.ui.common.screens.SignInPromptScreen
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.sample.R
import com.google.android.horologist.sample.Screen

@Composable
fun GoogleSignInPromptSampleScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: GoogleSignInPromptViewModel = viewModel(factory = GoogleSignInPromptViewModel.Factory)
) {
    SignInPromptScreen(
        message = stringResource(id = R.string.google_sign_in_prompt_message),
        onAlreadySignedIn = { navController.popBackStack() },
        modifier = modifier,
        viewModel = viewModel
    ) {
        item {
            SignInChip(
                onClick = {
                    navController.navigate(Screen.AuthGoogleSignInScreen.route) {
                        popUpTo(Screen.AuthMenuScreen.route)
                    }
                },
                chipType = StandardChipType.Secondary
            )
        }
        item {
            GuestModeChip(
                onClick = { navController.popBackStack() },
                chipType = StandardChipType.Secondary
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun GoogleSignInPromptSampleScreenPreview() {
    GoogleSignInPromptSampleScreen(navController = NavHostController(LocalContext.current))
}
