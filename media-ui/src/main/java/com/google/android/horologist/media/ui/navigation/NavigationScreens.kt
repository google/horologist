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

/**
 * Navigation routes enum.
 */
public sealed class NavigationScreens(
    public val route: String
) {
    public object Player : NavigationScreens("player?page={page}") {
        public val player: String = "player?page=0"
        public val library: String = "player?page=1"
    }
    public object Volume : NavigationScreens("volume")
    public object MediaItem : NavigationScreens("mediaItem")
    public object Login : NavigationScreens("login")
    public object Settings : NavigationScreens("settings")
    public object Collections : NavigationScreens("collections")
    public object Collection : NavigationScreens("collection")
}
