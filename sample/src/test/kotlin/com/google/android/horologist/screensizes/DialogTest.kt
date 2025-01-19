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

import android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Battery1Bar
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.style.TextAlign
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import com.google.android.horologist.compose.material.AlertContent
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ConfirmationContent
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.google.android.horologist.compose.tools.Device
import org.junit.Test

class DialogTest(device: Device) : WearLegacyScreenSizeTest(
    device = device,
    showTimeText = false,
) {

    @Composable
    override fun Content() {
        // horologist AlertContent using ResponsiveDialogContent
        AlertContent(
            title = "Phone app is required",
            onCancel = {},
            onOk = {},
            message = "Tap the button below to install it on your phone.",
        )
    }

    @Test
    fun wearMaterial() {
        // androidx.wear.compose.material.dialog.Alert with no formatting
        runTest {
            Alert(
                title = { Text("Phone app is required") },
                negativeButton = {
                    Button(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        onClick = {},
                    )
                },
                positiveButton = {
                    Button(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        onClick = { },
                    )
                },
            ) {
                Text(
                    text = "Tap the button below to install it on your phone.",
                )
            }
        }
    }

    @Test
    fun longDialogScreen1() {
        runTest {
            AlertContent(
                title = "Turn on Bedtime mode?",
                message = "Watch screen, tilt-to-wake, and touch are turned off. " +
                    "Only calls from starred contacts, repeat callers, " +
                    "and alarms will notify you.",
                onOk = {},
                onCancel = {},
                okButtonContentDescription = stringResource(R.string.ok),
                cancelButtonContentDescription = stringResource(R.string.cancel),
            ) {
                item {
                    ToggleChip(
                        checked = false,
                        onCheckedChanged = {},
                        label = "Don't show again",
                        toggleControl = ToggleChipToggleControl.Checkbox,
                    )
                }
            }
        }

        composeRule.onNode(hasScrollToNodeAction())
            .performTouchInput { repeat(10) { swipeUp() } }

        captureScreenshot("_2")
    }

    @Test
    fun batterySaverScreen() {
        runTest {
            AlertContent(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                    )
                },
                title = "Enable Battery Saver Mode?",
                message = "Your battery is low. Turn on battery saver.",
                onOk = {},
                onCancel = {},
                okButtonContentDescription = stringResource(R.string.ok),
                cancelButtonContentDescription = stringResource(R.string.cancel),
            ) {
                item {
                    Chip(
                        label = { Text(text = "Primary Label") },
                        onClick = {},
                        icon = {
                            Icon(
                                Icons.Outlined.Battery1Bar,
                                contentDescription = "Battery low",
                            )
                        },
                        colors = ChipDefaults.secondaryChipColors(),
                    )
                }
                item {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.caption3,
                    ) {
                        Text(
                            text = "Body enim ad minim veniam, quis nostrud",
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }

        composeRule.onNode(hasScrollToNodeAction())
            .performTouchInput { repeat(10) { swipeUp() } }

        captureScreenshot("_2")
    }

    @Test
    fun textAlertScreen() {
        runTest {
            AlertContent(
                title = "Text only dialogs can use up to 3 lines of text in this layout",
                onCancel = {},
                onOk = {},
            )
        }
    }

    @Test
    fun iconAndTextAlertScreen() {
        runTest {
            AlertContent(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                    )
                },
                title = "Icon dialogs can use up to 2 lines of text",
                onCancel = {},
                onOk = {},
            )
        }
    }

    @Test
    fun alarmConfirmationScreen() {
        runTest {
            ConfirmationContent(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.Green,
                    )
                },
                title = "Alarm in 23 hr 59 min",
            )
        }
    }

    @Test
    fun multiLineConfirmationScreen() {
        runTest {
            ConfirmationContent(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.Green,
                    )
                },
                title = "This example uses three lines of text to show max limit",
            )
        }
    }
}
