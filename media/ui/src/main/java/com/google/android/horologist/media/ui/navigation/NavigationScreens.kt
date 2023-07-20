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

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

/**
 * Navigation routes enum.
 */
public open class NavigationScreens(
    public val navRoute: String
) {
    public open val arguments: List<NamedNavArgument> = emptyList()

    public open fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = emptyList()

    public object Player : NavigationScreens("player?page={page}") {
        public fun playerDestination(): String = "player?page=0"
        public fun libraryDestination(): String = "player?page=1"

        override fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
            navDeepLink {
                uriPattern = "$deepLinkPrefix/player?page={page}"
            }
        )

        public fun getPageParam(backStack: NavBackStackEntry, remove: Boolean = false): Int? {
            val pageNumber = backStack.arguments?.getInt(page, -1) ?: -1
            if (remove) {
                backStack.arguments?.remove(page)
            }
            return if (pageNumber < 0) null else pageNumber
        }

        public val page: String = "page"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(page) {
                    type = NavType.IntType
                }
            )
    }

    public object Volume : NavigationScreens("volume") {
        public fun destination(): String = navRoute
    }

    public object MediaItem :
        NavigationScreens("mediaItem?id={id}&collectionId={collectionId}") {
        public val id: String = "id"
        public val collectionId: String = "collectionId"

        public fun destination(
            id: String,
            collectionId: String? = null
        ): String {
            var route = "mediaItem?id=$id"
            if (collectionId != null) {
                route += "&collectionId=$collectionId"
            }
            return route
        }

        override fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
            navDeepLink {
                uriPattern = "$deepLinkPrefix/mediaItem?id={id}&collectionId={collectionId}"
            }
        )

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(id) {
                    type = NavType.StringType
                },
                navArgument(collectionId) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
    }

    public object Login : NavigationScreens("login") {
        public fun destination(): String = navRoute
    }

    public object Settings : NavigationScreens("settings") {
        public fun destination(): String = navRoute
    }

    public object Collections : NavigationScreens("collections") {
        public fun destination(): String = navRoute
    }

    public object Collection : NavigationScreens("collection?id={id}&name={name}") {

        public const val id: String = "id"
        public const val name: String = "name"

        public fun destination(id: String, name: String): String = "collection?id=$id&name=$name"

        override fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
            navDeepLink {
                uriPattern = "$deepLinkPrefix/collection?id={id}&name={name}"
            }
        )

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(id) {
                    type = NavType.StringType
                },
                navArgument(name) {
                    type = NavType.StringType
                }
            )
    }
}
