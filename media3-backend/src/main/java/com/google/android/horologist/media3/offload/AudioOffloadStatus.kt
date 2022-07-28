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

package com.google.android.horologist.media3.offload

import androidx.media3.common.Format
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi

@ExperimentalHorologistMedia3BackendApi
public data class AudioOffloadStatus(
    public val offloadSchedulingEnabled: Boolean,
    public val sleepingForOffload: Boolean,
    public val format: Format?,
    public val isPlaying: Boolean,
    public val errors: List<AudioError>,
    public val offloadTimes: OffloadTimes,
    public val strategyStatus: String?,
) {
    public fun snapOffloadTimes(): OffloadTimes = offloadTimes.timesToNow(
        sleepingForOffload, isPlaying
    )
}
