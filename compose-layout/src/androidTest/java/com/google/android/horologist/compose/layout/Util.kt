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

package com.google.android.horologist.compose.layout

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import org.junit.Assert

@Composable
fun ForceMode(isRound: Boolean, fn: @Composable () -> Unit) {
    val oldConfiguration = LocalConfiguration.current
    val newConfiguration = Configuration(oldConfiguration).apply {
        screenLayout = if (isRound)
            screenLayout or Configuration.SCREENLAYOUT_ROUND_YES
        else
            screenLayout and Configuration.SCREENLAYOUT_ROUND_YES.inv()
    }
    CompositionLocalProvider(LocalConfiguration provides newConfiguration) {
        Assert.assertEquals(isRound, LocalConfiguration.current.isScreenRound)
        fn()
    }
}

@Composable
fun Boxes() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxRectangle()
                .testTag("boxInset")
        ) {
        }
    }
}

@Composable
fun ExampleCard(modifier: Modifier, i: Int) {
    Card(
        modifier = modifier.testTag("card.$i"),
        onClick = { }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Card $i")
        }
    }
}
