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
import app.cash.paparazzi.androidHome
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams

private val DefaultMaxPercentDifference = 0.1

@ExperimentalHorologistPaparazziApi
public fun WearPaparazzi(
    deviceConfig: DeviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
    theme: String = "android:ThemeOverlay.Material.Dark",
    maxPercentDifference: Double = DefaultMaxPercentDifference,
    round: Boolean = deviceConfig != WEAR_OS_SQUARE,
    snapshotHandler: SnapshotHandler = WearSnapshotHandler(
        determineHandler(maxPercentDifference),
        round = round
    ),
    renderExtensions: Set<RenderExtension> = setOf(),
    renderingMode: SessionParams.RenderingMode = SessionParams.RenderingMode.NORMAL
): Paparazzi {
    return Paparazzi(
        deviceConfig = deviceConfig,
        theme = theme,
        maxPercentDifference = maxPercentDifference,
        snapshotHandler = snapshotHandler,
        renderExtensions = renderExtensions,
        renderingMode = renderingMode,
        // Workaround https://github.com/cashapp/paparazzi/pull/487#issuecomment-1195584088
        environment = detectEnvironment().copy(
            platformDir = "${androidHome()}/platforms/android-32",
            compileSdkVersion = 32
        )
    )
}
