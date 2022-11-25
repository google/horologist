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

package com.google.android.horologist.auth.composables.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.material.dialog.DialogDefaults
import com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi
import com.google.android.horologist.auth.composables.R
import kotlinx.coroutines.delay

@ExperimentalHorologistAuthComposablesApi
@Composable
public fun SignedInConfirmationDialog(
    displayName: String,
    email: String,
    showDialog: Boolean,
    onDismissOrTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = DialogDefaults.ShortDurationMillis
) {
    val currentOnDismissOrTimeout by rememberUpdatedState(onDismissOrTimeout)

    LaunchedEffect(durationMillis) {
        delay(durationMillis)
        currentOnDismissOrTimeout()
    }

    Dialog(
        showDialog = showDialog,
        onDismissRequest = currentOnDismissOrTimeout,
        modifier = modifier
    ) {
        SignedInConfirmationDialogContent(
            displayName = displayName,
            email = email,
            modifier = modifier
        )
    }
}

@ExperimentalHorologistAuthComposablesApi
@Composable
internal fun SignedInConfirmationDialogContent(
    displayName: String,
    email: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .size(ChipDefaults.LargeIconSize)
                .background(color = Color.Blue, shape = CircleShape)
        ) {
            Text(
                text = displayName.first().uppercase(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = stringResource(
                id = R.string.horologist_signedin_confirmation_greeting,
                displayName
            ),
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = email,
            modifier = Modifier
                .padding(top = 5.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@ExperimentalHorologistAuthComposablesApi
@Composable
public fun SignedInConfirmationDialog(
    showDialog: Boolean,
    onDismissOrTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = DialogDefaults.ShortDurationMillis
) {
    val currentOnDismissOrTimeout by rememberUpdatedState(onDismissOrTimeout)

    LaunchedEffect(durationMillis) {
        delay(durationMillis)
        currentOnDismissOrTimeout()
    }

    Dialog(
        showDialog = showDialog,
        onDismissRequest = currentOnDismissOrTimeout,
        modifier = modifier
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                text = "Success!",
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
