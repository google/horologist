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

package com.google.android.horologist.datalayer.phone.ui.prompt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.google.android.horologist.datalayer.phone.ui.play.launchPlay

internal const val INSTALL_APP_KEY_APP_NAME = "HOROLOGIST_INSTALL_APP_KEY_APP_NAME"
internal const val INSTALL_APP_KEY_WATCH_NAME = "HOROLOGIST_INSTALL_APP_KEY_WATCH_NAME"
internal const val INSTALL_APP_KEY_MESSAGE = "HOROLOGIST_INSTALL_APP_KEY_MESSAGE"
internal const val INSTALL_APP_KEY_IMAGE_RES_ID = "HOROLOGIST_INSTALL_APP_KEY_IMAGE_RES_ID"

private const val NO_IMAGE = 0

internal class InstallAppDialogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appName = intent.extras?.getString(INSTALL_APP_KEY_APP_NAME) ?: ""
        val watchName = intent.extras?.getString(INSTALL_APP_KEY_WATCH_NAME) ?: ""
        val message = intent.extras?.getString(INSTALL_APP_KEY_MESSAGE) ?: ""
        val imageResId = intent.extras?.getInt(INSTALL_APP_KEY_IMAGE_RES_ID) ?: NO_IMAGE

        setContent {
            Surface {
                val icon: (@Composable () -> Unit)? = imageResId.takeIf { it != NO_IMAGE }?.let {
                    {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = null,
                        )
                    }
                }

                InstallAppDialog(
                    appName = appName,
                    watchName = watchName,
                    message = message,
                    icon = icon,
                    onDismissRequest = {
                        setResult(RESULT_CANCELED)
                        finish()
                    },
                    onConfirmation = {
                        this.launchPlay(this.applicationContext.packageName)

                        setResult(RESULT_OK)
                        finish()
                    },
                )
            }
        }
    }

    internal companion object {
        fun getIntent(
            context: Context,
            appName: String,
            watchName: String,
            message: String,
            @DrawableRes image: Int,
        ) = Intent(context, InstallAppDialogActivity::class.java).apply {
            putExtra(INSTALL_APP_KEY_APP_NAME, appName)
            putExtra(INSTALL_APP_KEY_WATCH_NAME, watchName)
            putExtra(INSTALL_APP_KEY_MESSAGE, message)
            putExtra(INSTALL_APP_KEY_IMAGE_RES_ID, image)
        }
    }
}
