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

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.RowScope
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
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.SwitchButton
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.ButtonDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.media.ui.navigation.NavigationScreen
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.DeveloperOptions
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.GoogleSignInScreen
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.GoogleSignOutScreen

@Composable
fun UampSettingsScreen(
    viewModel: SettingsScreenViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = modifier,
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = stringResource(id = R.string.sample_settings))
                }
            }
            item {
                if (screenState.authUser == null) {
                    FilledTonalButton(
                        label = { Text(stringResource(id = R.string.login)) },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(GoogleSignInScreen)
                        },
                        enabled = !screenState.guestMode,
                    )
                } else {
                    FilledTonalButton(
                        label = { Text(stringResource(id = R.string.logout)) },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(GoogleSignOutScreen) {
                                popUpTo<NavigationScreen.Player>()
                            }
                        },
                    )
                }
            }
            item {
                CheckedSetting(
                    screenState.guestMode,
                    stringResource(id = R.string.sample_guest_mode),
                    enabled = screenState.writable,
                ) {
                    viewModel.setGuestMode(it)
                }
            }
            if (screenState.showDeveloperOptions) {
                item {
                    ActionSetting(
                        text = stringResource(id = R.string.sample_developer_options),
                        icon = Icons.Default.DataObject,
                        onClick = {
                            navController.navigate(DeveloperOptions)
                        },
                    )
                }
            }
            item {
                val activity = LocalActivity.current
                Chip(
                    label = stringResource(id = R.string.show_licenses),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        activity?.startActivity(
                            Intent().apply {
                                setPackage(activity.packageName)
                                setAction("com.google.wear.ACTION_SHOW_LICENSE")
                            },
                        )
                    },
                    enabled = true,
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
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        label = {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                textAlign = if (icon != null) TextAlign.Left else TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
        },
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        icon = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = text)
            }
        },
    )
}

@Composable
fun CheckedSetting(
    value: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    SwitchButton(
        checked = value,
        onCheckedChange = onCheckedChange,
        label = {
            Text(text)
        },
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
    )
}
