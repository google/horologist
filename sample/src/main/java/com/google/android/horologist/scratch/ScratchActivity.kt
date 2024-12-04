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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientStateUpdate
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.nav.SwipeDismissableNavHost
import com.google.android.horologist.compose.nav.composable
import kotlinx.serialization.Serializable

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
    val navController = rememberSwipeDismissableNavController()
    AmbientAware { update ->
        AppScaffold(timeText = {
            if (update.value.isInteractive) {
                TimeText()
            }
        }) {
            SideEffect {
                println("App - Ambient state update: $update")
            }

            Box(modifier = Modifier.fillMaxSize()) {
                SwipeDismissableNavHost(navController, Preparing) {
                    composable<Preparing> {
                        PreparingScreen(update, modifier = Modifier.ambientGray(update))
                    }
                    composable<Exercise> {
                        ExerciseScreen(update, modifier = Modifier.ambientGray(update))
                    }
                }
            }
        }
    }
}

private val grayscale = Paint().apply {
    colorFilter = ColorFilter.colorMatrix(
        ColorMatrix().apply {
            setToSaturation(0.5f)
        },
    )
    isAntiAlias = false
}

internal fun Modifier.ambientGray(ambientState: State<AmbientStateUpdate>): Modifier =
    if (ambientState.value.isAmbient) {
        graphicsLayer {
            scaleX = 0.9f
            scaleY = 0.9f
        }.drawWithContent {
            drawIntoCanvas {
                it.withSaveLayer(size.toRect(), grayscale) {
                    drawContent()
                }
            }
        }
    } else {
        this
    }

@Composable
fun ExerciseScreen(ambientState: State<AmbientStateUpdate>, modifier: Modifier = Modifier) {
    ScreenScaffold {
    }
}

@Composable
fun PreparingScreen(update: State<AmbientStateUpdate>, modifier: Modifier = Modifier) {
    SideEffect {
        println("PreparingScreen - Ambient state update: $update")
    }

    ScreenScaffold {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                "Preparing: ${update.value}",
                color = Color.Blue,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Serializable
object Preparing

@Serializable
object Exercise

@WearPreviewLargeRound
@Composable
fun WearAppPreview() {
    WearApp()
}
