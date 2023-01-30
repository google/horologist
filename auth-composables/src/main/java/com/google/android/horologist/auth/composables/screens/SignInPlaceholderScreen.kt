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

package com.google.android.horologist.auth.composables.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi
import com.google.android.horologist.auth.composables.R

private const val AVATAR_BACKGROUND_COLOR = 0xFF3C4043
private const val BOTTOM_PADDING_SCREEN_PERCENTAGE = 0.094
private const val HORIZONTAL_PADDING_SCREEN_PERCENTAGE = 0.094

/**
 * A screen to represent a
 * [signing-in process state](https://developer.android.com/training/wearables/design/sign-in#confirmations).
 */
@ExperimentalHorologistAuthComposablesApi
@Composable
public fun SignInPlaceholderScreen(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.horologist_signin_placeholder_message)
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
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = Color(AVATAR_BACKGROUND_COLOR),
                    shape = CircleShape
                )
        )

        Text(
            text = message,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.title3
        )
    }
}
