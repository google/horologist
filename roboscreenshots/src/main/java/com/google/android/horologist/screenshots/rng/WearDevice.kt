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

package com.google.android.horologist.screenshots.rng

import kotlin.math.roundToInt

public data class WearDevice(
    public val id: String,
    public val modelName: String,
    public val screenSizePx: Int,
    public val density: Float,
    public val fontScale: Float = 1f,
    public val isRound: Boolean = true
) {
    companion object {
        val MobvoiTicWatchPro5 = WearDevice(
            id = "ticwatch_pro_5",
            modelName = "Mobvoi TicWatch Pro 5",
            screenSizePx = 466,
            density = 2.0f,
        )
        val SamsungGalaxyWatch5 = WearDevice(
            id = "galaxy_watch_5",
            modelName = "Samsung Galaxy Watch 5",
            screenSizePx = 396,
            density = 2.0f,
        )
        val SamsungGalaxyWatch6 = WearDevice(
            id = "galaxy_watch_6",
            modelName = "Samsung Galaxy Watch 6 Large",
            screenSizePx = 480,
            density = 2.125f,
        )
        val GooglePixelWatch = WearDevice(
            id = "pixel_watch",
            modelName = "Google Pixel Watch",
            screenSizePx = 384,
            density = 2.0f,
        )
        val GenericSmallRound = WearDevice(
            id = "small_round",
            modelName = "Generic Small Round",
            screenSizePx = 384,
            density = 2.0f,
        )
        val GenericLargeRound = WearDevice(
            id = "large_round",
            modelName = "Generic Large Round",
            screenSizePx = 454,
            density = 2.0f,
        )
        val SamsungGalaxyWatch6SmallFont = WearDevice(
            id = "galaxy_watch_6_small_font",
            modelName = "Samsung Galaxy Watch 6 Large",
            screenSizePx = 480,
            density = 2.125f,
            fontScale = 0.94f,
        )
        val GooglePixelWatchLargeFont = WearDevice(
            id = "pixel_watch_large_font",
            modelName = "Google Pixel Watch",
            screenSizePx = 384,
            density = 2.0f,
            fontScale = 1.24f,
        )
        val entries = listOf(
            MobvoiTicWatchPro5,
            SamsungGalaxyWatch5,
            SamsungGalaxyWatch6,
            GooglePixelWatch,
            GenericSmallRound,
            GenericLargeRound,
            SamsungGalaxyWatch6SmallFont,
            GooglePixelWatchLargeFont,
        )
    }

    public val dp: Int = (screenSizePx / density).roundToInt()
}
