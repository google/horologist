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
    private const val UTF_8 = "UTF-8"

    /**
     * Navigate to a single collection such as a playlist.
     */
    public fun NavController.navigateToCollection(collectionId: String, collectionName: String) {
        navigate(
            NavigationScreens.Collection.destination(
                collectionId,
                URLEncoder.encode(collectionName, UTF_8)
            )
        )
    }

    /**
     * Navigate to a selections of collection such as a list of playlist.
     */
    public fun NavController.navigateToCollections() {
        navigate(NavigationScreens.Collections.destination())
    }

    /**
     * Navigate to the settings screen.
     */
    public fun NavController.navigateToSettings() {
        navigate(NavigationScreens.Settings.destination())
    }

    /**
     * Navigate to a single media item, as part of a larger collection.
     */
    public fun NavController.navigateToMediaItem(mediaItemId: String, collectionId: String?) {
        navigate(NavigationScreens.MediaItem.destination(mediaItemId, collectionId))
    }

    /**
     * Navigate to the player page, removing other entries from the backstack.
     */
    public fun NavController.navigateToPlayer() {
        navigate(NavigationScreens.Player.playerDestination()) {
            popUpTo(NavigationScreens.Player.navRoute) {
                inclusive = true
                saveState = false
            }
        }
    }

    /**
     * Navigate to the library page, removing other entries from the backstack.
     */
    public fun NavController.navigateToLibrary() {
        navigate(NavigationScreens.Player.libraryDestination()) {
            popUpTo(NavigationScreens.Player.navRoute) {
                inclusive = true
                saveState = false
            }
        }
    }

    /**
     * Navigate to the volume screen.
     */
    public fun NavController.navigateToVolume() {
        navigate(NavigationScreens.Volume.destination())
    }
}
