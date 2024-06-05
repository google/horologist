/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.media.ui.navigation

import androidx.navigation.NavController
import java.net.URLEncoder

/**
 * Domain focused NavController extensions that links to the screens of a typical Media app.
 */
public object MediaNavController {

    /**
     * Navigate to the player page, removing other entries from the backstack.
     */
    public fun NavController.navigateToPlayer() {
        navigate(NavigationScreen.Player(0)) {
            popUpTo(NavigationScreen.Player) {
                inclusive = true
                saveState = false
            }
        }
    }

    /**
     * Navigate to the library page, removing other entries from the backstack.
     */
    public fun NavController.navigateToLibrary() {
        navigate(NavigationScreen.Player(1)) {
            popUpTo(NavigationScreen.Player) {
                inclusive = true
                saveState = false
            }
        }
    }

}
