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

package com.google.android.horologist.mediasample.ui.settings

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ChipType
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.navigation.navigateToDeveloperOptions
import com.google.android.horologist.mediasample.ui.navigation.navigateToGoogleSignIn
import com.google.android.horologist.mediasample.ui.navigation.navigateToGoogleSignOutScreen

@Composable
fun UampSettingsScreen(
    columnState: ScalingLazyColumnState,
    viewModel: SettingsScreenViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            ListHeader {
                Text(text = stringResource(id = R.string.sample_settings))
            }
        }
        item {
            if (screenState.authUser == null) {
                Chip(
                    label = stringResource(id = R.string.login),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigateToGoogleSignIn() },
                    chipType = ChipType.Primary,
                    enabled = !screenState.guestMode
                )
            } else {
                Chip(
                    label = stringResource(id = R.string.logout),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigateToGoogleSignOutScreen() },
                    chipType = ChipType.Primary
                )
            }
        }
        item {
            CheckedSetting(
                screenState.guestMode,
                stringResource(id = R.string.sample_guest_mode),
                enabled = screenState.writable
            ) {
                viewModel.setGuestMode(it)
            }
        }
        if (screenState.showDeveloperOptions) {
            item {
                ActionSetting(
                    text = stringResource(id = R.string.sample_developer_options),
                    icon = Icons.Default.DataObject,
                    colors = ChipDefaults.secondaryChipColors(),
                    onClick = { navController.navigateToDeveloperOptions() }
                )
            }
        }
    }
}

@Composable
fun ActionSetting(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    onClick: () -> Unit
) {
    val hasIcon = icon != null
    val labelParam: (@Composable RowScope.() -> Unit) =
        {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                textAlign = if (hasIcon) TextAlign.Left else TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }

    androidx.wear.compose.material.Chip(
        onClick = onClick,
        label = labelParam,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        colors = colors,
        icon = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = text)
            }
        },
        contentPadding = ChipDefaults.ContentPadding
    )
}

@Composable
fun CheckedSetting(
    value: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    ToggleChip(
        checked = value,
        toggleControl = {
            Icon(
                imageVector = ToggleChipDefaults.checkboxIcon(checked = value),
                contentDescription = if (value) stringResource(id = R.string.on) else stringResource(
                    id = R.string.off
                )
            )
        },
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        label = {
            Text(text)
        },
        modifier = modifier.fillMaxWidth()
    )
}
