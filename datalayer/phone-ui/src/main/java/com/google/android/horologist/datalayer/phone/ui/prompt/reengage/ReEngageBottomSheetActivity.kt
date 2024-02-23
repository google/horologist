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

package com.google.android.horologist.datalayer.phone.ui.prompt.reengage

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
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.di.CoroutineAppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal const val RE_ENGAGE_KEY_NODE_ID = "HOROLOGIST_RE_ENGAGE_KEY_NODE_ID"
internal const val RE_ENGAGE_KEY_IMAGE_RES_ID = "HOROLOGIST_RE_ENGAGE_KEY_IMAGE_RES_ID"
internal const val RE_ENGAGE_KEY_TOP_MESSAGE = "HOROLOGIST_RE_ENGAGE_KEY_TOP_MESSAGE"
internal const val RE_ENGAGE_KEY_BOTTOM_MESSAGE = "HOROLOGIST_RE_ENGAGE_KEY_BOTTOM_MESSAGE"
internal const val RE_ENGAGE_KEY_POSITIVE_BUTTON_LABEL =
    "HOROLOGIST_RE_ENGAGE_KEY_POSITIVE_BUTTON_LABEL"
internal const val RE_ENGAGE_KEY_NEGATIVE_BUTTON_LABEL =
    "HOROLOGIST_RE_ENGAGE_KEY_NEGATIVE_BUTTON_LABEL"

private const val NO_IMAGE = 0

internal class ReEngageBottomSheetActivity : ComponentActivity() {

    private lateinit var phoneDataLayerAppHelper: PhoneDataLayerAppHelper
    private lateinit var coroutineAppScope: CoroutineScope
    private lateinit var nodeId: String

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coroutineAppScope = CoroutineAppScope.getInstance()

        phoneDataLayerAppHelper = PhoneDataLayerAppHelper(
            this,
            WearDataLayerRegistry.fromContext(
                application = application,
                coroutineScope = coroutineAppScope,
            ),
        )

        nodeId = intent.extras?.getString(RE_ENGAGE_KEY_NODE_ID) ?: ""
        val imageResId = intent.extras?.getInt(RE_ENGAGE_KEY_IMAGE_RES_ID) ?: NO_IMAGE
        val topMessage = intent.extras?.getString(RE_ENGAGE_KEY_TOP_MESSAGE) ?: ""
        val bottomMessage = intent.extras?.getString(RE_ENGAGE_KEY_BOTTOM_MESSAGE) ?: ""
        val positiveButtonLabel = intent.extras?.getString(RE_ENGAGE_KEY_POSITIVE_BUTTON_LABEL)
        val negativeButtonLabel = intent.extras?.getString(RE_ENGAGE_KEY_NEGATIVE_BUTTON_LABEL)

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

                ReEngageBottomSheet(
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
                        launchAppOnWatch()
                    },
                    positiveButtonLabel = positiveButtonLabel,
                    negativeButtonLabel = negativeButtonLabel,
                    sheetState = bottomSheetState,
                )
            }
        }
    }

    private fun launchAppOnWatch() {
        // Can't use the Activity's lifecycleScope as it is going to finish the activity immediately
        // after this call
        coroutineAppScope.launch {
            phoneDataLayerAppHelper.startRemoteOwnApp(nodeId = nodeId)
        }

        // It returns OK to indicate that the user tapped on the positive button.
        // The call above might fail, but the result is not reflected in the activity's result.
        // In order to do that, it would require to make the bottom sheet display a spinner while
        // waiting for the result of the call.
        setResult(RESULT_OK)
        finishWithoutAnimation()
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
            nodeId: String,
            @DrawableRes image: Int,
            topMessage: String,
            bottomMessage: String,
            positiveButtonLabel: String? = null,
            negativeButtonLabel: String? = null,
        ) = Intent(context, ReEngageBottomSheetActivity::class.java).apply {
            putExtra(RE_ENGAGE_KEY_NODE_ID, nodeId)
            putExtra(RE_ENGAGE_KEY_IMAGE_RES_ID, image)
            putExtra(RE_ENGAGE_KEY_TOP_MESSAGE, topMessage)
            putExtra(RE_ENGAGE_KEY_BOTTOM_MESSAGE, bottomMessage)
            putExtra(RE_ENGAGE_KEY_POSITIVE_BUTTON_LABEL, positiveButtonLabel)
            putExtra(RE_ENGAGE_KEY_NEGATIVE_BUTTON_LABEL, negativeButtonLabel)
        }
    }
}
