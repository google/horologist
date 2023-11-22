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

package com.google.android.horologist.datalayer.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.horologist.datalayer.phone.ui.PhoneUiDataLayerHelper
import com.google.android.horologist.datalayer.sample.screens.main.MainScreen
import com.google.android.horologist.datalayer.sample.ui.theme.HorologistTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var phoneUiDataLayerHelper: PhoneUiDataLayerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HorologistTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen(
                        onShowInstallAppPrompt = ::showInstallAppPrompt,
                    )
                }
            }
        }
    }

    private fun showInstallAppPrompt() {
        phoneUiDataLayerHelper.showInstallAppPrompt(
            activity = this@MainActivity,
            appName = getString(R.string.install_app_prompt_sample_app_name),
            appPackageName = getString(R.string.install_app_prompt_sample_app_package_name),
            watchName = getString(R.string.install_app_prompt_sample_watch_name),
            message = getString(R.string.install_app_prompt_sample_message),
            image = R.drawable.sample_app_wearos_screenshot,
        )
    }
}
