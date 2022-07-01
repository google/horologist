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

package com.google.android.horologist.mediasample.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.mediasample.di.MediaActivityContainer
import com.google.android.horologist.mediasample.di.ViewModelModule
import com.google.android.horologist.mediasample.ui.app.UampWearApp

class MediaActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    lateinit var mediaActivityContainer: MediaActivityContainer

    lateinit var viewModelModule: ViewModelModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MediaActivityContainer.inject(this)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            navController = rememberSwipeDismissableNavController()
            UampWearApp(
                navController = navController,
                creationExtras = { defaultViewModelCreationExtras }
            )
        }
    }

    override fun getDefaultViewModelCreationExtras(): CreationExtras {
        return MutableCreationExtras(super.getDefaultViewModelCreationExtras()).apply {
            viewModelModule.addCreationExtras(this)
        }
    }
}
