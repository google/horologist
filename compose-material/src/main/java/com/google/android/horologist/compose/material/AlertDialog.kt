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
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
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
 * - additional content can be specified between the message and the buttons
 * - default positive and negative buttons;
 * - wrapped in a [Dialog];
 */
@ExperimentalHorologistApi
@Composable
public fun AlertDialog(
    showDialog: Boolean,
    onCancel: () -> Unit,
    onOk: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: String? = null,
    message: String? = null,
    okButtonContentDescription: String = stringResource(android.R.string.ok),
    cancelButtonContentDescription: String = stringResource(android.R.string.cancel),
    state: ScalingLazyColumnState = rememberColumnState(
        ScalingLazyColumnDefaults.responsive(),
    ),
    content: (ScalingLazyListScope.() -> Unit)? = null,
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onCancel,
        modifier = modifier,
        scrollState = state.state,
    ) {
        AlertContent(
            icon = icon,
            title = title,
            message = message,
            content = content,
            onCancel = onCancel,
            onOk = onOk,
            okButtonContentDescription = okButtonContentDescription,
            cancelButtonContentDescription = cancelButtonContentDescription,
            state = state,
            showPositionIndicator = false,
        )
    }
}

/**
 * This component is an alternative to [AlertContent], providing the following:
 * - a convenient way of passing a title and a message;
 * - slot for scrollable content (including stack of Chips for options);
 * - wrapped in a [Dialog];
 */
@ExperimentalHorologistApi
@Composable
public fun AlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: String? = null,
    message: String? = null,
    state: ScalingLazyColumnState = rememberColumnState(
        ScalingLazyColumnDefaults.responsive(),
    ),
    content: (ScalingLazyListScope.() -> Unit)? = null,
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        modifier = modifier,
        scrollState = state.state,
    ) {
        AlertContent(
            icon = icon,
            title = title,
            message = message,
            content = content,
            state = state,
            showPositionIndicator = true,
        )
    }
}

@ExperimentalHorologistApi
@Composable
public fun AlertContent(
    onCancel: (() -> Unit)? = null,
    onOk: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: String? = null,
    message: String? = null,
    okButtonContentDescription: String = stringResource(android.R.string.ok),
    cancelButtonContentDescription: String = stringResource(android.R.string.cancel),
    state: ScalingLazyColumnState = rememberColumnState(
        ScalingLazyColumnDefaults.responsive(),
    ),
    showPositionIndicator: Boolean = true,
    content: (ScalingLazyListScope.() -> Unit)? = null,
) {
    ResponsiveDialogContent(
        icon = icon,
        title = title?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                    maxLines = if (icon == null) 3 else 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        message = message?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Start,
                )
            }
        },
        content = content,
        onOk = onOk,
        onCancel = onCancel,
        okButtonContentDescription = okButtonContentDescription,
        cancelButtonContentDescription = cancelButtonContentDescription,
        state = state,
        showPositionIndicator = showPositionIndicator,
    )
}
