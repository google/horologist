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

package com.google.android.horologist.compose.material

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberColumnState

/**
 * This component is an alternative to [AlertContent], providing the following:
 * - a convenient way of passing a title and a message;
 * - default positive and negative buttons;
 * - wrapped in a [Dialog];
 */
@ExperimentalHorologistApi
@Composable
public fun AlertDialog(
    message: String,
    onCancelButtonClick: () -> Unit,
    onOKButtonClick: () -> Unit,
    showDialog: Boolean,
    modifier: Modifier = Modifier,
    title: String = "",
    okButtonContentDescription: String = stringResource(android.R.string.ok),
    cancelButtonContentDescription: String = stringResource(android.R.string.cancel),
    columnState: ScalingLazyColumnState = rememberColumnState(
        ScalingLazyColumnDefaults.responsive()
    ),
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onCancelButtonClick,
        modifier = modifier,
        scrollState = columnState.state
    ) {
        AlertContent(
            title = title,
            body = message,
            onCancelButtonClick = onCancelButtonClick,
            onOKButtonClick = onOKButtonClick,
            okButtonContentDescription = okButtonContentDescription,
            cancelButtonContentDescription = cancelButtonContentDescription,
            columnState = columnState,
            showPositionIndicator = false
        )
    }
}

@ExperimentalHorologistApi
@Composable
fun AlertContent(
    body: String,
    onCancelButtonClick: (() -> Unit)?,
    onOKButtonClick: (() -> Unit)?,
    title: String = "",
    okButtonContentDescription: String = stringResource(android.R.string.ok),
    cancelButtonContentDescription: String = stringResource(android.R.string.cancel),
    columnState: ScalingLazyColumnState = rememberColumnState(
        ScalingLazyColumnDefaults.responsive()
    ),
    showPositionIndicator: Boolean = true,
) {
    ResponsiveDialogContent(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.title3,
            )
        },
        onOkButtonClick = onOKButtonClick,
        onCancelButtonClick = onCancelButtonClick,
        okButtonContentDescription = okButtonContentDescription,
        cancelButtonContentDescription = cancelButtonContentDescription,
        state = columnState,
        showPositionIndicator = showPositionIndicator
    ) {
        item {
            Text(
                text = body,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
