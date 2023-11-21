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

package com.google.android.horologist.screensizes

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import com.google.android.horologist.compose.tools.Device
import org.junit.Test

class DialogTest(device: Device) : ScreenSizeTest(device = device, showTimeText = false) {

    @Composable
    override fun Content() {
        Alert(
            title = { Text("Phone app is required", textAlign = TextAlign.Center) },
            negativeButton = {
                Button(
                    colors = ButtonDefaults.secondaryButtonColors(),
                    onClick = {
                        /* Do something e.g. navController.popBackStack()*/
                    },
                ) {
                    Text("No")
                }
            },
            positiveButton = {
                Button(onClick = {
                    /* Do something e.g. navController.popBackStack()*/
                }) { Text("Yes") }
            },
        ) {
            Text(
                text = "Tap the button below to install it on your phone.",
                textAlign = TextAlign.Center,
            )
        }
    }


    @Test
    fun responsive() {
        runTest {
            com.google.android.horologist.compose.material.Alert(
                title = "Phone app is required",
                body = "Tap the button below to install it on your phone.",
                onCancelButtonClick = {},
                onOKButtonClick = {},
                okButtonContentDescription = "Yes",
                cancelButtonContentDescription = "No",
            )
        }
    }
}
