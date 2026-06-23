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

package com.google.android.horologist.mediasample.ui.navigation

import com.google.android.horologist.media.ui.material3.navigation.NavigationScreens

object UampNavigationScreen {

    public data object AudioDebug : NavigationScreens("audioDebug")

    public data object Samples : NavigationScreens("samples")

    public data object GoogleSignInPromptScreen : NavigationScreens("googleSignInPromptScreen")

    public data object GoogleSignInScreen : NavigationScreens("googleSignInScreen")

    public data object GoogleSignOutScreen : NavigationScreens("googleSignOutScreen")

    public data object DeveloperOptions : NavigationScreens("developerOptions")

    public data object NewHotness : NavigationScreens("newHotness")
}
