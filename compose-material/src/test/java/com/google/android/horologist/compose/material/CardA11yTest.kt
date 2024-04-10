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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextEquals
import androidx.wear.compose.material.Text
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test

class CardA11yTest : WearLegacyA11yTest() {

    @Test
    fun default() {
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

        composeRule.onNode(keyIsDefined(SemanticsProperties.Role))
            .assertTextEquals("Click me!")
            .assertHasClickAction()
    }

    @Test
    fun material() {
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

        composeRule.onNode(keyIsDefined(SemanticsProperties.Role))
            .assertTextEquals("Click me!")
            .assertHasClickAction()
    }

    @Test
    fun disabled() {
        runComponentTest {
            Card(
                onClick = {},
                onLongClick = {},
                enabled = false,
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
