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

package com.google.android.horologist.mediasample.data.service.offload

import android.annotation.SuppressLint
import androidx.media3.common.Format
import androidx.media3.common.TrackSelectionParameters.AudioOffloadPreferences
import androidx.media3.common.util.UnstableApi
import com.google.android.horologist.annotations.ExperimentalHorologistApi

@ExperimentalHorologistApi
public data class AudioOffloadStatus(
    public val offloadSchedulingEnabled: Boolean,
    public val sleepingForOffload: Boolean,
    public val trackOffload: Boolean = false,
    public val format: Format?,
    public val isPlaying: Boolean,
    public val errors: List<AudioError>,
    public val offloadTimes: OffloadTimes,
    public val audioOffloadPreferences: AudioOffloadPreferences,
) {
    public fun updateToNow(): OffloadTimes = offloadTimes.timesToNow(
        sleepingForOffload,
        isPlaying,
    )
    public fun describe(): String {
        return "Offload State: " +
            "sleeping: $sleepingForOffload " +
            "format: ${format?.shortDescription} " +
            "times: ${offloadTimes.shortDescription}"
    }

    public fun trackOffloadDescription(): String = if (trackOffload) "HW" else "SW"

    public companion object {
        @SuppressLint("UnsafeOptInUsageError")
        public val Disabled: AudioOffloadStatus = AudioOffloadStatus(
            offloadSchedulingEnabled = false,
            sleepingForOffload = false,
            trackOffload = false,
            format = null,
            isPlaying = false,
            errors = listOf(),
            offloadTimes = OffloadTimes(),
            audioOffloadPreferences = AudioOffloadPreferences.Builder()
                .build(),
        )
    }
}
