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

import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable

/**
 * Navigation routes enum.
 */
public interface NavigationScreen {
    @Serializable
    public data class Player(val page: Int? = null) : NavigationScreen {
        public companion object {
            public fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
                navDeepLink {
                    uriPattern = "$deepLinkPrefix/player?page={page}"
                },
            )

            public const val Player: Int = 0
            public const val Library: Int = 1
        }
    }

    @Serializable
    public data object Volume : NavigationScreen {
        public fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
            navDeepLink {
                uriPattern = "$deepLinkPrefix/volume"
            },
        )
    }

    @Serializable
    public data class MediaItem(val id: String, val collectionId: String?) : NavigationScreen {
        public companion object {
            public fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
                navDeepLink {
                    uriPattern = "$deepLinkPrefix/mediaItem?id={id}&collectionId={collectionId}"
                },
            )
        }
    }

    @Serializable
    public data object Settings : NavigationScreen {
        public fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
            navDeepLink {
                uriPattern = "$deepLinkPrefix/settings"
            },
        )
    }

    @Serializable
    public data object Collections : NavigationScreen {
        public fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
            navDeepLink {
                uriPattern = "$deepLinkPrefix/collections"
            },
        )
    }

    @Serializable
    public data class Collection(val id: String, val name: String? = null) : NavigationScreen {
        public companion object {
            public fun deepLinks(deepLinkPrefix: String): List<NavDeepLink> = listOf(
                navDeepLink {
                    uriPattern = "$deepLinkPrefix/collection?id={id}&name={name}"
                },
            )
        }
    }
}
