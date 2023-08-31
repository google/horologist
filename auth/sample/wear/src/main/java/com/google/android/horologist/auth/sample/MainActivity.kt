/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.auth.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.wear.watchface.complications.data.ComplicationType
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var wearDataLayerAppHelper: WearDataLayerAppHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wearDataLayerAppHelper = WearDataLayerAppHelper(
            context = this,
            registry = (application as SampleApplication).registry,
            scope = lifecycleScope,
        )

        lifecycleScope.launch {
            wearDataLayerAppHelper.markTileAsInstalled("MyTile")
            wearDataLayerAppHelper.markComplicationAsActivated(
                complicationName = "MyComplication",
                complicationInstanceId = 1234,
                complicationType = ComplicationType.SHORT_TEXT,
            )
            wearDataLayerAppHelper.markActivityLaunchedOnce()
        }

        setContent {
            WearApp()
        }
    }
}
