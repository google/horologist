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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class CardTest : WearLegacyComponentTest() {

    @Test
    fun withoutLongClick() {
        runComponentTest {
            Card(
                onClick = { },
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Hello, Card")

                    androidx.wear.compose.material.Button(onClick = { }) {
                        Text("Click me!")
                    }
                }
            }
        }
    }

    @Test
    fun withLongClick() {
        runComponentTest {
            Card(
                onClick = { },
                onLongClick = { },
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Hello, Card")

                    androidx.wear.compose.material.Button(onClick = { }) {
                        Text("Click me!")
                    }
                }
            }
        }
    }

    @Test
    fun withoutLongClickCustomPadding() {
        runComponentTest {
            Card(
                onClick = { },
                contentPadding = PaddingValues(20.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Hello, Card")

                    androidx.wear.compose.material.Button(onClick = { }) {
                        Text("Click me!")
                    }
                }
            }
        }
    }

    @Test
    fun withLongClickCustomPadding() {
        runComponentTest {
            Card(
                onClick = { },
                onLongClick = { },
                contentPadding = PaddingValues(20.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Hello, Card")

                    androidx.wear.compose.material.Button(onClick = { }) {
                        Text("Click me!")
                    }
                }
            }
        }
    }
}
