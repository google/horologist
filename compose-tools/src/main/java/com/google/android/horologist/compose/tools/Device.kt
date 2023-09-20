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

public val MobvoiTicWatchPro5: Device = Device("Mobvoi TicWatch Pro 5", 466, 2.0f)
public val SamsungGalaxyWatch5: Device = Device("Samsung Galaxy Watch 5", 396, 2.0f)
public val SamsungGalaxyWatch6Large: Device = Device("Samsung Galaxy Watch 6 Large", 480, 2.125f)
public val GooglePixelWatch: Device = Device("Google Pixel Watch", 384, 2.0f)
public val SmallRound: Device = Device("Small Round", 384, 2.0f)
public val LargeRound: Device = Device("Large Round", 454, 2.0f)
