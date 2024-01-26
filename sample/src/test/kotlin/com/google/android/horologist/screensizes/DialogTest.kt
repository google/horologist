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
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.AlertContent
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ConfirmationContent
import com.google.android.horologist.compose.material.ResponsiveDialogContent
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.screenshots.ScreenshotTestRule
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DialogTest(device: Device) : ScreenSizeTest(
    device = device,
    showTimeText = false,
    recordMode = ScreenshotTestRule.RecordMode.Record,
) {

    @Composable
    override fun Content() {
        // horologist AlertContent using ResponsiveDialogContent
        AlertContent(
            title = "Phone app is required",
            onCancelButtonClick = {},
            onOKButtonClick = {},
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
        lateinit var columnState: ScalingLazyColumnState

        runTest(testFn = {
            screenshotTestRule.interact {
                runBlocking {
                    columnState.state.scrollToItem(999, 0)
                }
            }

            screenshotTestRule.takeScreenshot()
        }) {
            columnState = rememberColumnState()

            ResponsiveDialogContent(
                title = {
                    Text(
                        text = "Turn on Bedtime mode?",
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                    )
                },
                onOkButtonClick = {},
                onCancelButtonClick = {},
                okButtonContentDescription = stringResource(R.string.ok),
                cancelButtonContentDescription = stringResource(R.string.cancel),
                state = columnState,
            ) {
                item {
                    Text(
                        text = "Watch screen, tilt-to-wake, and touch are turned off. " +
                            "Only calls from starred contacts, repeat callers, " +
                            "and alarms will notify you.",
                        textAlign = TextAlign.Left,
                    )
                }
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
    }

    @Test
    fun batterySaverScreen() {
        lateinit var columnState: ScalingLazyColumnState

        runTest(testFn = {
            screenshotTestRule.interact {
                runBlocking {
                    columnState.state.scrollToItem(999, 0)
                }
            }

            screenshotTestRule.takeScreenshot()
        }) {
            columnState = rememberColumnState()

            ResponsiveDialogContent(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                    )
                },
                title = {
                    Text(
                        text = "Enable Battery Saver Mode?",
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                    )
                },
                message = {
                    Text(
                        text = "Your battery is low." +
                            "turn on battery saver.",
                        textAlign = TextAlign.Center,
                    )
                },
                onOkButtonClick = {},
                onCancelButtonClick = {},
                okButtonContentDescription = stringResource(R.string.ok),
                cancelButtonContentDescription = stringResource(R.string.cancel),
                state = columnState,
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
    }

    @Test
    fun textAlertScreen() {
        runTest {
            AlertContent(
                title = "Text only dialogs can use up to 3 lines of text in this layout",
                onCancelButtonClick = {},
                onOKButtonClick = {},
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
                onCancelButtonClick = {},
                onOKButtonClick = {},
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
}
