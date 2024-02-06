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

package com.google.android.horologist.compose.material

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.responsive
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState

@ExperimentalHorologistApi
@Composable
public fun ResponsiveDialogContent(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    message: @Composable (() -> Unit)? = null,
    onOk: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    okButtonContentDescription: String = stringResource(R.string.ok),
    cancelButtonContentDescription: String = stringResource(R.string.cancel),
    state: ScalingLazyColumnState =
        rememberColumnState(responsive(firstItemIsFullWidth = icon == null)),
    showPositionIndicator: Boolean = true,
    content: (ScalingLazyListScope.() -> Unit)? = null,
) {
    ScreenScaffold(
        modifier = modifier.fillMaxSize(),
        scrollState = if (showPositionIndicator) state else null,
        timeText = {},
    ) {
        // This will be applied only to the content.
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.body2,
        ) {
            ScalingLazyColumn(columnState = state) {
                icon?.let {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp), // 8.dp bellow icon
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            it()
                        }
                    }
                }
                title?.let {
                    item {
                        CompositionLocalProvider(
                            LocalTextStyle provides MaterialTheme.typography.title3,
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(titleMaxWidthFraction)
                                    .padding(bottom = 8.dp), // 12.dp below icon
                            ) { it() }
                        }
                    }
                }
                if (icon == null && title == null) {
                    // Ensure the content is visible when there is nothing above it.
                    item {
                        Spacer(Modifier.height(20.dp))
                    }
                }
                message?.let {
                    item {
                        Box(
                            Modifier.fillMaxWidth(messageMaxWidthFraction),
                        ) { it() }
                    }
                }
                content?.let {
                    it()
                }
                if (onOk != null || onCancel != null) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = if (content != null || message != null) 12.dp else 0.dp,
                                ),
                            horizontalArrangement = spacedBy(
                                4.dp,
                                Alignment.CenterHorizontally,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            onCancel?.let {
                                Button(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = cancelButtonContentDescription,
                                    onClick = it,
                                    colors = ButtonDefaults.secondaryButtonColors(),
                                )
                            }
                            onOk?.let {
                                Button(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = okButtonContentDescription,
                                    onClick = onOk,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

internal const val globalHorizontalPadding = 5.2f
internal const val messageExtraHorizontalPadding = 4.16f
internal const val titleExtraHorizontalPadding = 8.84f

// Fraction of the max available width that message should take (after global and message padding)
internal val messageMaxWidthFraction = 1f - 2f * calculatePaddingFraction(
    messageExtraHorizontalPadding,
)

// Fraction of the max available width that title should take (after global and message padding)
internal val titleMaxWidthFraction = 1f - 2f * calculatePaddingFraction(
    titleExtraHorizontalPadding,
)

// Calculate total padding given global padding and additional padding required inside that.
internal fun calculatePaddingFraction(extraPadding: Float) =
    extraPadding / (100f - 2f * globalHorizontalPadding)
