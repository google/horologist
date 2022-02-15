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

import android.media.AudioDeviceInfo
import android.media.AudioManager

/**
 * A device capable of playing audio.
 *
 * This interface is intentionally open to allow other targets to be defined in future.
 */
public interface AudioOutput {
    /**
     * The id from [AudioManager] if applicable.
     */
    public val id: Int?

    /**
     * The user meaningful display name for the device.
     */
    public val name: String

    /**
     * No current device.
     */
    public object None : AudioOutput {
        override val name: String = "None"
        override val id: Int? = null
    }

    /**
     * A bluetooth headset paired with the watch.
     */
    public data class BluetoothHeadset(
        override val id: Int,
        override val name: String,
        val audioDevice: AudioDeviceInfo? = null
    ) : AudioOutput

    /**
     * The one device watch speaker.
     */
    public data class WatchSpeaker(
        override val id: Int,
        override val name: String,
        val audioDevice: AudioDeviceInfo? = null
    ) : AudioOutput

    /**
     * An unknown device provided by the [AudioManager]
     */
    public data class Unknown(
        override val id: Int,
        override val name: String,
        val audioDevice: AudioDeviceInfo? = null
    ) : AudioOutput
}
