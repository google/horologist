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

package com.google.android.horologist.compose.tools

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

public data class ThemeValues(val name: String, val index: Int, val colors: Colors) {
    val safeName: String
        get() = name.replace("[^A-Za-z0-9]".toRegex(), "")
}

public val themeValues: List<ThemeValues> = listOf(
    ThemeValues("Blue (Default - AECBFA)", 0, Colors()),
    ThemeValues(
        "Blue (7FCFFF)",
        1,
        Colors(
            primary = Color(0xFF7FCFFF),
            primaryVariant = Color(0xFF3998D3),
            secondary = Color(0xFF6DD58C),
            secondaryVariant = Color(0xFF1EA446),
        ),
    ),
    ThemeValues(
        "Lilac (D0BCFF)",
        2,
        Colors(
            primary = Color(0xFFD0BCFF),
            primaryVariant = Color(0xFF9A82DB),
            secondary = Color(0xFF7FCFFF),
            secondaryVariant = Color(0xFF3998D3),
        ),
    ),
    ThemeValues(
        "Green (6DD58C)",
        3,
        Colors(
            primary = Color(0xFF6DD58C),
            primaryVariant = Color(0xFF1EA446),
            secondary = Color(0xFFFFBB29),
            secondaryVariant = Color(0xFFD68400),
        ),
    ),
    ThemeValues(
        "Blue with Text (7FCFFF)",
        4,
        Colors(
            primary = Color(0xFF7FCFFF),
            primaryVariant = Color(0xFF3998D3),
            onPrimary = Color(0xFF003355),
            secondary = Color(0xFF6DD58C),
            secondaryVariant = Color(0xFF1EA446),
            onSecondary = Color(0xFF0A3818),
            surface = Color(0xFF303030),
            onSurface = Color(0xFFE3E3E3),
            onSurfaceVariant = Color(0xFFC4C7C5),
            background = Color.Black,
            onBackground = Color.White,
            error = Color(0xFFF2B8B5),
            onError = Color(0xFF370906),
        ),
    ),
    ThemeValues(
        "Orange-y",
        5,
        Colors(
            secondary = Color(0xFFED612B), // Used for RSB
            surface = Color(0xFF202124), // Used for Device Chip
            onPrimary = Color(0xFFED612B),
            onSurface = Color(0xFFED612B),
        ),
    ),
    ThemeValues(
        "Uamp",
        6,
        Colors(
            primary = Color(0xFF981F68),
            primaryVariant = Color(0xFF66003d),
            secondary = Color(0xFF981F68),
            error = Color(0xFFE24444),
            onPrimary = Color.White,
            onSurfaceVariant = Color(0xFFDADCE0),
            surface = Color(0xFF303133),
            onError = Color.Black,
        ),
    ),
)

public data class ThemeColors(
    val primary: Color = Color(0xFFAECBFA),
    val primaryVariant: Color = Color(0xFF8AB4F8),
    val secondary: Color = Color(0xFFFDE293),
    val secondaryVariant: Color = Color(0xFF594F33),
    val background: Color = Color.Black,
    val surface: Color = Color(0xFF303133),
    val error: Color = Color(0xFFEE675C),
    val onPrimary: Color = Color(0xFF303133),
    val onSecondary: Color = Color(0xFF303133),
    val onBackground: Color = Color.White,
    val onSurface: Color = Color.White,
    val onSurfaceVariant: Color = Color(0xFFDADCE0),
    val onError: Color = Color(0xFF000000),
) {
    public fun toColors(): Colors = Colors(
        primary = primary,
        primaryVariant = primaryVariant,
        secondary = secondary,
        secondaryVariant = secondaryVariant,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        onBackground = onBackground,
        onSurface = onSurface,
        onSurfaceVariant = onSurfaceVariant,
        onError = onError,
    )
}
