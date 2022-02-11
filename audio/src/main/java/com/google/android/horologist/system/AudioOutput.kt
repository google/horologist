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

package com.google.android.horologist.system

import android.media.AudioDeviceInfo

sealed interface AudioOutput {
    val id: Int?
    val name: String

    object None : AudioOutput {
        override val name: String = "None"
        override val id: Int? = null
    }

    data class BluetoothHeadset(
        override val id: Int,
        override val name: String,
        val audioDevice: AudioDeviceInfo? = null
    ) : AudioOutput

    data class WatchSpeaker(
        override val id: Int,
        override val name: String,
        val audioDevice: AudioDeviceInfo? = null
    ) : AudioOutput

    data class Unknown(
        override val id: Int,
        override val name: String,
        val audioDevice: AudioDeviceInfo? = null
    ) : AudioOutput

    companion object {
        fun fromDevice(audioDevice: AudioDeviceInfo): AudioOutput? {
            if (audioDevice.isSink) {
                val type = audioDevice.type
                val name = audioDevice.productName.toString()
                return when (type) {
                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> WatchSpeaker(
                        audioDevice.id,
                        name,
                        audioDevice
                    )
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> BluetoothHeadset(
                        audioDevice.id,
                        name,
                        audioDevice
                    )
                    else -> Unknown(audioDevice.id, name, audioDevice)
                }
            }

            return null
        }
    }
}
