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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.contentColorFor
import androidx.wear.compose.material.dialog.Confirmation
import androidx.wear.compose.material.dialog.Dialog
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

/**
 * This component is an alternative to [Alert], providing the following:
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
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    title: String = "",
    okButtonContentDescription: String = stringResource(android.R.string.ok),
    cancelButtonContentDescription: String = stringResource(android.R.string.cancel),
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onCancelButtonClick,
        scrollState = scalingLazyListState,
        modifier = modifier,
    ) {
        Alert(
            title = title,
            body = message,
            onCancelButtonClick = onCancelButtonClick,
            onOKButtonClick = onOKButtonClick,
            okButtonContentDescription = okButtonContentDescription,
            cancelButtonContentDescription = cancelButtonContentDescription,
        )
    }
}

@ExperimentalHorologistApi
@Composable
public fun Alert(
    title: String,
    body: String,
    onCancelButtonClick: () -> Unit,
    onOKButtonClick: () -> Unit,
    okButtonContentDescription: String,
    cancelButtonContentDescription: String,
) {
    Alert(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.title3,
            )
        },
        content = {
            Text(
                text = body,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
            )
        },
        negativeButton = {
            Button(
                imageVector = Icons.Default.Close,
                contentDescription = cancelButtonContentDescription,
                onClick = onCancelButtonClick,
                colors = ButtonDefaults.secondaryButtonColors(),
            )
        },
        positiveButton = {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = okButtonContentDescription,
                onClick = onOKButtonClick,
            )
        },
    )
}

@Composable
internal fun Alert(
    title: @Composable ColumnScope.() -> Unit,
    negativeButton: @Composable () -> Unit,
    positiveButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (ColumnScope.() -> Unit)? = null,
    columnState: ScalingLazyColumnState = ScalingLazyColumnDefaults.responsive().create(),
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    titleColor: Color = contentColor,
    iconColor: Color = contentColor,
    content: @Composable (ColumnScope.() -> Unit)? = null
) {
    DialogImpl(
        modifier = modifier,
        columnState = columnState,
        backgroundColor = backgroundColor,
    ) {
        if (icon != null) {
            item {
                DialogIconHeader(iconColor, content = icon)
            }
        }

        item {
            DialogTitle(titleColor, padding = DialogDefaults.TitlePadding, title)
        }

        if (content != null) {
            item {
                DialogBody(contentColor, content)
            }
        }

        // Buttons
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                negativeButton()
                Spacer(modifier = Modifier.width(DialogDefaults.ButtonSpacing))
                positiveButton()
            }
        }
    }
}

/**
 * Common Wear Material dialog implementation that offers a single content slot,
 * fills the screen and is scrollable by default if the content is taller than the viewport.
 */
@Composable
internal fun DialogImpl(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState,
    backgroundColor: Color,
    content: ScalingLazyListScope.() -> Unit
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        columnState = columnState,
        content = content
    )
}


/**
 * [DialogIconHeader] displays an icon at the top of the dialog
 * followed by the recommended spacing.
 *
 * @param iconColor [Color] in which to tint the icon.
 * @param content Slot for an icon.
 */
@Composable
private fun DialogIconHeader(
    iconColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(LocalContentColor provides iconColor) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(DialogDefaults.IconSpacing)
            )
        }
    }
}

/**
 * [DialogTitle] displays the title content in a dialog with the recommended padding.
 *
 * @param titleColor [Color] in which the title is displayed.
 * @param padding The padding around the title content.
 */
@Composable
private fun DialogTitle(
    titleColor: Color,
    padding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides titleColor,
        LocalTextStyle provides MaterialTheme.typography.title3
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content,
        )
    }
}

/**
 * [DialogBody] displays the body content in a dialog with recommended padding.
 */
@Composable
private fun DialogBody(
    bodyColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides bodyColor,
        LocalTextStyle provides MaterialTheme.typography.body2
    ) {
        Column(
            modifier = Modifier.padding(DialogDefaults.BodyPadding),
            content = content
        )
    }
}

/**
 * Contains the default values used by [Alert] and [Confirmation].
 */
internal object DialogDefaults {
    /**
     * Creates the recommended vertical arrangement for [Alert] dialog content.
     */
    public val AlertVerticalArrangement =
        Arrangement.spacedBy(4.dp, Alignment.CenterVertically)

    /**
     * The padding to apply around the contents.
     */
    public val ContentPadding = PaddingValues(horizontal = 10.dp)

    /**
     * Spacing between [Button]s.
     */
    internal val ButtonSpacing = 12.dp

    /**
     * Spacing below [Icon].
     */
    internal val IconSpacing = 4.dp

    /**
     * Padding around body content.
     */
    internal val BodyPadding
        @Composable get() =
            if (isRoundDevice())
                PaddingValues(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 12.dp)
            else
                PaddingValues(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 12.dp)

    /**
     * Padding around title text.
     */
    internal val TitlePadding
        @Composable get() =
            if (isRoundDevice())
                PaddingValues(
                    start = 14.dp,
                    end = 14.dp,
                    top = 0.dp,
                    bottom = 8.dp
                )
            else
                PaddingValues(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 8.dp)

    /**
     * Bottom padding for title text.
     */
    internal val TitleBottomPadding
        @Composable get() =
            PaddingValues(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 16.dp)

    @Composable
    internal fun isRoundDevice(): Boolean {
        val configuration = LocalConfiguration.current
        return remember(configuration) {
            configuration.isScreenRound
        }
    }
}
