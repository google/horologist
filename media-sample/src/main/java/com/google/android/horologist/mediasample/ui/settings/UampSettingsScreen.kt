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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.navigation.navigateToDeveloperOptions

@Composable
fun UampSettingsScreen(
    focusRequester: FocusRequester,
    state: ScalingLazyListState,
    settingsScreenViewModel: SettingsScreenViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val uiState by rememberStateWithLifecycle(settingsScreenViewModel.uiState)

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, state),
        state = state
    ) {
        item {
            Text(
                text = stringResource(id = R.string.sample_settings),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.title3
            )
        }
        item {
            ActionSetting(
                text = stringResource(id = R.string.sample_developer_options),
                icon = Icons.Default.DataObject,
                colors = ChipDefaults.secondaryChipColors(),
                onClick = { navController.navigateToDeveloperOptions() }
            )
        }
        item {
            ActionSetting(
                text = stringResource(id = R.string.logout),
                icon = Icons.Default.Logout,
                colors = ChipDefaults.secondaryChipColors(),
                enabled = false,
                onClick = { settingsScreenViewModel.logout() }
            )
        }
    }
}

@Composable
fun ActionSetting(
    text: String,
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

    Chip(
        onClick = onClick,
        label = labelParam,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = colors,
        icon = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = text)
            }
        },
        contentPadding = ChipDefaults.ContentPadding
    )
}
