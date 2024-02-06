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

package com.google.android.horologist.compose.layout

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleButton
import kotlin.math.max

/**
 *  An app scaffold, to be used to wrap a SwipeDismissableNavHost.
 * The [TimeText] will be shown here, but can be customised in either [ScreenScaffold] or
 * [PagerScaffold].
 *
 * Without this, the vanilla [Scaffold] is likely placed on each individual screen and [TimeText]
 * moves with the screen, or shown twice when swiping to dimiss.
 *
 * @param modifier the Scaffold modifier.
 * @param timeText the app default time text, defaults to TimeText().
 * @param content the content block.
 */
@Composable
fun BaseAppScaffold(
    modifier: Modifier = Modifier,
    timeText: @Composable () -> Unit = { ResponsiveTimeText() },
    content: @Composable BoxScope.() -> Unit,
) {
    val scaffoldState = LocalScaffoldState.current.apply {
        appTimeText.value = timeText
    }

    Scaffold(
        modifier = modifier,
        timeText = scaffoldState.timeText,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

/**
 * An app scaffold, that

 */
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    timeText: @Composable () -> Unit = { TimeText() },
    content: @Composable BoxScope.() -> Unit,
) {
    val currentConfig = LocalConfiguration.current
    val isWatch = max(currentConfig.screenWidthDp, currentConfig.screenHeightDp) < 300
    if (isWatch) {
        BaseAppScaffold(modifier, timeText, content)
    } else {
        val screenSizes = intArrayOf(192, 220, 240, 260)
        val screenSizeIx = remember { mutableIntStateOf(0) }
        val screenSize by remember {
            derivedStateOf { screenSizes[screenSizeIx.value] }
        }
        val zooms = floatArrayOf(1.0f, 1.5f, 2.0f)
        val zoomIx = remember { mutableIntStateOf(0) }
        var roundScreen by remember { mutableStateOf(true) }
        val config by remember {
            derivedStateOf {
                Configuration().apply {
                    setTo(currentConfig)
                    screenWidthDp = screenSize
                    screenHeightDp = screenSize
                    densityDpi = 320
                    // Set the screen to round.
                    screenLayout = (screenLayout and Configuration.SCREENLAYOUT_ROUND_MASK.inv()) or
                        (if (roundScreen) Configuration.SCREENLAYOUT_ROUND_YES else
                            Configuration.SCREENLAYOUT_ROUND_NO)

                }
            }
        }
        val currentDensity = LocalDensity.current
        val density = object: Density {
            override val density: Float get() = currentDensity.density * zooms[zoomIx.value]
            override val fontScale: Float get() = currentDensity.fontScale

            override fun equals(other: Any?) : Boolean {
                if (other !is Density) return false
                return density == other.density && fontScale == other.fontScale
            }

            override fun hashCode() = 31 * density.toBits() + fontScale.toBits()
        }

        Column(
            Modifier
                .background(Color.DarkGray)
                .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
            CompositionLocalProvider(
                LocalConfiguration provides config,
                LocalDensity provides density
            ) {
                ToggleRow("Zoom", zooms.map { it.toString() }.toTypedArray(), zoomIx, 60.dp)
                val shape = if (roundScreen) CircleShape else RoundedCornerShape(0)
                Box(
                    Modifier
                        .border(2.dp, Color.Red, shape)
                        .clip(shape)
                        .size(screenSize.dp)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    BaseAppScaffold(modifier, timeText, content)
                }
            }
            ToggleRow("Screen",
                screenSizes.map { it.toString() }.toTypedArray(),
                screenSizeIx, 60.dp)
            Row (horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ToggleButton(
                    checked = roundScreen,
                    onCheckedChange = { roundScreen = !roundScreen }
                ) {
                    Text("Round")
                }
            }
        }
    }
}

@Composable
fun ToggleRow(
    title: String,
    options: Array<String>,
    selected: MutableIntState,
    optionWidth: Dp,
    modifier: Modifier = Modifier
) {
    val heightDp = 40.dp
    val heightPx = with(LocalDensity.current) { heightDp.toPx() }
    Row(modifier.height(heightDp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color(0xFF757179))
        Spacer(Modifier.width(20.dp))
        options.forEachIndexed { ix, text ->
            val shape = if (ix == 0) RoundedCornerShape(
                heightPx / 2, 0f, 0f, heightPx / 2)
            else if (ix == options.lastIndex) RoundedCornerShape(
                0f,
                heightPx / 2,
                heightPx / 2,
                0f
            )
            else RectangleShape

            Box(
                Modifier
                    .width(optionWidth)
                    .height(heightDp)
                    .border(
                        1.dp, Color(0xFF75717A), shape = shape
                    )
                    .clip(shape)
                    .clickable { selected.value = ix }
                    .background(
                        if (ix == selected.value) Color(0xFF4A4458) else Color(0xFF1B1B20),
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) { Text(text, color = Color(0xFFEEEEFF)) }
        }
    }
}

internal val LocalScaffoldState = compositionLocalOf { ScaffoldState() }
