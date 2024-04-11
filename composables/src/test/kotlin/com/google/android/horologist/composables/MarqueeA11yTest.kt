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

package com.google.android.horologist.composables

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test

class MarqueeA11yTest : WearLegacyA11yTest() {
    @Test
    fun noMarquee() {
        runComponentTest {
            MarqueeSample("Sia")
        }
    }

    @Test
    fun marquee() {
        composeRule.mainClock.autoAdvance = false
        runComponentTest {
            MarqueeSample("Tikki Tikki Tembo-no Sa Rembo-chari Bari Ruchi-pip Peri Pembo")
        }
    }

    @Composable
    private fun MarqueeSample(text: String) {
        MarqueeText(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(192.dp),
        )
    }
}
