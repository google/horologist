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

package com.google.android.horologist.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.base.ui.components.Title
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.composables.SectionedListScope
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.sample.R
import com.google.android.horologist.sample.Screen

@Composable
fun AuthMenuScreen(
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState
) {
    SectionedList(
        columnState = columnState,
        modifier = modifier
    ) {
        pkceSection(navigateToRoute)

        deviceGrantSection(navigateToRoute)

        googleSignInSection(navigateToRoute)
    }
}

private fun SectionedListScope.pkceSection(navigateToRoute: (String) -> Unit) {
    section(
        listOf(
            Pair(R.string.auth_menu_oauth_pkce_sign_in_item, Screen.PKCESignInPromptScreen.route),
            Pair(
                R.string.auth_menu_oauth_pkce_sign_out_item,
                Screen.PKCESignOutScreen.route
            )
        )
    ) {
        header {
            Title(stringResource(id = R.string.auth_menu_oauth_pkce_header))
        }
        loaded { (textId, route) ->
            StandardChip(
                label = stringResource(id = textId),
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(route) },
                chipType = StandardChipType.Primary
            )
        }
    }
}

private fun SectionedListScope.deviceGrantSection(navigateToRoute: (String) -> Unit) {
    section(
        listOf(
            Pair(
                R.string.auth_menu_oauth_device_grant_sign_in_item,
                Screen.DeviceGrantSignInPromptScreen.route
            ),
            Pair(
                R.string.auth_menu_oauth_device_grant_sign_out_item,
                Screen.DeviceGrantSignOutScreen.route
            )
        )
    ) {
        header {
            Title(stringResource(id = R.string.auth_menu_oauth_device_grant_header))
        }
        loaded { (textId, route) ->
            StandardChip(
                label = stringResource(id = textId),
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(route) },
                chipType = StandardChipType.Primary
            )
        }
    }
}

private fun SectionedListScope.googleSignInSection(navigateToRoute: (String) -> Unit) {
    section(
        listOf(
            Pair(
                R.string.auth_menu_google_sign_in_item,
                Screen.GoogleSignInPromptSampleScreen.route
            ),
            Pair(R.string.auth_menu_google_sign_out_item, Screen.GoogleSignOutScreen.route)
        )
    ) {
        header {
            Title(stringResource(id = R.string.auth_menu_google_sign_in_header))
        }
        loaded { (textId, route) ->
            StandardChip(
                label = stringResource(id = textId),
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(route) },
                chipType = StandardChipType.Primary
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun AuthMenuScreenPreview() {
    AuthMenuScreen(
        navigateToRoute = {},
        columnState = belowTimeTextPreview()
    )
}
