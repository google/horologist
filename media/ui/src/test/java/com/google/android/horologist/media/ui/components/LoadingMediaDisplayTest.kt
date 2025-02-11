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

@file:OptIn(ExperimentalWearFoundationApi::class, ExperimentalWearMaterialApi::class)

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.LocalReduceMotion
import androidx.wear.compose.foundation.ReduceMotion
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.google.android.horologist.media.ui.components.animated.MarqueeTextMediaDisplay
import com.google.android.horologist.media.ui.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.components.display.TextMediaDisplay
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

class LoadingMediaDisplayTest : WearLegacyComponentTest() {
    @Test
    fun default() {
        runComponentTest {
            LoadingMediaDisplay()
        }
    }

    @Test
    fun loadingMediaDisplay_textMediaDisplay_overlay_largeScreen() {
        runComponentTest {
            DisplayArea {
                LoadingMediaDisplay(modifier = Modifier.alpha(0.5f))
                TextMediaDisplay(title = "Sorrow", subtitle = "David Bowie")
            }
        }
    }

    @Test
    fun loadingMediaDisplay_marqueeTextMediaDisplay_overlay_largeScreen() {
        runComponentTest {
            DisplayArea {
                LoadingMediaDisplay(modifier = Modifier.alpha(0.5f))
                MarqueeTextMediaDisplay(title = "Sorrow", artist = "David Bowie")
            }
        }
    }

    @Config(
        qualifiers = "+w192dp-h192dp",
    )
    @Test
    fun loadingMediaDisplay_textMediaDisplay_overlay_smallScreen() {
        runComponentTest {
            DisplayArea {
                LoadingMediaDisplay(modifier = Modifier.alpha(0.5f))
                TextMediaDisplay(title = "Sorrow", subtitle = "David Bowie")
            }
        }
    }

    @Config(
        qualifiers = "+w192dp-h192dp",
    )
    @Test
    fun loadingMediaDisplay_marqueeTextMediaDisplay_overlay_smallScreen() {
        runComponentTest {
            DisplayArea {
                LoadingMediaDisplay(modifier = Modifier.alpha(0.5f))
                MarqueeTextMediaDisplay(title = "Sorrow", artist = "David Bowie")
            }
        }
    }

    @Composable
    fun DisplayArea(content: @Composable () -> Unit) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }

    @Composable
    override fun ComponentScaffold(content: @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalReduceMotion provides ReduceMotion {
                true
            },
        ) {
            super.ComponentScaffold(content)
        }
    }
}
