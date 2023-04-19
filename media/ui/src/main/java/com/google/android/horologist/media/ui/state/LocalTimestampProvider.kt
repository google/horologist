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

package com.google.android.horologist.media.ui.state

import android.os.SystemClock
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.android.horologist.media.model.TimestampProvider

/**
 * [TimestampProvider] used to generate timestamps for predicting progress. The timestamps must
 * match those used to create [com.google.android.horologist.media.model.MediaPositionPredictor] and
 * [com.google.android.horologist.media.model.LiveMediaPositionPredictor] instances.
 */
public val LocalTimestampProvider: ProvidableCompositionLocal<TimestampProvider> =
    staticCompositionLocalOf { TimestampProvider { SystemClock.elapsedRealtime() } }
