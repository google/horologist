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
import com.android.resources.ScreenRound

// A11y tests shouldn't be automatically cropped to a circle
public val RoundNonFullScreenDevice: DeviceConfig =
    DeviceConfig.GALAXY_WATCH4_CLASSIC_LARGE.copy(screenRound = ScreenRound.NOTROUND)
