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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.mediasample.ui.util.JankPrinter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaActivity : ComponentActivity() {
    private lateinit var jankPrinter: JankPrinter
    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jankPrinter = JankPrinter()

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            navController = rememberSwipeDismissableNavController()
            UampWearApp(
                navController = navController,
                intent = intent
            )
        }

        jankPrinter.installJankStats(this, navController)
    }

    companion object {
        const val CollectionKey = "collection"
        const val MediaIdKey = "mediaId"
    }
}
