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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CheckboxButton
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListHeaderDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.google.android.horologist.media.ui.material3.navigation.CustomRoute
import com.google.android.horologist.media.ui.material3.navigation.MediaRoute
import com.google.android.horologist.media.ui.material3.navigation.PlayerRoute
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.DeveloperOptions
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.GoogleSignInScreen
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.GoogleSignOutScreen

@Composable
fun UampSettingsScreen(
    viewModel: SettingsScreenViewModel,
    backStack: NavBackStack<MediaRoute>,
    modifier: Modifier = Modifier,
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val transformationSpec = rememberTransformationSpec()
    val columnState = rememberTransformingLazyColumnState()

    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier,
    ) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding,
        ) {
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Text(text = stringResource(id = R.string.sample_settings))
                }
            }
            item {
                if (screenState.authUser == null) {
                    FilledTonalButton(
                        label = { Text(stringResource(id = R.string.login)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec)
                            .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                        onClick = {
                            backStack.add(CustomRoute(GoogleSignInScreen.navRoute))
                        },
                        enabled = !screenState.guestMode,
                        transformation = SurfaceTransformation(transformationSpec),
                    )
                } else {
                    FilledTonalButton(
                        label = { Text(stringResource(id = R.string.logout)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec)
                            .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                        onClick = {
                            backStack.clear()
                            backStack.add(PlayerRoute(page = 0))
                            backStack.add(CustomRoute(GoogleSignOutScreen.navRoute))
                        },
                        transformation = SurfaceTransformation(transformationSpec),
                    )
                }
            }
            item {
                CheckedSetting(
                    value = screenState.guestMode,
                    text = stringResource(id = R.string.sample_guest_mode),
                    enabled = screenState.writable,
                    transformation = SurfaceTransformation(transformationSpec),
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                ) {
                    viewModel.setGuestMode(it)
                }
            }
            if (screenState.showDeveloperOptions) {
                item {
                    ActionSetting(
                        text = stringResource(id = R.string.sample_developer_options),
                        icon = Icons.Default.DataObject,
                        transformation = SurfaceTransformation(transformationSpec),
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec)
                            .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                        onClick = {
                            backStack.add(CustomRoute(DeveloperOptions.navRoute))
                        },
                    )
                }
            }
            item {
                val activity = LocalActivity.current
                FilledTonalButton(
                    label = { Text(stringResource(id = R.string.show_licenses)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                    onClick = {
                        activity?.startActivity(
                            Intent().apply {
                                setPackage(activity.packageName)
                                setAction("com.google.wear.ACTION_SHOW_LICENSE")
                            },
                        )
                    },
                    enabled = true,
                    transformation = SurfaceTransformation(transformationSpec),
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
    transformation: SurfaceTransformation? = null,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        label = {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
        },
        enabled = enabled,
        modifier = modifier,
        icon = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = text)
            }
        },
        transformation = transformation,
    )
}

@Composable
fun CheckedSetting(
    value: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    transformation: SurfaceTransformation? = null,
    onCheckedChange: (Boolean) -> Unit,
) {
    CheckboxButton(
        checked = value,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        label = {
            Text(text)
        },
        modifier = modifier,
        transformation = transformation,
    )
}
