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

import android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog

/**
 * This composable fulfils the redlines of the following components:
 * - AlertDialog - Title + body + buttons
 */
@Composable
public fun AlertDialog(
    body: String,
    onCancelButtonClick: () -> Unit,
    onOKButtonClick: () -> Unit,
    showDialog: Boolean,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    title: String = "",
    okButtonContentDescription: String = stringResource(R.string.ok),
    cancelButtonContentDescription: String = stringResource(R.string.cancel)
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onCancelButtonClick,
        scrollState = scalingLazyListState,
        modifier = modifier
    ) {
        AlertDialogAlert(
            title = title,
            body = body,
            onCancelButtonClick = onCancelButtonClick,
            onOKButtonClick = onOKButtonClick,
            okButtonContentDescription = okButtonContentDescription,
            cancelButtonContentDescription = cancelButtonContentDescription
        )
    }
}

@Composable
internal fun AlertDialogAlert(
    title: String,
    body: String,
    onCancelButtonClick: () -> Unit,
    onOKButtonClick: () -> Unit,
    okButtonContentDescription: String,
    cancelButtonContentDescription: String
) {
    Alert(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.title3
            )
        },
        content = {
            Text(
                text = body,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        },
        negativeButton = {
            StandardButton(
                imageVector = Icons.Default.Close,
                contentDescription = cancelButtonContentDescription,
                onClick = onCancelButtonClick,
                buttonType = StandardButtonType.Secondary
            )
        },
        positiveButton = {
            StandardButton(
                imageVector = Icons.Default.Check,
                contentDescription = okButtonContentDescription,
                onClick = onOKButtonClick
            )
        }
    )
}
