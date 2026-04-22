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

package com.google.android.horologist.compose.ambient

import androidx.compose.runtime.Immutable
import androidx.wear.ambient.AmbientLifecycleObserver

/**
 * Represent Ambient as updates, with the state and time of change. This is necessary to ensure that
 * when the system provides a (typically) 1min-frequency callback to onUpdateAmbient, the developer
 * may wish to update composables, but the state hasn't changed.
 */
@Immutable
sealed interface AmbientState {
    val displayName: String

    /**
     * Represents that the state of the device is is interactive, and the app is open and being used.
     *
     * This object is used to track whether the application is currently
     * being interacted with by the user.
     */
    data object Interactive : AmbientState {
        override val displayName: String
            get() = "Interactive"
    }

    /**
     * Represents the state of a device, that the app is in ambient mode and not actively updating
     * the display.
     *
     * This class holds information about the ambient display properties, such as
     * whether burn-in protection is required, if the device has low bit ambient display,
     * and the last time the ambient state was updated.
     *
     * @see [AmbientLifecycleObserver.AmbientDetails]
     * @property burnInProtectionRequired Indicates if burn-in protection is necessary for the device.
     * Defaults to false.
     * @property deviceHasLowBitAmbient Specifies if the device has a low bit ambient display.
     * Defaults to false.
     * @property updateTimeMillis The timestamp in milliseconds when the ambient state was last updated.
     * Defaults to the current system time.
     */
    data class Ambient(
        val burnInProtectionRequired: Boolean = false,
        val deviceHasLowBitAmbient: Boolean = false,
        val updateTimeMillis: Long = System.currentTimeMillis(),
    ) :
        AmbientState {
            override val displayName: String
                get() = "Ambient"
        }

    /**
     * Represents the state of a device, that the app isn't currently monitoring the ambient state.
     *
     * @property displayName A user-friendly name for this state, displayed as "Inactive".
     */
    data object Inactive : AmbientState {
        override val displayName: String
            get() = "Inactive"
    }

    val isInteractive: Boolean
        get() = !isAmbient

    val isAmbient: Boolean
        get() = this is Ambient
}
