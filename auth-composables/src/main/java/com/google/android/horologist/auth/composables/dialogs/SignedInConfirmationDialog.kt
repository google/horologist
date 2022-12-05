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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.material.dialog.DialogDefaults
import coil.compose.rememberAsyncImagePainter
import com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.base.ui.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import kotlinx.coroutines.delay
import java.time.Duration

private const val AVATAR_BACKGROUND_COLOR = 0xFF4ECDE6
private const val AVATAR_TEXT_COLOR = 0xFF202124
private const val BOTTOM_PADDING_SCREEN_PERCENTAGE = 0.094
private const val HORIZONTAL_PADDING_SCREEN_PERCENTAGE = 0.094

/**
 * A signed in confirmation dialog that can display the name, email and avatar image of the user.
 */
@ExperimentalHorologistAuthComposablesApi
@Composable
public fun SignedInConfirmationDialog(
    showDialog: Boolean,
    onDismissOrTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    name: String? = null,
    email: String? = null,
    avatarUri: Any? = null,
    duration: Duration = Duration.ofMillis(DialogDefaults.ShortDurationMillis)
) {
    val currentOnDismissOrTimeout by rememberUpdatedState(onDismissOrTimeout)

    val accessibilityManager = LocalAccessibilityManager.current
    val durationMillis = remember(duration) {
        accessibilityManager?.calculateRecommendedTimeoutMillis(
            originalTimeoutMillis = duration.toMillis(),
            containsIcons = false,
            containsText = true,
            containsControls = false
        ) ?: duration.toMillis()
    }

    LaunchedEffect(durationMillis) {
        delay(durationMillis)
        currentOnDismissOrTimeout()
    }

    Dialog(
        showDialog = showDialog,
        onDismissRequest = currentOnDismissOrTimeout,
        modifier = modifier
    ) {
        SignedInConfirmationDialogContent(
            modifier = modifier,
            displayName = name,
            email = email,
            avatarUri = avatarUri
        )
    }
}

@ExperimentalHorologistAuthComposablesApi
@Composable
internal fun SignedInConfirmationDialogContent(
    modifier: Modifier = Modifier,
    displayName: String? = null,
    email: String? = null,
    avatarUri: Any? = null
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
        val hasName = displayName != null
        val hasAvatar = avatarUri != null

        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = Color(AVATAR_BACKGROUND_COLOR), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (hasAvatar) {
                Image(
                    modifier = Modifier.clip(CircleShape),
                    painter = rememberAsyncImagePainter(model = avatarUri),
                    contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                    contentScale = ContentScale.Fit
                )
            } else if (hasName) {
                Text(
                    text = displayName!!.first().uppercase(),
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
                    displayName!!
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
