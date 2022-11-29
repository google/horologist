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
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnConfig
import com.google.android.horologist.compose.layout.ScalingLazyColumnConfigDefaults
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
import com.google.android.horologist.compose.rotaryinput.toRotaryScrollAdapter
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.sample.R
import com.google.android.horologist.sample.Screen

@Composable
fun AuthMenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
    config: ScalingLazyColumnConfig = ScalingLazyColumnConfigDefaults.rememberTopAlignedConfig()
) {
    ScalingLazyColumn(
        modifier = modifier,
        config = config
    ) {
        item {
            StandardChip(
                label = stringResource(id = R.string.auth_menu_oauth_pkce_item),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.AuthPKCEScreen.route) },
                chipType = StandardChipType.Primary
            )
        }
        item {
            StandardChip(
                label = stringResource(id = R.string.auth_menu_oauth_device_grant_item),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.AuthDeviceGrantScreen.route) },
                chipType = StandardChipType.Primary
            )
        }
        item {
            StandardChip(
                label = stringResource(id = R.string.auth_menu_google_sign_in_item),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.AuthGoogleSignInScreen.route) },
                chipType = StandardChipType.Primary
            )
        }
        item {
            StandardChip(
                label = stringResource(id = R.string.auth_menu_google_sign_out_item),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.AuthGoogleSignOutScreen.route) },
                chipType = StandardChipType.Primary
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun AuthMenuScreenPreview() {
    AuthMenuScreen(navigateToRoute = {})
}
