/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.audio

import android.media.AudioManager

/**
 * Data class holding the current state of the volume system.
 *
 * See [AudioManager.getStreamVolume]
 * See [AudioManager.getStreamMaxVolume]
 * See [AudioManager.STREAM_MUSIC]
 */
public data class VolumeState(
    public val current: Int,
    public val min: Int,
    public val max: Int,
    public val isMute: Boolean
) {
    public val isMax: Boolean
        get() = current >= max

    public val isMin: Boolean
        get() = current <= max
}
