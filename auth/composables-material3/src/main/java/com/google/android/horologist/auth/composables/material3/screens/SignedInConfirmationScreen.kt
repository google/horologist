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

package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.images.base.paintable.Paintable

private const val HORIZONTAL_PADDING_SCREEN_PERCENTAGE = 0.052f
private const val TOP_PADDING_SCREEN_PERCENTAGE = 0.012f
private const val BOTTOM_PADDING_SCREEN_PERCENTAGE = 0.092f
private const val EMAIL_PADDING_HORIZONTAL_SCREEN_PERCENTAGE = 0.092f

/**
 * A signed in confirmation dialog that can display the name, email and avatar image of the user.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120"/>
 */
@Composable
public fun SignedInConfirmationScreen(
    onDismissOrTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    name: String? = null,
    email: String? = null,
    avatar: Paintable? = null,
    defaultAvatar: Paintable = Icons.Default.AccountCircle.asPaintable(),
) {
    var showConfirmation by remember { mutableStateOf(true) }

    Dialog(
        showConfirmation,
        onDismissRequest = {
            showConfirmation = false
            onDismissOrTimeout()
        },
        modifier = modifier,
    ) {
        SignedInConfirmationDialogContent(
            modifier = modifier,
            name = name,
            email = email,
            avatar = avatar,
            defaultAvatar = defaultAvatar,
        )
    }
}

/**
 * A [SignedInConfirmationScreen] that can display the name, email and avatar image of an
 * [AccountUiModel].
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120"/>
 */
@Composable
public fun SignedInConfirmationScreen(
    onDismissOrTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    accountUiModel: AccountUiModel,
) {
    SignedInConfirmationScreen(
        onDismissOrTimeout = onDismissOrTimeout,
        modifier = modifier,
        name = accountUiModel.name,
        email = accountUiModel.email,
        avatar = accountUiModel.avatar,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SignedInConfirmationDialogContent(
    modifier: Modifier = Modifier,
    name: String? = null,
    email: String? = null,
    avatar: Paintable? = null,
    defaultAvatar: Paintable = Icons.Outlined.AccountCircle.asPaintable(),
) {
    val configuration = LocalConfiguration.current
    val topPadding = (configuration.screenHeightDp * TOP_PADDING_SCREEN_PERCENTAGE).dp
    val bottomPadding = (configuration.screenHeightDp * BOTTOM_PADDING_SCREEN_PERCENTAGE).dp
    val horizontalPadding = (configuration.screenWidthDp * HORIZONTAL_PADDING_SCREEN_PERCENTAGE).dp

    ScreenScaffold(timeText = {}) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = topPadding,
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = bottomPadding,
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val hasName = !name.isNullOrEmpty()
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(96.dp)
                    .clip(MaterialShapes.Pill.toShape())
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center,
            ) {
                if (avatar != null) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = avatar.rememberPainter(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                    )
                } else {
                    Icon(
                        painter = defaultAvatar.rememberPainter(),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp),
                    )
                }
            }

            // Title text
            Text(
                text = if (hasName) {
                    stringResource(
                        id = R.string.horologist_signedin_confirmation_greeting,
                        name!!,
                    )
                } else {
                    stringResource(id = R.string.horologist_signedin_confirmation_greeting_no_name)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.displayMedium,
            )

            email?.let {
                val emailHorizontalPadding =
                    (configuration.screenWidthDp * EMAIL_PADDING_HORIZONTAL_SCREEN_PERCENTAGE).dp
                val emailTextStyle = MaterialTheme.typography.bodyMedium.copy(
                    // linebreak specific to email strings
                    lineBreak = LineBreak(
                        strategy = LineBreak.Strategy.Balanced,
                        strictness = LineBreak.Strictness.Normal,
                        wordBreak = LineBreak.WordBreak.Default,
                    ),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = email,
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = emailHorizontalPadding,
                            end = emailHorizontalPadding,
                        )
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = emailTextStyle,
                )
            }
        }
    }
}
