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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text

class ScratchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    HomeScreen()
}

@Preview
@Composable
fun HomeScreen() {
    val defaultDensity = LocalDensity.current
    val scrollState = rememberScrollState()
    val fontSize = 32.sp
    val textStyle = TextStyle.Default.copy(fontSize = fontSize)
    var height1: Int = 0
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        CompositionLocalProvider(LocalDensity provides Density(defaultDensity.density, 1.0f)) {
            val textMeasurer = rememberTextMeasurer()

            height1 =
                textMeasurer.measure(text = "Font Scale: 1.0 112.dp", style = textStyle).size.height
        }
        println("Scale,Sp,Px,Ratio")
        (94..200).map { it / 100f }.forEach { fontScale ->
            CompositionLocalProvider(
                LocalDensity provides Density(
                    defaultDensity.density,
                    fontScale
                )
            ) {
                val textMeasurer = rememberTextMeasurer()

                val size =
                    textMeasurer.measure(text = "Font Scale: $fontScale 112.dp", style = textStyle)
                val height = size.size.height
                val ratio = height.toFloat() / height1
                println("$fontScale,${fontSize.value},$height,$ratio")
                Text("Font Scale: $fontScale $height", style = textStyle)
            }
        }
    }
}
