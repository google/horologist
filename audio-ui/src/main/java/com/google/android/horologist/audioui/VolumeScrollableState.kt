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

package com.google.android.horologist.audioui

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.VibrationEffect
import android.os.VibrationEffect.Composition
import android.os.Vibrator
import android.os.Vibrator.VIBRATION_EFFECT_SUPPORT_NO
import android.os.Vibrator.VIBRATION_EFFECT_SUPPORT_UNKNOWN
import android.util.Log
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import com.google.android.horologist.audio.ExperimentalAudioApi
import com.google.android.horologist.audio.VolumeRepository

/**
 * ScrollableState integration for VolumeControl to scroll events
 * via RSB/Bezel to trigger volume changes.
 */
@ExperimentalAudioUiApi
@OptIn(ExperimentalAudioApi::class)
public class VolumeScrollableState(
    private val volumeRepository: VolumeRepository,
    private val vibrator: Vibrator
) : ScrollableState {
    override val isScrollInProgress: Boolean
        get() = true

    override fun dispatchRawDelta(delta: Float): Float = scrollableState.dispatchRawDelta(delta)

    private var totalDelta = 0f

    private val scrollableState = ScrollableState { delta ->
        totalDelta += delta

        val changed = when {
            totalDelta > 40f -> {
                volumeRepository.increaseVolume()
                performHaptics()
                true
            }
            totalDelta < -40f -> {
                volumeRepository.decreaseVolume()
                performHaptics()
                true
            }
            else -> false
        }

        if (changed) {
            totalDelta = 0f
        }

        delta
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        scrollableState.scroll(block = block)
    }

    private fun performHaptics() {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            var effect: Int
            for (primitive in COMPOSITION_PRIMITIVES) {
                effect = vibrator.areAllEffectsSupported(primitive)
                if (effect == VIBRATION_EFFECT_SUPPORT_NO) {
                    performStandardHaptics()
                    break
                } else if (effect == VIBRATION_EFFECT_SUPPORT_UNKNOWN) {
                    notSupported()
                    break
                }
                performPremiumHaptics()
            }
        }
    }

    private fun performPremiumHaptics() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            vibrator.vibrate(composition)
        }
    }

    private fun performStandardHaptics() {
        perform(waveform)
    }

    private val composition = createComposition()

    private val waveform = if (VERSION.SDK_INT >= VERSION_CODES.O) {
        TODO("update with a more appropriate waveform")
        VibrationEffect.createWaveform(
            longArrayOf(1000, 100, 1000, 100),
            0
        )
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    private fun perform(effect: VibrationEffect?) {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            vibrator.vibrate(effect)
        }
    }

    private fun notSupported() {
        Log.i(TAG, "Effect not supported")
    }

    private fun createComposition(): VibrationEffect? {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            val composition: Composition = VibrationEffect.startComposition()
            for (primitive in COMPOSITION_PRIMITIVES) {
                composition.addPrimitive(primitive)
            }
            return composition.compose()
        }
        return null
    }

    private companion object {
        private var COMPOSITION_PRIMITIVES: IntArray = intArrayOf(
            Composition.PRIMITIVE_CLICK
        )
        private const val TAG = "VolumeScrollableState"
    }
}
