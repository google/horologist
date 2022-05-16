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

package com.google.android.horologist.networks.data

public sealed interface NetworkType {
    public val name: String?
    public val typeName: String
    public val highBatteryUsage: Boolean?
    public val metered: Boolean?

    public data class Wifi(
        override val name: String,
        public val ssid: String? = null
    ) : NetworkType {
        override val typeName: String = wifi
        override val highBatteryUsage: Boolean = false

        // TODO should this be checked
        override val metered: Boolean = false
    }

    public data class Cellular(
        override val name: String,
        override val metered: Boolean?
    ) : NetworkType {
        override val typeName: String = cell
        override val highBatteryUsage: Boolean = true
    }

    public data class Bluetooth(override val name: String) : NetworkType {
        override val typeName: String = ble
        override val highBatteryUsage: Boolean = false

        // TODO should this be checked
        override val metered: Boolean = false
    }

    public data class Unknown(
        override val name: String? = unknown,
        override val metered: Boolean? = null
    ) : NetworkType {
        override val typeName: String = "unknown"
        override val highBatteryUsage: Boolean? = null
    }

    public companion object {
        public val wifi = "wifi"
        public val cell = "cell"
        public val ble = "ble"
        public val unknown = "unknown"
    }
}
