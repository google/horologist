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

<<<<<<< HEAD
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.contentColorFor
import androidx.wear.compose.material.dialog.Confirmation
import androidx.wear.compose.material.dialog.DialogDefaults
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.base.ui.ExperimentalHorologistBaseUiApi

/**
 * A wrapper for [Confirmation] component, that calculates the value passed to [durationMillis] for
 * accessibility.
 *
 * This should be removed once https://issuetracker.google.com/issues/261385562 is addressed.
 */
@ExperimentalHorologistBaseUiApi
=======
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

>>>>>>> 11be7a4f (Merge to latest)
@Composable
public fun ConfirmationDialog(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (ColumnScope.() -> Unit)? = null,
    scrollState: ScalingLazyListState = rememberScalingLazyListState(),
    durationMillis: Long = DialogDefaults.ShortDurationMillis,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    iconColor: Color = contentColor,
    verticalArrangement: Arrangement.Vertical = DialogDefaults.ConfirmationVerticalArrangement,
    contentPadding: PaddingValues = DialogDefaults.ContentPadding,
    content: @Composable ColumnScope.() -> Unit
) {
<<<<<<< HEAD
    val a11yDurationMillis = LocalAccessibilityManager.current?.calculateRecommendedTimeoutMillis(
        originalTimeoutMillis = durationMillis,
        containsIcons = false,
        containsText = true,
        containsControls = false
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
        content = content
=======
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onCancelButtonClick,
        scrollState = scalingLazyListState
    ) {
        ConfirmationDialogAlert(
            prompt = prompt,
            proceedText = proceedText,
            cancelText = cancelText,
            onCancelButtonClick = onCancelButtonClick,
            onProceedButtonClick = onProceedButtonClick
        )
    }
}

@Composable
public fun ConfirmationDialogAlert(
    prompt: String,
    proceedText: String,
    cancelText: String,
    onCancelButtonClick: () -> Unit,
    onProceedButtonClick: () -> Unit
) {
    Alert(
        title = {
            Text(
                text = prompt,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.title3,
            )
        },
        negativeButton = {
            StandardButton(
                imageVector = Icons.Default.Close,
                contentDescription = cancelText,
                onClick = onCancelButtonClick,
                buttonType = StandardButtonType.Secondary,
            )
        },
        positiveButton = {
            StandardButton(
                imageVector = Icons.Default.Check,
                contentDescription = proceedText,
                onClick = onProceedButtonClick,
            )
        }
>>>>>>> 11be7a4f (Merge to latest)
    )
}
