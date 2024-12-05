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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.wear.ambient.AmbientLifecycleObserver

/**
 * Composable for general handling of changes and updates to ambient status. A new
 * [AmbientState] is generated with any change of ambient state, as well as with any periodic
 * update generated whilst the screen is in ambient mode.
 *
 * This composable changes the behavior of the activity, enabling Always-On. See:
 *
 *     https://developer.android.com/training/wearables/views/always-on).
 *
 * It should be used within each individual screen inside nav routes.
 *
 * @param content Lambda that will be used for building the UI, which is passed the current ambient
 * state.
 */
@Composable
fun AmbientAware(
    content: @Composable (AmbientState) -> Unit,
) {
    // Using AmbientAware correctly relies on there being an Activity context. If there isn't, then
    // gracefully allow the composition of [block], but no ambient-mode functionality is enabled.
    val activity = LocalContext.current.findActivityOrNull()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var ambientState = remember {
        mutableStateOf<AmbientState>(AmbientState.Inactive)
    }

    val observer = remember {
        if (activity != null) {
            AmbientLifecycleObserver(
                activity,
                object : AmbientLifecycleObserver.AmbientLifecycleCallback {
                    override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                        ambientState.value = AmbientState.Ambient(
                            burnInProtectionRequired = ambientDetails.burnInProtectionRequired,
                            deviceHasLowBitAmbient = ambientDetails.deviceHasLowBitAmbient,
                        )
                    }

                    override fun onExitAmbient() {
                        ambientState.value = AmbientState.Interactive
                    }

                    override fun onUpdateAmbient() {
                        val lastAmbientDetails =
                            (ambientState.value as? AmbientState.Ambient)
                        ambientState.value = AmbientState.Ambient(
                            burnInProtectionRequired = lastAmbientDetails?.burnInProtectionRequired == true,
                            deviceHasLowBitAmbient = lastAmbientDetails?.deviceHasLowBitAmbient == true,
                        )
                    }
                },
            ).also { observer ->
                ambientState.value =
                    if (observer.isAmbient) AmbientState.Ambient() else AmbientState.Interactive

                lifecycle.addObserver(observer)
            }
        } else {
            null
        }
    }

    val value = ambientState.value
    CompositionLocalProvider(LocalAmbientState provides value) {
        content(value)
    }
}

val LocalAmbientState = compositionLocalOf<AmbientState> { AmbientState.Inactive }

private fun Context.findActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
