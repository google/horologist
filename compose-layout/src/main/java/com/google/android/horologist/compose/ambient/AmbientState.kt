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

/**
 * Represent Ambient as updates, with the state and time of change. This is necessary to ensure that
 * when the system provides a (typically) 1min-frequency callback to onUpdateAmbient, the developer
 * may wish to update composables, but the state hasn't changed.
 */
@Immutable
sealed interface AmbientState {
    val name: String

    data class Ambient(
        val burnInProtectionRequired: Boolean = false,
        val deviceHasLowBitAmbient: Boolean = false,
        val updateTimeMillis: Long = System.currentTimeMillis(),
    ) :
        AmbientState {
            override val name: String
                get() = "Ambient"
        }

    data object Interactive : AmbientState {
        override val name: String
            get() = "Interactive"
    }

    data object Inactive : AmbientState {
        override val name: String
            get() = "Inactive"
    }

    val isInteractive: Boolean
        get() = !isAmbient

    val isAmbient: Boolean
        get() = this is Ambient
}
