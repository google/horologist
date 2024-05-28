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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.AlertContent

@ExperimentalHorologistApi
@Composable
public fun NonScrollableAlertContent(
    onCancel: (() -> Unit)? = null,
    onOk: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: String? = null,
    message: String? = null,
    okButtonContentDescription: String = stringResource(android.R.string.ok),
    cancelButtonContentDescription: String = stringResource(android.R.string.cancel),
    showPositionIndicator: Boolean = true,
    content: (ScalingLazyListScope.() -> Unit)? = null,
) {
    val columnState = centeredColumnState()

    AlertContent(
        onCancel,
        onOk,
        icon,
        title,
        message,
        okButtonContentDescription,
        cancelButtonContentDescription,
        columnState,
        showPositionIndicator,
        content
    )
}

@Composable
private fun centeredColumnState() = rememberColumnState(
    ScalingLazyColumnDefaults.scalingLazyColumnDefaults(
        initialCenterIndex = 0,
        initialCenterOffset = 50,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        autoCentering = AutoCenteringParams(itemIndex = 0, itemOffset = 0)
    ),
)