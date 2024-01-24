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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import com.google.android.horologist.compose.material.AlertContent
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.screenshots.ScreenshotTestRule
import org.junit.Test

class DialogTest(device: Device) : ScreenSizeTest(
    device = device,
    showTimeText = false,
) {

    @Composable
    override fun Content() {
        // horologist AlertContent using ResponsiveDialogContent
        AlertContent(
            title = "Phone app is required",
            onCancelButtonClick = {},
            onOKButtonClick = {},
            body = "Tap the button below to install it on your phone.",
        )
    }

    @Test
    fun wearMaterial() {
        // androidx.wear.compose.material.dialog.Alert with no formatting
        runTest {
            Alert(title = { Text("Phone app is required") },
                negativeButton = {
                    Button(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        onClick = {},
                    )
                }, positiveButton = {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "",
                    onClick = { },
                )
            }) {
                Text(
                    text = "Tap the button below to install it on your phone.",
                )
            }
        }
    }
}
