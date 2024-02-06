/*
 * Copyright 2024 The Android Open Source Project
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

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleButton
import com.google.android.horologist.compose.material.ResponsiveDialogContent


class DialogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearDialogApp()
        }
    }
}

@Composable
fun WearDialogApp() {
    val sizes = listOf(190, 200, 210, 220, 230, 240, 250)
    val size = remember { mutableIntStateOf(0) }
    val hasIcon = remember { mutableStateOf(true) }
    val hasTitle = remember { mutableStateOf(true) }
    val hasOkButton = remember { mutableStateOf(true) }
    val hasCancelButton = remember { mutableStateOf(true) }
    val contentTypes = arrayOf("None", "Chips", "Long Text")
    val contentIx = remember { mutableIntStateOf(0) }
    val extraPadding = 4.16f / (100f - 2f * 5.2f)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SizedContainer(screenSize = sizes[size.intValue], roundScreen = true) {
            ResponsiveDialogContent(
                icon = if (hasIcon.value) { {
                    Icon(Icons.Default.ErrorOutline, null)
                } } else null,
                title = if (hasTitle.value) { {
                    Text("Title enim minim veniam, quis ut", textAlign = TextAlign.Center)
                } } else null,
                onOk = if (hasOkButton.value) {{}} else null,
                onCancel = if (hasCancelButton.value) {{}} else null,
            ) {
                when (contentIx.value) {
                    1 -> items(10) {
                        Chip(onClick = { }, label = {
                            Text("Chip $it")
                        })
                    }
                    2 -> {
                        item {
                            Text(
                                "This is a text that may be long enough to span " +
                                    "multiple rows, so it's left aligned.",
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth(1f - 2f * extraPadding)
                            )
                        }
                        // Adding the spaces to previous and next items, this ends up as 12.dp
                        item { Spacer(Modifier.height(4.dp)) }
                        item {
                            Text(
                                "We have another not so long text here.",
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth(1f - 2f * extraPadding)
                            )
                        }
                    }
                }
            }
        }
        ToggleRow(title = "Size",
            options = sizes.map { it.toString() }.toTypedArray(),
            selected = size, optionWidth = 50.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SimpleToggle("Icon", hasIcon)
            SimpleToggle("Title", hasTitle)
            SimpleToggle("OK", hasOkButton)
            SimpleToggle("Cancel", hasCancelButton)
        }
        ToggleRow(title = "Content",
            options = contentTypes,
            selected = contentIx, optionWidth = 100.dp)
    }
}

@Composable
fun SimpleToggle(name: String, value: MutableState<Boolean>) {
    ToggleButton(checked = value.value, onCheckedChange = { value.value = !value.value }) {
        Text(name, style = LocalTextStyle.current.copy(fontSize = 14.sp))
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
        Text(title, color = Color.White)
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

@Composable
fun SizedContainer(
    screenSize: Int,
    roundScreen: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val currentConfig = LocalConfiguration.current
    val config by remember(screenSize, roundScreen) {
        derivedStateOf {
            Configuration().apply {
                setTo(currentConfig)
                screenWidthDp = screenSize
                screenHeightDp = screenSize
                densityDpi = 320
                // Set the screen to round.
                screenLayout =
                    (screenLayout and Configuration.SCREENLAYOUT_ROUND_MASK.inv()) or
                        (if (roundScreen) Configuration.SCREENLAYOUT_ROUND_YES else
                            Configuration.SCREENLAYOUT_ROUND_NO)
            }
        }
    }
    val currentDensity = LocalDensity.current
    val density = object : Density by currentDensity {
        override val density: Float get() = currentDensity.density * ZoomLevel
    }

    CompositionLocalProvider(
        LocalConfiguration provides config,
        LocalDensity provides density
    ) {
        val shape = if (roundScreen) CircleShape else RoundedCornerShape(0)
        Box(
            modifier = modifier
                .border(2.dp, Color.DarkGray, shape)
                .clip(shape)
                .size(screenSize.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center,
            content = { content() }
        )
    }
}

private val ZoomLevel = 1.5f
