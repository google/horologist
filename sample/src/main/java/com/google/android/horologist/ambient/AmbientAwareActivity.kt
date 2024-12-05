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

package com.google.android.horologist.ambient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.google.android.horologist.compose.nav.SwipeDismissableNavHost
import com.google.android.horologist.compose.nav.composable
import kotlinx.serialization.Serializable

class AmbientAwareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AmbientAwareWearApp()
        }
    }
}

@Composable
fun AmbientAwareWearApp() {
    val navController = rememberSwipeDismissableNavController()

    navController.cu

    AppScaffold {
        Box(modifier = Modifier.fillMaxSize()) {
            SwipeDismissableNavHost(navController, Home) {
                composable<Home> {
                    HomeScreen(onRun = { navController.navigate(Preparing) }, onSettings = { navController.navigate(Settings) })
                }
                composable<Preparing> {
                    PreparingScreen(onStart = { navController.navigate(Exercise) }, onSettings = { navController.navigate(Settings) })
                }
                composable<Exercise> {
                    ExerciseScreen(onStop = { navController.navigate(Home) }, onSettings = { navController.navigate(Settings) })
                }
                composable<Settings> {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onRun: () -> Unit, onSettings: () -> Unit) {
    val columnState = rememberResponsiveColumnState()
    ScreenScaffold(modifier = modifier, scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                Title("Home")
            }
            item {
                Chip(
                    label = "Run",
                    onClick = onRun,
                )
            }
            item {
                Chip(
                    label = "Settings",
                    onClick = onSettings,
                )
            }
        }
    }
}

@Composable
fun ExerciseScreen(modifier: Modifier = Modifier, onStop: () -> Unit, onSettings: () -> Unit) {
    AmbientAware { ambientState ->
        if (ambientState.isInteractive) {
            ScreenScaffold(modifier = modifier, timeText = {
                if (ambientState.isInteractive) {
                    AmbientAwareTimeText(ambientState)
                }
            }) {
                Box(modifier = modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Exercise",
                            color = Color.Green,
                        )
                        Button(onClick = onStop, imageVector = Icons.Rounded.Cancel, contentDescription = "Cancel")
                        Button(onClick = onSettings, imageVector = Icons.Rounded.Settings, contentDescription = "Settings")
                    }
                }
            }
        }
    }
}

@Composable
fun PreparingScreen(modifier: Modifier = Modifier, onStart: () -> Unit, onSettings: () -> Unit) {
    AmbientAware { ambientState ->
        ScreenScaffold(modifier = modifier.ambientGray(ambientState), timeText = {
            AmbientAwareTimeText(ambientState)
        }) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Preparing",
                    color = Color.Blue,
                )
                Text(
                    ambientState.name,
                    color = Color.Blue,
                )
                Button(onClick = onStart, imageVector = Icons.Rounded.PlayArrow, contentDescription = "Start")
                Button(onClick = onSettings, imageVector = Icons.Rounded.Settings, contentDescription = "Settings")
            }
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val columnState = rememberResponsiveColumnState()
    ScreenScaffold(modifier = modifier, scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                Title("Settings")
            }
            items(5) {
                val toggled = remember { mutableStateOf(false) }
                ToggleChip(
                    checked = false,
                    label = "Item $it",
                    onCheckedChanged = { toggled.value = !toggled.value },
                    toggleControl = ToggleChipToggleControl.Switch,
                )
            }
        }
    }
}

private val grayscale = Paint().apply {
    colorFilter = ColorFilter.colorMatrix(
        ColorMatrix().apply {
            setToSaturation(0f)
        },
    )
    isAntiAlias = false
}

internal fun Modifier.ambientGray(ambientState: AmbientState): Modifier =
    if (ambientState.isAmbient) {
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
fun AmbientAwareTimeText(ambientState: AmbientState) {
    TimeText(endCurvedContent = {
        curvedText(ambientState.name, color = Color.LightGray)
    })
}

@Serializable
object Home

@Serializable
object Preparing

@Serializable
object Exercise

@Serializable
object Settings

@WearPreviewLargeRound
@Composable
fun WearAppPreview() {
    AmbientAwareWearApp()
}
