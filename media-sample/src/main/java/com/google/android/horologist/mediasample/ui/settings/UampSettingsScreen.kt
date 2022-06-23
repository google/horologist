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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.mediasample.R

@Composable
fun UampSettingsScreen(
    focusRequester: FocusRequester,
    state: ScalingLazyListState,
    settingsScreenViewModel: SettingsScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by rememberStateWithLifecycle(settingsScreenViewModel.uiState)

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, state),
        state = state
    ) {
        item {
            CheckedSetting(
                uiState.podcastControls, stringResource(id = R.string.horologist_podcast_controls)
            ) {
                settingsScreenViewModel.setPodcastControls(it)
            }
        }
        item {
            CheckedSetting(
                uiState.loadItemsAtStartup, stringResource(id = R.string.horologist_load_items)
            ) {
                settingsScreenViewModel.setLoadItemsAtStartup(it)
            }
        }
        item {
            ActionSetting(stringResource(id = R.string.horologist_logout)) {
                settingsScreenViewModel.logout()
            }
        }
    }
}

@Composable
private fun ActionSetting(
    text: String,
    onClick: () -> Unit,
) {
    Chip(
        onClick = onClick, label = {
            Text(text)
        }, modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    private fun ToggleSetting(
        value: Boolean,
        text: String,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        ToggleChip(
            checked = value, toggleControl = {
                Icon(
                    imageVector = ToggleChipDefaults.radioIcon(checked = value),
                    contentDescription = if (value) stringResource(id = R.string.horologist_on) else stringResource(
                        id = R.string.horologist_off
                    ),
                )
            }, onCheckedChange = onCheckedChange, label = {
            Text(text)
        }, modifier = Modifier.fillMaxWidth()
            )
        }

        @Composable
        private fun CheckedSetting(
            value: Boolean,
            text: String,
            onCheckedChange: (Boolean) -> Unit,
        ) {
            ToggleChip(
                checked = value, toggleControl = {
                    Icon(
                        imageVector = ToggleChipDefaults.checkboxIcon(checked = value),
                        contentDescription = if (value) stringResource(id = R.string.horologist_on) else stringResource(
                            id = R.string.horologist_off
                        ),
                    )
                }, onCheckedChange = onCheckedChange, label = {
                Text(text)
            }, modifier = Modifier.fillMaxWidth()
                )
            }
            