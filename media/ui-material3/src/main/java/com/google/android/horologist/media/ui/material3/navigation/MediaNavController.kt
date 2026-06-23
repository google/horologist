/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.navigation

import androidx.navigation3.runtime.NavBackStack

/**
 * Domain focused NavController extensions that links to the screens of a typical Media app.
 */
@Suppress("UNCHECKED_CAST")
public object MediaNavController {

    /**
     * Navigate to a single collection such as a playlist.
     */
    public fun <T : MediaRoute> NavBackStack<T>.navigateToCollection(collectionId: String, collectionName: String) {
        add(CollectionRoute(collectionId, collectionName) as T)
    }

    /**
     * Navigate to a selections of collection such as a list of playlist.
     */
    public fun <T : MediaRoute> NavBackStack<T>.navigateToCollections() {
        add(CollectionsRoute as T)
    }

    /**
     * Navigate to the settings screen.
     */
    public fun <T : MediaRoute> NavBackStack<T>.navigateToSettings() {
        add(SettingsRoute as T)
    }

    /**
     * Navigate to a single media item, as part of a larger collection.
     */
    public fun <T : MediaRoute> NavBackStack<T>.navigateToMediaItem(mediaItemId: String, collectionId: String?) {
        add(MediaItemRoute(mediaItemId, collectionId) as T)
    }

    /**
     * Navigate to the player page, removing other entries from the backstack.
     */
    public fun <T : MediaRoute> NavBackStack<T>.navigateToPlayer() {
        clear()
        add(PlayerRoute(page = 0) as T)
    }

    /**
     * Navigate to the library page, removing other entries from the backstack.
     */
    public fun <T : MediaRoute> NavBackStack<T>.navigateToLibrary() {
        clear()
        add(PlayerRoute(page = 1) as T)
    }

    /**
     * Navigate to the volume screen.
     */
    public fun <T : MediaRoute> NavBackStack<T>.navigateToVolume() {
        add(VolumeRoute as T)
    }
}
