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

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color.WHITE
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewSquare


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
    Row(modifier = Modifier.fillMaxSize()) {
        ViewCounter(modifier = Modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight()
            .border(1.dp, Color.Red))

        ComposeCounter(modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight()
            .border(1.dp, Color.Blue))
    }
}

@Composable
fun ComposeCounter(modifier: Modifier) {
    val value = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        value.animateTo(100f)
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Text("Compose")
        Text("Value: ${value.value}")
    }
}

@SuppressLint("SetTextI18n")
@Composable
fun ViewCounter(modifier: Modifier ) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Text("Views")
        AndroidView(factory = {
            val counterAnimator = ValueAnimator.ofFloat(0f, 100f)

            TextView(it).apply {
                setTextColor(WHITE)
                text = "Value: "
                counterAnimator.addUpdateListener { animation ->
                    val animatedValue = animation.animatedValue as Float
                    text = "Value: $animatedValue"
                }
                counterAnimator.start()
            }
        })
    }
}

@WearPreviewSquare
@Composable
fun AnimateDemo() {
    WearApp()
}
