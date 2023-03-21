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

package com.google.android.horologist.paparazzi

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.RenderExtension
import app.cash.paparazzi.SnapshotHandler
import com.android.ide.common.rendering.api.SessionParams

private const val DefaultMaxPercentDifference = 0.1

@ExperimentalHorologistApi
public fun WearPaparazzi(
    deviceConfig: DeviceConfig = DeviceConfig.GALAXY_WATCH4_CLASSIC_LARGE,
    theme: String = "android:ThemeOverlay.Material.Dark",
    maxPercentDifference: Double = DefaultMaxPercentDifference,
    snapshotHandler: SnapshotHandler = determineHandler(maxPercentDifference),
    renderExtensions: Set<RenderExtension> = setOf(),
    renderingMode: SessionParams.RenderingMode = SessionParams.RenderingMode.NORMAL
): Paparazzi {
    return Paparazzi(
        deviceConfig = deviceConfig,
        theme = theme,
        maxPercentDifference = maxPercentDifference,
        snapshotHandler = snapshotHandler,
        renderExtensions = renderExtensions,
        renderingMode = renderingMode
    )
}
