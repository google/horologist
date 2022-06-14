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

package com.google.android.horologist.media3.flows

import androidx.media3.common.Player
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Create a Flow for the isPlaying state of Player using callbacks.
 */
@ExperimentalHorologistMedia3BackendApi
public fun Player.isPlayingFlow(): Flow<Boolean> = callbackFlow {
    send(isPlaying)

    val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            trySendBlocking(isPlaying)
        }
    }

    addListener(listener)

    awaitClose { removeListener(listener) }
}
