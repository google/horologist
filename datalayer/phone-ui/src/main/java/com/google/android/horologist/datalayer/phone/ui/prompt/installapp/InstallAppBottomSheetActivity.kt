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

package com.google.android.horologist.datalayer.phone.ui.prompt.installapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch

internal const val INSTALL_APP_KEY_APP_PACKAGE_NAME = "HOROLOGIST_INSTALL_APP_KEY_APP_PACKAGE_NAME"
internal const val INSTALL_APP_KEY_IMAGE_RES_ID = "HOROLOGIST_INSTALL_APP_KEY_IMAGE_RES_ID"
internal const val INSTALL_APP_KEY_TOP_MESSAGE = "HOROLOGIST_INSTALL_APP_KEY_TOP_MESSAGE"
internal const val INSTALL_APP_KEY_BOTTOM_MESSAGE = "HOROLOGIST_INSTALL_APP_KEY_BOTTOM_MESSAGE"

private const val NO_IMAGE = 0

internal class InstallAppBottomSheetActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appPackageName = intent.extras?.getString(INSTALL_APP_KEY_APP_PACKAGE_NAME) ?: ""
        val imageResId = intent.extras?.getInt(INSTALL_APP_KEY_IMAGE_RES_ID) ?: NO_IMAGE
        val topMessage = intent.extras?.getString(INSTALL_APP_KEY_TOP_MESSAGE) ?: ""
        val bottomMessage = intent.extras?.getString(INSTALL_APP_KEY_BOTTOM_MESSAGE) ?: ""

        setContent {
            Surface {
                val bottomSheetState = rememberModalBottomSheetState()
                val coroutineScope = rememberCoroutineScope()

                val image: (@Composable () -> Unit)? = imageResId.takeIf { it != NO_IMAGE }?.let {
                    {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = null,
                        )
                    }
                }

                InstallAppBottomSheet(
                    image = image,
                    topMessage = topMessage,
                    bottomMessage = bottomMessage,
                    onDismissRequest = {
                        setResult(RESULT_CANCELED)
                        coroutineScope.launch {
                            try {
                                bottomSheetState.hide()
                            } finally {
                                finishWithoutAnimation()
                            }
                        }
                    },
                    onConfirmation = {
                        InstallAppPromptAction.run(context = this, appPackageName = appPackageName)

                        setResult(RESULT_OK)
                        finishWithoutAnimation()
                    },
                    sheetState = bottomSheetState,
                )
            }
        }
    }

    private fun finishWithoutAnimation() {
        finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    internal companion object {
        fun getIntent(
            context: Context,
            appPackageName: String,
            @DrawableRes image: Int,
            topMessage: String,
            bottomMessage: String,
        ) = Intent(context, InstallAppBottomSheetActivity::class.java).apply {
            putExtra(INSTALL_APP_KEY_APP_PACKAGE_NAME, appPackageName)
            putExtra(INSTALL_APP_KEY_TOP_MESSAGE, topMessage)
            putExtra(INSTALL_APP_KEY_BOTTOM_MESSAGE, bottomMessage)
            putExtra(INSTALL_APP_KEY_IMAGE_RES_ID, image)
        }
    }
}
