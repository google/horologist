/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.auth.sample.screens.common.streamline

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.sample.R
import com.google.android.horologist.auth.sample.Screen
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader

@Composable
fun StreamlineSignInMenuScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    )

    ScreenScaffold(scrollState = columnState) {
        SectionedList(
            columnState = columnState,
            modifier = modifier.fillMaxSize(),
        ) {
            section(
                listOf(
                    Triple(
                        R.string.common_screens_streamline_sign_in_single_account_item,
                        Screen.StreamlineSignInSampleScreen.route,
                        AuthUserRepositoryStreamlineImpl.Mode.SINGLE_ACCOUNT_AVAILABLE,
                    ),
                    Triple(
                        R.string.common_screens_streamline_sign_in_multiple_accounts_item,
                        Screen.StreamlineSignInSampleScreen.route,
                        AuthUserRepositoryStreamlineImpl.Mode.MULTIPLE_ACCOUNTS_AVAILABLE,
                    ),
                    Triple(
                        R.string.common_screens_streamline_sign_in_no_accounts_item,
                        Screen.StreamlineSignInSampleScreen.route,
                        AuthUserRepositoryStreamlineImpl.Mode.NO_ACCOUNTS_AVAILABLE,
                    ),
                ),
            ) {
                header {
                    ResponsiveListHeader(contentPadding = firstItemPadding()) {
                        Text(stringResource(id = R.string.common_screens_streamline_sign_in_header), modifier = Modifier.listTextPadding())
                    }
                }
                loaded { (textId, route, mode) ->
                    Chip(
                        label = stringResource(id = textId),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            AuthUserRepositoryStreamlineImpl.mode = mode
                            navController.navigate(route)
                        },
                    )
                }
            }
        }
    }
}
