/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.util

import android.content.res.Configuration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

/** Whether the device is considered large screen for layout adjustment purposes. */
internal val Configuration.isLargeScreen: Boolean get() = screenHeightDp > 224

/** Get percentage of screen size in [Dp]. Rounds off the result to next integer. */
internal fun Configuration.getScreenSizeInDpFromPercentage(percent: Float): Dp =
    ceil(screenHeightDp * percent / 100f).dp
