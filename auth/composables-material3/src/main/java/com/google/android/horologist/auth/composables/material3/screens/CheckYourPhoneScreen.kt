/*
 * Copyright 2025 The Android Open Source Project
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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.CurvedScope
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OpenOnPhoneDialogColors
import androidx.wear.compose.material3.OpenOnPhoneDialogContent
import androidx.wear.compose.material3.OpenOnPhoneDialogDefaults
import androidx.wear.compose.material3.openOnPhoneDialogCurvedText
import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.auth.composables.material3.theme.HorologistMaterialTheme
import com.google.android.horologist.auth.composables.material3.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.layout.ScreenScaffold

/**
 * A screen to request the user to check their paired phone to proceed.
 * It also allows a [message] to be displayed.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/check_your_phone_screen_code.png"  height="120" width="120"/>
 */
@Composable
public fun CheckYourPhoneScreen(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.horologist_check_your_phone_title),
) {
    HorologistMaterialTheme {
        ScreenScaffold(timeText = {}) {
            val curvedTextStyle = MaterialTheme.typography.arcLarge
            val curvedText: (CurvedScope.() -> Unit) = {
                openOnPhoneDialogCurvedText(text = message, style = curvedTextStyle)
            }
            val a11yFullDurationMillis =
                LocalAccessibilityManager.current?.calculateRecommendedTimeoutMillis(
                    originalTimeoutMillis = OpenOnPhoneDialogDefaults.DurationMillis,
                    containsIcons = true,
                    containsText = true,
                    containsControls = false,
                ) ?: OpenOnPhoneDialogDefaults.DurationMillis
            OpenOnPhoneDialogContent(
                modifier = modifier.fillMaxSize(),
                curvedText = curvedText,
                durationMillis = a11yFullDurationMillis,
                colors = OpenOnPhoneDialogColors(
                    iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    progressIndicatorColor = MaterialTheme.colorScheme.primary,
                    progressTrackColor = MaterialTheme.colorScheme.onPrimary,
                    textColor = MaterialTheme.colorScheme.onBackground,
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.horologist_ic_check_your_phone),
                    contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                )
            }
        }
    }
}
