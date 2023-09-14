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
import android.content.Context
import android.content.ContextWrapper
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
 *
 * @param isAlwaysOnScreen If supplied, this indicates whether always-on should be enabled. This can
 * be used to ensure that some screens display an ambient-mode version, whereas others do not, for
 * example, a workout screen vs a end-of-workout summary screen.
 * @param block Lambda that will be used for building the UI, which is passed the current ambient
 * state.
 */
@Composable
fun AmbientAware(
    isAlwaysOnScreen: Boolean = true,
    block: @Composable (AmbientStateUpdate) -> Unit,
) {
    val activity = LocalContext.current.findActivityOrNull()
    // Using AmbientAware correctly relies on there being an Activity context. If there isn't, then
    // gracefully allow the composition of [block], but no ambient-mode functionality is enabled.
    if (activity != null && isAlwaysOnScreen) {
        AmbientAwareEnabled(activity, block)
    } else {
        AmbientAwareDisabled(block)
    }
}

@Composable
private fun AmbientAwareEnabled(
    activity: Activity,
    block: @Composable (AmbientStateUpdate) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var ambientUpdate by remember { mutableStateOf<AmbientStateUpdate?>(null) }

    val observer = remember {
        val callback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
            override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                ambientUpdate = AmbientStateUpdate(AmbientState.Ambient(ambientDetails))
            }

            override fun onExitAmbient() {
                ambientUpdate = AmbientStateUpdate(AmbientState.Interactive)
            }

            override fun onUpdateAmbient() {
                val lastAmbientDetails =
                    (ambientUpdate?.ambientState as? AmbientState.Ambient)?.ambientDetails
                ambientUpdate = AmbientStateUpdate(AmbientState.Ambient(lastAmbientDetails))
            }
        }
        AmbientLifecycleObserver(activity, callback).also {
            // Necessary to populate the initial value
            val initialAmbientState = if (it.isAmbient) {
                AmbientState.Ambient(null)
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

@Composable
private fun AmbientAwareDisabled(block: @Composable (AmbientStateUpdate) -> Unit) {
    val staticAmbientState by remember { mutableStateOf(AmbientStateUpdate(AmbientState.Interactive)) }
    block(staticAmbientState)
}

private fun Context.findActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
