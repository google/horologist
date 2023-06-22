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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * This composable fulfils the redlines of the following components:
 * - AlertDialog - Title + body + buttons
 */
@Deprecated(
    "Replaced by AlertDialog in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "AlertDialog(body, onCancelButtonClick, onOKButtonClick, showDialog, scalingLazyListState, modifier, title, okButtonContentDescription, cancelButtonContentDescription)",
        "com.google.android.horologist.compose.material.AlertDialog"
    )
)
@ExperimentalHorologistApi
@Composable
public fun AlertDialog(
    body: String,
    onCancelButtonClick: () -> Unit,
    onOKButtonClick: () -> Unit,
    showDialog: Boolean,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    title: String = "",
    okButtonContentDescription: String = stringResource(android.R.string.ok),
    cancelButtonContentDescription: String = stringResource(android.R.string.cancel)
) {
    com.google.android.horologist.compose.material.AlertDialog(
        message = body,
        onCancelButtonClick = onCancelButtonClick,
        onOKButtonClick = onOKButtonClick,
        showDialog = showDialog,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        title = title,
        okButtonContentDescription = okButtonContentDescription,
        cancelButtonContentDescription = cancelButtonContentDescription
    )
}
