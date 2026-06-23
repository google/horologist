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

package com.google.android.horologist.mediasample.ui.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.google.android.horologist.media.ui.material3.navigation.CustomRoute
import com.google.android.horologist.media.ui.material3.navigation.MediaRoute
import com.google.android.horologist.media.ui.material3.navigation.PlayerRoute
import com.google.android.horologist.media.ui.material3.navigation.CollectionRoute
import com.google.android.horologist.media.ui.material3.navigation.MediaItemRoute
import com.google.android.horologist.media.ui.material3.navigation.NavigationScreens
import com.google.android.horologist.mediasample.ui.util.JankPrinter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaActivity : ComponentActivity() {
    private lateinit var jankPrinter: JankPrinter
    lateinit var backStack: NavBackStack<MediaRoute>

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jankPrinter = JankPrinter()

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            backStack = rememberNavBackStack(
                PlayerRoute(page = 0),
            ) as NavBackStack<MediaRoute>

            UampWearApp(
                backStack = backStack,
                intent = intent,
            )

            LaunchedEffect(backStack) {
                snapshotFlow {
                    when (val last = backStack.lastOrNull()) {
                        is CustomRoute -> last.route
                        is PlayerRoute -> "player?page=${last.page}"
                        is CollectionRoute -> "collection?id=${last.id}"
                        is MediaItemRoute -> "mediaItem?id=${last.id}"
                        null -> ""
                        else -> last.javaClass.simpleName
                    }
                }.collect { route ->
                    jankPrinter.setRouteState(route = route)
                }
            }
        }

        jankPrinter.installJankStats(activity = this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Don't show progress on resume since it can be out of date
            setRecentsScreenshotEnabled(false)
        }
    }

    companion object {
        const val CollectionKey = "collection"
        const val MediaIdKey = "mediaId"
        const val PositionKey = "position"
    }
}
