/*
 * Copyright 2021 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.system

import android.media.AudioManager

/**
 * Data class holding the current state of the volume system.
 *
 * See [AudioManager.getStreamVolume]
 * See [AudioManager.getStreamMaxVolume]
 * See [AudioManager.STREAM_MUSIC]
 */
data class VolumeState(val current: Int, val min: Int, val max: Int, val isMute: Boolean) {
    val isMax: Boolean
        get() = current >= max

    val isMin: Boolean
        get() = current <= max
}