/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@Config(
    sdk = [33],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound
)
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SeekToScreenshotTests {

    @Test
    fun ButtonRow() {
        captureRoboImage(filePath = "src/test/screenshots/SeekToScreenshotTests.png") {
            Row(
                modifier = Modifier
                    .padding(50.dp)
                    .border(1.dp, Color.Blue),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .border(1.dp, Color.Red)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(scaleX = -1f),
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(size = 32.dp),
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Previous",
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .border(1.dp, Color.Red)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(size = 32.dp),
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                        )
                    }
                }
            }
        }
    }
}

