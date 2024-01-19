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
    onOkButtonClick: (() -> Unit)? = null,
    onCancelButtonClick: (() -> Unit)? = null,
    okButtonContentDescription: String = stringResource(R.string.ok),
    cancelButtonContentDescription: String = stringResource(R.string.cancel),
    state: ScalingLazyColumnState = rememberColumnState(responsive(firstItemIsFullWidth = icon == null)),
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
                            SidePaddingExtraPct(
                                8.84f,
                                Modifier.padding(bottom = 8.dp), // 12.dp bellow icon
                            ) { it() }
                        }
                    }
                }
                content?.let {
                    if (icon == null && title == null) {
                        // Ensure the content is visible when there is nothing above it.
                        item {
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                    it()
                }
                if (onOkButtonClick != null || onCancelButtonClick != null) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = if (content != null) 12.dp else 0.dp),
                            horizontalArrangement = spacedBy(
                                4.dp,
                                Alignment.CenterHorizontally,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            onCancelButtonClick?.let {
                                Button(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = cancelButtonContentDescription,
                                    onClick = it,
                                    colors = ButtonDefaults.secondaryButtonColors(),
                                )
                            }
                            onOkButtonClick?.let {
                                Button(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = okButtonContentDescription,
                                    onClick = onOkButtonClick,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Surrounds a composable with padding at the start and end, expressed as a percentage of the
 * available width.
 *
 * @param paddingPct how much padding to add to each side, expressed as a percentage.
 * @param basePaddingPct how much padding has already being applied (expressed as a percentage).
 * This is used to simplify calculations when multiple nested padding need to be applied.
 * @param content the content to show inside the padding.
 */
@ExperimentalHorologistApi
@Composable
internal fun SidePaddingExtraPct(
    paddingPct: Float,
    modifier: Modifier = Modifier,
    basePaddingPct: Float = 5.2f,
    content: @Composable () -> Unit,
) {
    val extraPadding = paddingPct / (100f - 2 * basePaddingPct)
    Row(
        modifier.fillMaxWidth(),
    ) {
        Spacer(Modifier.weight(extraPadding))
        Box(Modifier.weight(1f - 2 * extraPadding)) {
            content()
        }
        Spacer(Modifier.weight(extraPadding))
    }
}
