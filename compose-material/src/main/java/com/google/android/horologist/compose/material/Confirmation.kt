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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.contentColorFor
import androidx.wear.compose.material.dialog.Confirmation
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.material.dialog.DialogDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberColumnState
import kotlinx.coroutines.delay

/**
 * This component is an alternative to [ConfirmationContent], providing the following:
 * - a convenient way of passing a title and an icon;
 * - duration;
 * - wrapped in a [Dialog];
 */
@ExperimentalHorologistApi
@Composable
public fun Confirmation(
    showDialog: Boolean,
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: String? = null,
    durationMillis: Long = DialogDefaults.ShortDurationMillis,
    columnState: ScalingLazyColumnState = rememberColumnState(
        ScalingLazyColumnDefaults.responsive(
            verticalArrangement = confirmationVerticalArrangement(),
            additionalPaddingAtBottom = 0.dp,
        ),
    ),
) {
    // Always refer to the latest inputs with which Confirmation was recomposed.
    val currentOnDismissed by rememberUpdatedState(onTimeout)

    val a11yDurationMillis = LocalAccessibilityManager.current?.calculateRecommendedTimeoutMillis(
        originalTimeoutMillis = durationMillis,
        containsIcons = icon != null,
        containsText = title != null,
        containsControls = false,
    ) ?: durationMillis

    LaunchedEffect(showDialog, a11yDurationMillis) {
        if (showDialog) {
            delay(a11yDurationMillis)
            currentOnDismissed()
        }
    }

    Dialog(
        showDialog = showDialog,
        onDismissRequest = currentOnDismissed,
        modifier = modifier,
        scrollState = columnState.state,
    ) {
        ConfirmationContent(
            icon = icon,
            title = title,
            columnState = columnState,
            showPositionIndicator = false,
        )
    }
}

@ExperimentalHorologistApi
@Composable
public fun ConfirmationContent(
    icon: @Composable (() -> Unit)? = null,
    title: String? = null,
    columnState: ScalingLazyColumnState = rememberColumnState(
        ScalingLazyColumnDefaults.responsive(
            verticalArrangement = confirmationVerticalArrangement(),
            additionalPaddingAtBottom = 0.dp,
        ),
    ),
    showPositionIndicator: Boolean = true,
) {
    ResponsiveDialogContent(
        icon = icon,
        title = title?.let {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        state = columnState,
        showPositionIndicator = showPositionIndicator,
    )
}

/**
 * A wrapper for [Confirmation] component, that calculates the value passed to [durationMillis] for
 * accessibility.
 *
 * This should be removed once https://issuetracker.google.com/issues/261385562 is addressed.
 */
@ExperimentalHorologistApi
@Composable
public fun Confirmation(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (ColumnScope.() -> Unit)? = null,
    scrollState: ScalingLazyListState = rememberScalingLazyListState(),
    durationMillis: Long = DialogDefaults.ShortDurationMillis,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    iconColor: Color = contentColor,
    verticalArrangement: Arrangement.Vertical = confirmationVerticalArrangement(),
    contentPadding: PaddingValues = DialogDefaults.ContentPadding,
    content: @Composable ColumnScope.() -> Unit,
) {
    val a11yDurationMillis = LocalAccessibilityManager.current?.calculateRecommendedTimeoutMillis(
        originalTimeoutMillis = durationMillis,
        containsIcons = false,
        containsText = true,
        containsControls = false,
    ) ?: durationMillis

    Confirmation(
        onTimeout = onTimeout,
        modifier = modifier,
        icon = icon,
        scrollState = scrollState,
        durationMillis = a11yDurationMillis,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        iconColor = iconColor,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
        content = content,
    )
}

private fun confirmationVerticalArrangement() = Arrangement.spacedBy(
    // NB an additional 4dp bottom padding is added after the icon
    // in ResponsiveDialogContent to make the 8dp in the UX spec.
    space = 4.dp,
    alignment = Alignment.CenterVertically,
)