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

package com.google.android.horologist.datalayer.sample.screens.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.horologist.datalayer.sample.R
import com.google.android.horologist.datalayer.sample.screens.AppHelperNodes
import com.google.android.horologist.datalayer.sample.screens.AppHelperNodesListener
import com.google.android.horologist.datalayer.sample.screens.Counter
import com.google.android.horologist.datalayer.sample.screens.InstallAppCustomPromptDemo
import com.google.android.horologist.datalayer.sample.screens.InstallAppPromptDemo
import com.google.android.horologist.datalayer.sample.screens.InstallTileCustomPromptDemo
import com.google.android.horologist.datalayer.sample.screens.InstallTilePromptDemo
import com.google.android.horologist.datalayer.sample.screens.ReEngageCustomPromptDemo
import com.google.android.horologist.datalayer.sample.screens.ReEngagePromptDemo
import com.google.android.horologist.datalayer.sample.screens.SignInCustomPromptDemo
import com.google.android.horologist.datalayer.sample.screens.SignInPromptDemo

@Composable
fun MenuScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(id = R.string.menu_screen_apphelper_header))

        Button(onClick = { navController.navigate(AppHelperNodes) }) {
            Text(text = stringResource(id = R.string.menu_screen_nodes_item))
        }

        Button(onClick = { navController.navigate(AppHelperNodesListener) }) {
            Text(text = stringResource(id = R.string.menu_screen_nodes_listener_item))
        }

        Text(
            text = stringResource(id = R.string.menu_screen_inapp_prompts_header),
            modifier = Modifier.padding(top = 10.dp),
        )

        Button(onClick = { navController.navigate(InstallAppPromptDemo) }) {
            Text(text = stringResource(id = R.string.menu_screen_install_app_demo_item))
        }

        Button(onClick = { navController.navigate(ReEngagePromptDemo) }) {
            Text(text = stringResource(id = R.string.menu_screen_reengage_demo_item))
        }

        Button(onClick = { navController.navigate(SignInPromptDemo) }) {
            Text(text = stringResource(id = R.string.menu_screen_signin_demo_item))
        }

        Button(onClick = { navController.navigate(InstallTilePromptDemo) }) {
            Text(text = stringResource(id = R.string.menu_screen_install_tile_demo_item))
        }

        Button(onClick = { navController.navigate(InstallAppCustomPromptDemo) }) {
            Text(text = stringResource(id = R.string.menu_screen_install_app_custom_demo_item))
        }

        Button(onClick = { navController.navigate(ReEngageCustomPromptDemo) }) {
            Text(text = stringResource(id = R.string.menu_screen_reengage_custom_demo_item))
        }

        Button(onClick = { navController.navigate(SignInCustomPromptDemo) }) {
            Text(text = stringResource(id = R.string.menu_screen_signin_custom_demo_item))
        }

        Button(onClick = { navController.navigate(InstallTileCustomPromptDemo) }) {
            Text(text = stringResource(id = R.string.menu_screen_install_tile_custom_demo_item))
        }

        Text(
            text = stringResource(id = R.string.menu_screen_datalayer_header),
            modifier = Modifier.padding(top = 10.dp),
        )

        Button(onClick = { navController.navigate(Counter) }) {
            Text(text = stringResource(id = R.string.menu_screen_counter_item))
        }
    }
}
