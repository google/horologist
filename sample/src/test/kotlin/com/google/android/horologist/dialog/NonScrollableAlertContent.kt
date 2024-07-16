/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.AlertContent
import com.google.android.horologist.compose.material.centeredDialogColumnState

@ExperimentalHorologistApi
@Composable
internal fun NonScrollableAlertContent(
    onCancel: (() -> Unit)? = null,
    onOk: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: String? = null,
    message: String? = null,
    okButtonContentDescription: String = stringResource(android.R.string.ok),
    cancelButtonContentDescription: String = stringResource(android.R.string.cancel),
    content: (ScalingLazyListScope.() -> Unit)? = null,
) {
    val columnState = centeredDialogColumnState()

    AlertContent(
        onCancel = onCancel,
        onOk = onOk,
        icon = icon,
        title = title,
        message = message,
        okButtonContentDescription = okButtonContentDescription,
        cancelButtonContentDescription = cancelButtonContentDescription,
        state = columnState,
        showPositionIndicator = false,
        content = content,
    )
}
