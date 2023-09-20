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

package com.google.android.horologist.compose.tools

import kotlin.math.roundToInt

public data class Device(
    val name: String,
    val screenSizePx: Int,
    val density: Float,
    val fontScale: Float = 1.0f,
    val boldText: Boolean = false,
    val round: Boolean = true,
) {
    val screenSizeDp: Int = (screenSizePx / density).roundToInt()
}

public val MobvoiTicWatchPro5: Device = Device(
    name = "Mobvoi TicWatch Pro 5",
    screenSizePx = 466,
    density = 2.0f,
)
public val SamsungGalaxyWatch5: Device = Device(
    name = "Samsung Galaxy Watch 5",
    screenSizePx = 396,
    density = 2.0f,
)
public val SamsungGalaxyWatch6Large: Device = Device(
    name = "Samsung Galaxy Watch 6 Large",
    screenSizePx = 480,
    density = 2.125f,
)
public val GooglePixelWatch: Device = Device(
    name = "Google Pixel Watch",
    screenSizePx = 384,
    density = 2.0f,
)
public val GenericSmallRound: Device = Device(
    name = "Generic Small Round",
    screenSizePx = 384,
    density = 2.0f,
)
public val GenericLargeRound: Device = Device(
    name = "Generic Large Round",
    screenSizePx = 454,
    density = 2.0f,
)
