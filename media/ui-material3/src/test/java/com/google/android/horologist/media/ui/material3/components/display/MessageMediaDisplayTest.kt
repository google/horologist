/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

class MessageMediaDisplayTest : WearLegacyComponentTest() {
    @Test
    fun default() {
        runComponentTest {
            MessageMediaDisplay(modifier = Modifier.alpha(0.5f).fillMaxSize(), "Test")
        }
    }

    @Test
    fun messageMediaDisplay_whenEmptyMessage() {
        runComponentTest {
            DisplayArea {
                MessageMediaDisplay(modifier = Modifier.alpha(0.5f).fillMaxSize(), "")
            }
        }
    }

    @Config(
        qualifiers = "+w192dp-h192dp",
    )
    @Test
    fun messageMediaDisplay_whenSmallScreen() {
        runComponentTest {
            DisplayArea {
                MessageMediaDisplay(modifier = Modifier.alpha(0.5f).fillMaxSize(), "David Mode")
            }
        }
    }

    @Test
    fun messageMediaDisplay_whenLargeScreen() {
        runComponentTest {
            DisplayArea {
                MessageMediaDisplay(modifier = Modifier.alpha(0.5f).fillMaxSize(), "David Mode")
            }
        }
    }

    @Composable
    private fun DisplayArea(content: @Composable () -> Unit) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
