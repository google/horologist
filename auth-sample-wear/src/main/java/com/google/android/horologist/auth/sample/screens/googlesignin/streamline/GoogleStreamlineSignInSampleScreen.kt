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

package com.google.android.horologist.auth.sample.screens.googlesignin.streamline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.sample.R
import com.google.android.horologist.auth.sample.Screen
import com.google.android.horologist.auth.ui.common.screens.streamline.StreamlineSignInScreen
import com.google.android.horologist.auth.ui.common.screens.streamline.StreamlineSignInViewModel
import com.google.android.horologist.auth.ui.googlesignin.streamline.GoogleStreamlineSignInViewModelFactory
import com.google.android.horologist.base.ui.components.ConfirmationDialog

@Composable
fun GoogleStreamlineSignInSampleScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: StreamlineSignInViewModel = viewModel(factory = GoogleStreamlineSignInViewModelFactory)
) {
    var showAlreadySignedInDialog by rememberSaveable { mutableStateOf(false) }

    StreamlineSignInScreen(
        onAlreadySignedIn = { showAlreadySignedInDialog = true },
        onSignedOut = {
            navController.navigate(Screen.GoogleSignInScreen.route) {
                popUpTo(Screen.MainScreen.route)
            }
        },
        modifier = modifier,
        viewModel = viewModel
    )

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
                text = stringResource(id = R.string.google_streamline_sign_in_already_signed_in_message)
            )
        }
    }
}
