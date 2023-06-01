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

package com.google.android.horologist.auth.composables.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.DialogDefaults
import coil.compose.rememberAsyncImagePainter
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.base.ui.components.ConfirmationDialog
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import java.time.Duration

private const val AVATAR_BACKGROUND_COLOR = 0xFF4ECDE6
private const val AVATAR_TEXT_COLOR = 0xFF202124
private const val BOTTOM_PADDING_SCREEN_PERCENTAGE = 0.094
private const val HORIZONTAL_PADDING_SCREEN_PERCENTAGE = 0.094

/**
 * A signed in confirmation dialog that can display the name, email and avatar image of the user.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120"/>
 */
@ExperimentalHorologistApi
@Composable
public fun SignedInConfirmationDialog(
    onDismissOrTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    name: String? = null,
    email: String? = null,
    avatar: Any? = null,
    duration: Duration = Duration.ofMillis(DialogDefaults.ShortDurationMillis)
) {
    ConfirmationDialog(
        onTimeout = onDismissOrTimeout,
        modifier = modifier,
        durationMillis = duration.toMillis()
    ) {
        SignedInConfirmationDialogContent(
            modifier = modifier,
            name = name,
            email = email,
            avatar = avatar
        )
    }
}

/**
 * A [SignedInConfirmationDialog] that can display the name, email and avatar image of an
 * [AccountUiModel].
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120"/>
 */
@ExperimentalHorologistApi
@Composable
public fun SignedInConfirmationDialog(
    onDismissOrTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    accountUiModel: AccountUiModel,
    duration: Duration = Duration.ofMillis(DialogDefaults.ShortDurationMillis)
) {
    SignedInConfirmationDialog(
        onDismissOrTimeout = onDismissOrTimeout,
        modifier = modifier,
        name = accountUiModel.name,
        email = accountUiModel.email,
        avatar = accountUiModel.avatar,
        duration = duration
    )
}

@ExperimentalHorologistApi
@Composable
internal fun SignedInConfirmationDialogContent(
    modifier: Modifier = Modifier,
    name: String? = null,
    email: String? = null,
    avatar: Any? = null
) {
    val configuration = LocalConfiguration.current
    val horizontalPadding = (configuration.screenWidthDp * HORIZONTAL_PADDING_SCREEN_PERCENTAGE).dp
    val bottomPadding = (configuration.screenHeightDp * BOTTOM_PADDING_SCREEN_PERCENTAGE).dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding)
            .padding(bottom = bottomPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val hasName = name != null
        val hasAvatar = avatar != null

        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = Color(AVATAR_BACKGROUND_COLOR), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (hasAvatar) {
                Image(
                    modifier = Modifier.clip(CircleShape),
                    painter = rememberAsyncImagePainter(model = avatar),
                    contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                    contentScale = ContentScale.Fit
                )
            } else if (hasName) {
                Text(
                    text = name!!.first().uppercase(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    color = Color(AVATAR_TEXT_COLOR),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = if (hasName) {
                stringResource(
                    id = R.string.horologist_signedin_confirmation_greeting,
                    name!!
                )
            } else {
                stringResource(id = R.string.horologist_signedin_confirmation_greeting_no_name)
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.title3
        )

        email?.let {
            Text(
                text = email,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.body2
            )
        }
    }
}
