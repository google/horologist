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

package com.google.android.horologist.audioui

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

data class ThemeValues(val name: String, val index: Int, val colors: Colors) {
    val safeName: String
        get() = name.replace("[^A-Za-z0-9]".toRegex(), "")
}

val themeValues = listOf(
    ThemeValues("Blue (Default - AECBFA)", 0, Colors()),
    ThemeValues(
        "Blue (7FCFFF)",
        1,
        Colors(
            primary = Color(0xFF7FCFFF),
            primaryVariant = Color(0xFF3998D3),
            secondary = Color(0xFF6DD58C),
            secondaryVariant = Color(0xFF1EA446)
        )
    ),
    ThemeValues(
        "Lilac (D0BCFF)",
        2,
        Colors(
            primary = Color(0xFFD0BCFF),
            primaryVariant = Color(0xFF9A82DB),
            secondary = Color(0xFF7FCFFF),
            secondaryVariant = Color(0xFF3998D3)
        )
    ),
    ThemeValues(
        "Green (6DD58C)",
        3,
        Colors(
            primary = Color(0xFF6DD58C),
            primaryVariant = Color(0xFF1EA446),
            secondary = Color(0xFFFFBB29),
            secondaryVariant = Color(0xFFD68400),
        )
    ),
    ThemeValues(
        "Green and Red",
        4,
        Colors(
            primary = Color(0xFF6DD58C),
            primaryVariant = Color(0xFF1EA446),
            secondary = Color(0xFFFFBB29),
            secondaryVariant = Color(0xFFD68400),
            onPrimary = Color.Red,
            onSecondary = Color.White
        )
    ),
)
