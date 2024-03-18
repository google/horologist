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

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SecurityUpdateGood
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION

private const val TOP_PADDING_SCREEN_PERCENTAGE = 0.1248f
private const val BOTTOM_PADDING_SCREEN_PERCENTAGE = 0.0624f
private const val SIDE_PADDING_SCREEN_PERCENTAGE = 0.052f
private const val TEXT_PADDING_SCREEN_PERCENTAGE = 0.0416f
private val indicatorPadding = 8.dp
private val iconSize = 48.dp
private val progressBarStrokeWidth = 4.dp

@Composable
private fun ProgressCircle(modifier: Modifier) {
    Box(
        modifier = modifier
            .size(iconSize)
            .clip(CircleShape),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(iconSize - progressBarStrokeWidth + indicatorPadding),
            strokeWidth = progressBarStrokeWidth,
        )
        Icon(
            imageVector = Icons.Default.SecurityUpdateGood,
            contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
            modifier = Modifier
                .align(Alignment.Center)
                .size(iconSize - indicatorPadding - 8.dp)
                .clip(CircleShape),
        )
    }
}

/**
 * A screen to request the user to check their paired phone to proceed.
 * It also allows a [message] to be displayed.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/check_your_phone_screen_code.png"  height="120" width="120"/>
 */
@Composable
public fun CheckYourPhoneScreen(
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    val configuration = LocalConfiguration.current

    val isLarge = configuration.isLargeScreen

    val topPadding = (configuration.screenHeightDp * TOP_PADDING_SCREEN_PERCENTAGE).dp
    val bottomPadding = (configuration.screenHeightDp * BOTTOM_PADDING_SCREEN_PERCENTAGE).dp
    val sidePadding = (configuration.screenHeightDp * SIDE_PADDING_SCREEN_PERCENTAGE).dp
    val textPadding =
        if (isLarge) (configuration.screenHeightDp * TEXT_PADDING_SCREEN_PERCENTAGE).dp else 0.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = topPadding,
                bottom = bottomPadding,
                start = sidePadding,
                end = sidePadding,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = textPadding),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = R.string.horologist_check_your_phone_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            if (message != null) {
                Text(
                    text = message,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                )
            }
        }

        ProgressCircle(
            Modifier
                .align(Alignment.CenterHorizontally),
        )
    }
}

/** Whether the device is considered large screen for layout adjustment purposes. */
internal val Configuration.isLargeScreen: Boolean get() = screenHeightDp > 224
