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

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.wear.ambient.AmbientLifecycleObserver

/**
 * Composable for general handling of changes and updates to ambient status. A new
 * [AmbientStateUpdate] is generated with any change of ambient state, as well as with any periodic
 * update generated whilst the screen is in ambient mode.
 *
 * This composable changes the behavior of the activity, enabling Always-On. See:
 *
 *     https://developer.android.com/training/wearables/views/always-on).
 *
 * It should therefore be used high up in the tree of composables.
 */
@Composable
fun AmbientAware(block: @Composable (AmbientStateUpdate) -> Unit) {
    val activity = LocalContext.current as Activity
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var ambientUpdate by remember { mutableStateOf<AmbientStateUpdate?>(null) }
    // On entering ambient mode, the [onEnterAmbient] callback method provides details of burn-in
    // protection and low-bit support. These are stored here such that they can be provided as a
    // convenience with each [AmbientStateUpdate] as [onUpdateAmbient] does not provide them.
    var lastAmbientDetails by remember {
        mutableStateOf<AmbientLifecycleObserver.AmbientDetails?>(
            null,
        )
    }

    val observer = remember {
        val callback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
            override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                lastAmbientDetails = ambientDetails
                ambientUpdate = AmbientStateUpdate(AmbientState.Ambient(ambientDetails))
            }

            override fun onExitAmbient() {
                ambientUpdate = AmbientStateUpdate(AmbientState.Interactive)
            }

            override fun onUpdateAmbient() {
                ambientUpdate = AmbientStateUpdate(AmbientState.Ambient(lastAmbientDetails))
            }
        }
        AmbientLifecycleObserver(activity, callback).also {
            // Necessary to populate the initial value
            val initialAmbientState = if (it.isAmbient) {
                AmbientState.Ambient(lastAmbientDetails)
            } else {
                AmbientState.Interactive
            }
            ambientUpdate = AmbientStateUpdate(initialAmbientState)
        }
    }

    DisposableEffect(Unit) {
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    ambientUpdate?.let {
        block(it)
    }
}
