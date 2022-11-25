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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.base.ui.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION

private val indicatorPadding = 8.dp
private val iconSize = 48.dp
private val progressBarStrokeWidth = 4.dp

@ExperimentalHorologistAuthComposablesApi
@Composable
public fun CheckYourPhoneScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.horologist_check_your_phone_title),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = modifier
                .padding(bottom = 20.dp)
                .align(Alignment.BottomCenter)
                .size(iconSize)
                .clip(CircleShape)
        ) {
            CircularProgressIndicator(
                modifier = modifier
                    .size(iconSize - progressBarStrokeWidth + indicatorPadding),
                strokeWidth = progressBarStrokeWidth
            )
            Icon(
                imageVector = Icons.Default.SecurityUpdateGood,
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(iconSize - indicatorPadding - 8.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@ExperimentalHorologistAuthComposablesApi
@Composable
public fun CheckYourPhoneScreen(
    modifier: Modifier = Modifier,
    message: String
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.horologist_check_your_phone_title),
            modifier = Modifier
                .padding(top = 60.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Text(
            text = message,
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth()
                .size(iconSize)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = modifier
                    .size(iconSize - progressBarStrokeWidth + indicatorPadding),
                strokeWidth = progressBarStrokeWidth
            )
            Icon(
                imageVector = Icons.Default.SecurityUpdateGood,
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(iconSize - indicatorPadding - 8.dp)
                    .clip(CircleShape)
            )
        }
    }
}
