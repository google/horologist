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
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
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

/**
 * Debug logging that can be enabled.
 */
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
internal fun <T> Flow<T>.throttleLatest(timeframe: Long): Flow<T> =
    flow {
        conflate().collect {
            emit(it)
            delay(timeframe)
        }
    }

/**
 * Interface for Rotary haptic feedback
 */
@ExperimentalHorologistComposeLayoutApi
public interface RotaryHapticFeedback {
    @ExperimentalHorologistComposeLayoutApi
    public fun performHapticFeedback(type: RotaryHapticsType)
}

/**
 * Rotary haptic types
 */
@ExperimentalHorologistComposeLayoutApi
@JvmInline
public value class RotaryHapticsType(private val type: Int) {
    public companion object {
        /**
         * A scroll ticking haptic. Similar to texture haptic - performed each time when
         * a scrollable content is scrolled by a certain distance
         */
        @ExperimentalHorologistComposeLayoutApi
        public val ScrollTick: RotaryHapticsType = RotaryHapticsType(1)

        /**
         * An item focus (snap) haptic. Performed when a scrollable content is snapped
         * to a specific item.
         */
        @ExperimentalHorologistComposeLayoutApi
        public val ScrollItemFocus: RotaryHapticsType = RotaryHapticsType(2)

        /**
         * A limit(overscroll) haptic. Performed when a list reaches the limit
         * (start or end) and can't scroll further
         */
        @ExperimentalHorologistComposeLayoutApi
        public val ScrollLimit: RotaryHapticsType = RotaryHapticsType(3)
    }
}

/**
 * Remember disabled haptics
 */
@ExperimentalHorologistComposeLayoutApi
@Composable
public fun rememberDisabledHaptic(): RotaryHapticFeedback = remember {
    object : RotaryHapticFeedback {
        override fun performHapticFeedback(type: RotaryHapticsType) {
            // Do nothing
        }
    }
}

/**
 * Remember rotary haptic feedback.
 */
@ExperimentalHorologistComposeLayoutApi
@Composable
public fun rememberRotaryHapticFeedback(
    throttleThresholdMs: Long = 40,
    hapticsChannel: Channel<RotaryHapticsType> = rememberHapticChannel(),
    rotaryHaptics: RotaryHapticFeedback = rememberDefaultRotaryHapticFeedback()
): RotaryHapticFeedback {
    return remember(hapticsChannel, rotaryHaptics) {
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

@OptIn(ExperimentalHorologistComposeLayoutApi::class)
@Composable
private fun rememberHapticChannel() =
    remember {
        Channel<RotaryHapticsType>(
            capacity = 2,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

@ExperimentalHorologistComposeLayoutApi
@Composable
public fun rememberDefaultRotaryHapticFeedback(): RotaryHapticFeedback =
    LocalView.current.let { view -> remember { DefaultRotaryHapticFeedback(view) } }

/**
 * Default Rotary implementation for [RotaryHapticFeedback]
 */
@ExperimentalHorologistComposeLayoutApi
public class DefaultRotaryHapticFeedback(private val view: View) : RotaryHapticFeedback {

    @ExperimentalHorologistComposeLayoutApi
    override fun performHapticFeedback(
        type: RotaryHapticsType
    ) {
        when (type) {
            RotaryHapticsType.ScrollItemFocus -> {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }

            RotaryHapticsType.ScrollTick -> {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }

            RotaryHapticsType.ScrollLimit -> {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        }
    }

    public companion object {
        // Hidden constants from HapticFeedbackConstants.java
        public const val ROTARY_SCROLL_TICK: Int = 18
        public const val ROTARY_SCROLL_ITEM_FOCUS: Int = 19
        public const val ROTARY_SCROLL_LIMIT: Int = 20
    }
}
