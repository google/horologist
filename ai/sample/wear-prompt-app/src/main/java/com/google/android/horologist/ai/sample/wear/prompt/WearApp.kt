/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.ai.sample.wear.prompt

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.ai.sample.wear.prompt.prompt.SamplePromptScreen
import com.google.android.horologist.ai.sample.wear.prompt.settings.SettingsScreen
import com.google.android.horologist.compose.nav.SwipeDismissableNavHost
import com.google.android.horologist.compose.nav.composable

@Composable
fun WearApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    AppScaffold(modifier = modifier) {
        SwipeDismissableNavHost(
            startDestination = Prompt,
            navController = navController,
        ) {
            composable<Prompt> {
                SamplePromptScreen(
                    onSettingsClick = { navController.navigate(Settings) },
                )
            }
            composable<Settings> {
                SettingsScreen()
            }
        }
    }
}

// There is deliberately no `@Preview` of `WearApp()`. Its nav host resolves `SamplePromptScreen`,
// which reaches for `hiltViewModel()`, and there is no Hilt-enabled activity behind a preview — so
// the capture produces no image at all and fails the render outright. The same fix as
// `:ai:sample:wear-gemini`: preview the screens, which already have stateless overloads and their
// own previews in `prompt/SamplePromptScreen.kt` and `settings/SettingsScreen.kt`, so nothing is
// lost by dropping this one.
