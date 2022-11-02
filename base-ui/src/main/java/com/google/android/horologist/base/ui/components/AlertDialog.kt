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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog

/**
 * This composable fulfils the redlines of the following components:
 * - AlertDialog
 */
@Composable
public fun AlertDialog(
    text: String,
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
        AlertDialogAlert(
            text = text,
            proceedText = proceedText,
            cancelText = cancelText,
            onCancelButtonClick = onCancelButtonClick,
            onProceedButtonClick = onProceedButtonClick
        )
    }
}

@Composable
public fun AlertDialogAlert(
    text: String,
    proceedText: String,
    cancelText: String,
    onCancelButtonClick: () -> Unit,
    onProceedButtonClick: () -> Unit
) {
    Alert(
        title = {
            Text(
                text = text,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.title3
            )
        },
        negativeButton = {
            StandardButton(
                imageVector = Icons.Default.Close,
                contentDescription = cancelText,
                onClick = onCancelButtonClick,
                buttonType = StandardButtonType.Secondary
            )
        },
        positiveButton = {
            StandardButton(
                imageVector = Icons.Default.Check,
                contentDescription = proceedText,
                onClick = onProceedButtonClick
            )
        }
    )
}
