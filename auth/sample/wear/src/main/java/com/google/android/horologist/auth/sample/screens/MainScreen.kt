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

package com.google.android.horologist.auth.sample.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.sample.R
import com.google.android.horologist.auth.sample.Screen
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.composables.SectionedListScope
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.compose.material.Title

@Composable
fun MainScreen(
    navigateToRoute: (String) -> Unit,
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
            googleSignInSection(navigateToRoute)

            tokenShareSection(navigateToRoute)

            commonScreensSection(navigateToRoute)
        }
    }
}

private fun SectionedListScope.googleSignInSection(navigateToRoute: (String) -> Unit) {
    section(
        listOf(
            Pair(
                R.string.auth_menu_google_sign_in_prompt_item,
                Screen.GoogleSignInPromptSampleScreen.route,
            ),
            Pair(R.string.auth_menu_google_sign_out_item, Screen.GoogleSignOutScreen.route),
        ),
    ) {
        header {
            ResponsiveListHeader(contentPadding = firstItemPadding()) {
                Text(stringResource(id = R.string.auth_menu_google_sign_in_header), modifier = Modifier.listTextPadding())
            }
        }
        loaded { (textId, route) ->
            Chip(
                label = stringResource(id = textId),
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(route) },
            )
        }
    }
}

private fun SectionedListScope.tokenShareSection(navigateToRoute: (String) -> Unit) {
    section(
        listOf(
            Pair(
                R.string.auth_menu_token_share_default_key_item,
                Screen.TokenShareDefaultKeyScreen.route,
            ),
            Pair(
                R.string.auth_menu_token_share_custom_key_item,
                Screen.TokenShareCustomKeyScreen.route,
            ),
        ),
    ) {
        header {
            ResponsiveListHeader(contentPadding = firstItemPadding()) {
                Text(stringResource(id = R.string.auth_menu_token_share_header), modifier = Modifier.listTextPadding())
            }
        }
        loaded { (textId, route) ->
            Chip(
                label = stringResource(id = textId),
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(route) },
            )
        }
    }
}

private fun SectionedListScope.commonScreensSection(navigateToRoute: (String) -> Unit) {
    section(
        listOf(
            Pair(
                R.string.auth_menu_common_screens_streamline_sign_in_item,
                Screen.StreamlineSignInMenuScreen.route,
            ),
        ),
    ) {
        header {
            Title(stringResource(id = R.string.auth_menu_common_screens_header))
        }
        loaded { (textId, route) ->
            Chip(
                label = stringResource(id = textId),
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(route) },
            )
        }
    }
}

@Suppress("unused")
@WearPreviewDevices
@Composable
fun AuthMenuScreenPreview() {
    MainScreen(
        navigateToRoute = {},
    )
}
