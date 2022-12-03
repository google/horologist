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

package com.google.android.horologist.scratch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Stepper
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.tools.WearSquareDevicePreview

class ScratchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VolumeScreen()
        }
    }
}

@WearSquareDevicePreview
@Composable
fun VolumeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        timeText = {
            TimeText(
                timeSource = object : TimeSource {
                    override val currentTime: String
                        @Composable
                        get() = "11:02 AM"
                }
            )
        },
        positionIndicator = {
            PositionIndicator(value = { 0.5f })
        }
    ) {
        Stepper(
            value = 5,
            onValueChange = {},
            valueProgression = 1..10,
            decreaseIcon = {
                Icon(
                    modifier = Modifier
                        .size(26.dp),
                    imageVector = Icons.Default.VolumeDown,
                    contentDescription = "Decrease Volume"
                )
            },
            increaseIcon = {
                Icon(
                    modifier = Modifier
                        .size(26.dp),
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Increase Volume"
                )
            }) {
            Text(
                "Volume",
                style = MaterialTheme.typography.button,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}
