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

package com.google.android.horologist.compose.rotaryinput

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

private const val DEBUG = false
private inline fun debugLog(generateMsg: () -> String) {
    if (DEBUG) {
        println("RotaryHaptics: ${generateMsg()}")
    }
}

/**
 * Throttling events within specified timeframe. Only first and last events will be received.
 * For a flow emitting elements 1 to 30, with a 100ms delay between them:
 * ```
 * val flow = flow {
 *     for (i in 1..30) {
 *         delay(100)
 *         emit(i)
 *     }
 * }
 * ```
 * With timeframe=1000 only those integers will be received: 1, 10, 20, 30 .
 */
public fun <T> Flow<T>.throttleLatest(timeframe: Long): Flow<T> =
    flow {
        conflate().collect {
            emit(it)
            delay(timeframe)
        }
    }

/**
 * Interface for Rotary haptic feedback
 */
interface RotaryHapticFeedback {
    fun performHapticFeedback(type: RotaryHapticsType)
}

/**
 * Rotary haptic types
 */
@JvmInline
value class RotaryHapticsType(val type: Int) {
    companion object {
        val ScrollTick = RotaryHapticsType(1)
        val ScrollItemFocus = RotaryHapticsType(2)
        val ScrollLimit = RotaryHapticsType(3)
    }
}

/**
 * Remember rotary haptic feedback.
 */
@Composable
fun rememberRotaryHapticFeedback(
    throttleThresholdMs: Long = 40,
    hapticsChannel: Channel<RotaryHapticsType> = rememberHapticChannel(),
    rotaryHaptics: RotaryHapticFeedback = rememberDefaultRotaryHapticFeedback(),
): RotaryHapticFeedback {
    return remember(throttleThresholdMs, hapticsChannel, rotaryHaptics) {
        object : RotaryHapticFeedback {
            override fun performHapticFeedback(type: RotaryHapticsType) {
                hapticsChannel.trySend(type)
            }
        }
    }.apply {
        LaunchedEffect(hapticsChannel) {
            hapticsChannel.receiveAsFlow()
                .throttleLatest(throttleThresholdMs)
                .collect { hapticType ->
                    // 'withContext' launches performHapticFeedback in a separate thread,
                    // as otherwise it produces a visible lag (b/219776664)
                    val currentTime = System.currentTimeMillis()
                    debugLog { "Haptics started" }
                    withContext(Dispatchers.Default) {
                        debugLog {
                            "Performing haptics, delay: " +
                                "${System.currentTimeMillis() - currentTime}"
                        }
                        rotaryHaptics.performHapticFeedback(hapticType)
                    }
                }
        }
    }
}

@Composable
private fun rememberHapticChannel() =
    remember {
        Channel<RotaryHapticsType>(
            capacity = 2,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

@Composable
fun rememberDefaultRotaryHapticFeedback() =
    LocalView.current.let { view -> remember { DefaultRotaryHapticFeedback(view) } }

/**
 * Default Rotary implementation for [RotaryHapticFeedback]
 */
class DefaultRotaryHapticFeedback(private val view: View) : RotaryHapticFeedback {

    override fun performHapticFeedback(
        hapticType: RotaryHapticsType,
    ) {

        when (hapticType) {
            RotaryHapticsType.ScrollItemFocus -> {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
            RotaryHapticsType.ScrollTick -> {
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
            RotaryHapticsType.ScrollLimit -> {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        }
    }

    companion object {
        // Hidden constants from HapticFeedbackConstants.java
        const val ROTARY_SCROLL_TICK = 18
        const val ROTARY_SCROLL_ITEM_FOCUS = 19
        const val ROTARY_SCROLL_LIMIT = 20
    }
}