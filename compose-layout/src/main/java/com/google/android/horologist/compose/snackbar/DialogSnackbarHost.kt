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

package com.google.android.horologist.compose.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Confirmation
import com.google.android.horologist.compose.navscaffold.ExperimentalComposeLayoutApi
import com.google.android.horologist.compose.snackbar.material.SnackbarHost
import com.google.android.horologist.compose.snackbar.material.SnackbarHostState
import com.google.android.horologist.compose.snackbar.material.toMillis

@ExperimentalComposeLayoutApi
@Composable
public fun DialogSnackbarHost(
    modifier: Modifier = Modifier,
    hostState: SnackbarHostState,
) {
    val accessibilityManager = LocalAccessibilityManager.current
    SnackbarHost(
        modifier = modifier.alpha(0.9f),
        snackbar = {
            val duration = it.duration.toMillis(
                it.actionLabel != null,
                accessibilityManager
            )
            Confirmation(onTimeout = { it.dismiss() }, durationMillis = duration) {
            Text(
                modifier = Modifier.align(CenterHorizontally),
                text = it.message,
                style = MaterialTheme.typography.display3
            )
            Button(
                modifier = Modifier.align(CenterHorizontally),
                onClick = { it.dismiss() }
            ) {
                Text(text = "Dismiss")
            }
        }
        },
        hostState = hostState,
    )
}
