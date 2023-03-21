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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.tile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.tiles.material.Colors
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.compose.tools.WearLargeRoundDevicePreview
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.preview.ThemePreviewTileRenderer

val SampleTheme =
    Colors(0xFF981F68.toInt(), 0xFFFFFFFF.toInt(), 0xFF1C1B1F.toInt(), 0xFFFFFFFF.toInt())

@WearLargeRoundDevicePreview
@Composable
public fun SampleThemePreview() {
    ThemePreviewTile(SampleTheme)
}

@Composable
public fun ThemePreviewTile(theme: Colors) {
    val context = LocalContext.current
    val renderer = remember(theme) { ThemePreviewTileRenderer(context, theme) }

    TileLayoutPreview(state = Unit, resourceState = Unit, renderer = renderer)
}
