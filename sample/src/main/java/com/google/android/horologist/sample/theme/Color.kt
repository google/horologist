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

package com.google.android.horologist.sample.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

val MaterialBlue = Color(0xFFAECBFA)
val MaterialBlueDark = Color(0xFF202124)
val MaterialAlmondDark = Color(0xFF262523)
val MaterialGray400 = Color(0xFFBDC1C6)

/**
 * Custom color palette for Wear OS App.
 */
internal val wearColorPalette: Colors = Colors(
    primary = MaterialBlue,
    surface = MaterialAlmondDark,
    onBackground = MaterialGray400,
)