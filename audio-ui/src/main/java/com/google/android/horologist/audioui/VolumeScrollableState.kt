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
public class VolumeScrollableState(private val volumeRepository: VolumeRepository) : ScrollableState {
    override val isScrollInProgress: Boolean
        get() = true

    override fun dispatchRawDelta(delta: Float): Float = scrollableState.dispatchRawDelta(delta)

    private var totalDelta = 0f

    private val scrollableState = ScrollableState { delta ->
        totalDelta += delta

        val changed = when {
            totalDelta > 40f -> {
                volumeRepository.increaseVolume()
                true
            }
            totalDelta < -40f -> {
                volumeRepository.decreaseVolume()
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
}
