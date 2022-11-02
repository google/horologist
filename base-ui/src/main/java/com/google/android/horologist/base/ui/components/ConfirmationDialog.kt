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

package com.google.android.horologist.base.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog


@Composable
public fun ConfirmationDialog(
    prompt: String,
    proceedText: String,
    cancelText: String,
    onCancelButtonClick: () -> Unit,
    onProceedButtonClick: () -> Unit,
    showDialog: Boolean,
    scalingLazyListState: ScalingLazyListState
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onCancelButtonClick,
        scrollState = scalingLazyListState
    ) {
        EntityDialogAlert(
            prompt = prompt,
            proceedText = proceedText,
            cancelText = cancelText,
            onCancelButtonClick = onCancelButtonClick,
            onProceedButtonClick = onProceedButtonClick
        )
    }

}

@Composable
public fun ConfirmationDialogAlert(
    prompt: String,
    proceedText: String,
    cancelText: String,
    onCancelButtonClick: () -> Unit,
    onProceedButtonClick: () -> Unit
)
 {
    Alert(
        title = {
            Text(
                text = prompt,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.title3
            )
        },
        negativeButton = {
            Button(
                onClick = onCancelButtonClick,
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = cancelText,
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
            }
        },
        positiveButton = {
            Button(
                onClick = onProceedButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = proceedText,
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
            }
        }
    )
}

