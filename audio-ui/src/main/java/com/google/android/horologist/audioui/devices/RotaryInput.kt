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

package com.google.android.horologist.audioui.devices

import android.os.Build

public sealed interface RotaryInput {
    public val needsHaptic: Boolean
    public val rotaryPixelsForVolume: Int

    public data class HardwareBezel(override val rotaryPixelsForVolume: Int) : RotaryInput {
        override val needsHaptic: Boolean = false
    }

    public data class SoftwareBezel(override val rotaryPixelsForVolume: Int) : RotaryInput {
        override val needsHaptic: Boolean = true
    }

    public data class Crown(override val rotaryPixelsForVolume: Int) : RotaryInput {
        override val needsHaptic: Boolean = true
    }

    public companion object {
        public val Emulator: SoftwareBezel = SoftwareBezel(rotaryPixelsForVolume = 100)
        public val SamsungGw4Classic: HardwareBezel = HardwareBezel(rotaryPixelsForVolume = 136)
        public val SamsungGw4: SoftwareBezel = SoftwareBezel(rotaryPixelsForVolume = 136)

        public fun instance(): RotaryInput? {
            return if (Build.PRODUCT.startsWith("sdk_gwear_")) {
                Emulator
            } else if (Build.MODEL.matches("SM-R8[89]5.".toRegex())) {
                SamsungGw4Classic
            } else if (Build.MODEL.matches("SM-R8[67]5.".toRegex())) {
                SamsungGw4
            } else {
                null
            }
        }
    }
}