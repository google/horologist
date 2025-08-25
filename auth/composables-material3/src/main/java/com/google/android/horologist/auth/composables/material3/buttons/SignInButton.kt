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

package com.google.android.horologist.auth.composables.material3.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonColors
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ButtonDefaults.ButtonHorizontalPadding
import androidx.wear.compose.material3.ButtonDefaults.ButtonVerticalPadding
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.horologist.auth.composables.material3.R

/**
 * An opinionated [Button] to represent the "Sign in" action.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables-material3/sign_in_button.png" height="120" width="120" >
 *
 * @sample com.google.android.horologist.auth.sample.screens.googlesignin.prompt.GoogleSignInPromptSampleScreen
 */
@Composable
public fun SignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.horologist_sign_in_chip_label),
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        colors = colors,
        contentPadding = PaddingValues(
            start = ButtonHorizontalPadding,
            top = ButtonVerticalPadding,
            end = ButtonHorizontalPadding,
            bottom = ButtonVerticalPadding,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}
