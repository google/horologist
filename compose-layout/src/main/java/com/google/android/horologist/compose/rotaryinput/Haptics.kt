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
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import kotlin.math.abs

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
 * Handles haptics for rotary usage
 */
@ExperimentalHorologistApi
public interface RotaryHapticHandler {

    /**
     * Handles haptics when scroll is used
     */
    @ExperimentalHorologistApi
    public fun handleScrollHaptic(scrollDelta: Float)

    /**
     * Handles haptics when scroll with snap is used
     */
    @ExperimentalHorologistApi
    public fun handleSnapHaptic(scrollDelta: Float)
}

/**
 * Default implementation of [RotaryHapticHandler]. It handles haptic feedback based
 * on the [scrollableState], scrolled pixels and [hapticsThreshold].
 * Haptic is not fired in this class, instead it's sent to [hapticsChannel]
 * where it'll performed later.
 *
 * @param scrollableState Haptic performed based on this state
 * @param hapticsChannel Channel to which haptic events will be sent
 * @param hapticsThreshold A scroll threshold after which haptic is produced.
 */
@OptIn(ExperimentalHorologistApi::class)
public class DefaultRotaryHapticHandler(
    private val scrollableState: ScrollableState,
    private val hapticsChannel: Channel<RotaryHapticsType>,
    private val hapticsThreshold: Long = 50
) : RotaryHapticHandler {

    private var overscrollHapticTriggered = false
    private var currScrollPosition = 0f
    private var prevHapticsPosition = 0f

    override fun handleScrollHaptic(scrollDelta: Float) {
        if ((scrollDelta > 0 && !scrollableState.canScrollForward) ||
            (scrollDelta < 0 && !scrollableState.canScrollBackward)
        ) {
            if (!overscrollHapticTriggered) {
                trySendHaptic(RotaryHapticsType.ScrollLimit)
                overscrollHapticTriggered = true
            }
        } else {
            overscrollHapticTriggered = false
            currScrollPosition += scrollDelta
            val diff = abs(currScrollPosition - prevHapticsPosition)

            if (diff >= hapticsThreshold) {
                trySendHaptic(RotaryHapticsType.ScrollTick)
                prevHapticsPosition = currScrollPosition
            }
        }
    }

    override fun handleSnapHaptic(scrollDelta: Float) {
        if ((scrollDelta > 0 && !scrollableState.canScrollForward) ||
            (scrollDelta < 0 && !scrollableState.canScrollBackward)
        ) {
            if (!overscrollHapticTriggered) {
                trySendHaptic(RotaryHapticsType.ScrollLimit)
                overscrollHapticTriggered = true
            }
        } else {
            overscrollHapticTriggered = false
            trySendHaptic(RotaryHapticsType.ScrollItemFocus)
        }
    }

    private fun trySendHaptic(rotaryHapticsType: RotaryHapticsType) {
        // Ok to ignore the ChannelResult because we default to capacity = 2 and DROP_OLDEST
        @Suppress("UNUSED_VARIABLE")
        val unused = hapticsChannel.trySend(rotaryHapticsType)
    }
}

/**
 * Interface for Rotary haptic feedback
 */
@ExperimentalHorologistApi
public interface RotaryHapticFeedback {
    @ExperimentalHorologistApi
    public fun performHapticFeedback(type: RotaryHapticsType)
}

/**
 * Rotary haptic types
 */
@ExperimentalHorologistApi
@JvmInline
public value class RotaryHapticsType(private val type: Int) {
    public companion object {
        /**
         * A scroll ticking haptic. Similar to texture haptic - performed each time when
         * a scrollable content is scrolled by a certain distance
         */
        @ExperimentalHorologistApi
        public val ScrollTick: RotaryHapticsType = RotaryHapticsType(1)

        /**
         * An item focus (snap) haptic. Performed when a scrollable content is snapped
         * to a specific item.
         */
        @ExperimentalHorologistApi
        public val ScrollItemFocus: RotaryHapticsType = RotaryHapticsType(2)

        /**
         * A limit(overscroll) haptic. Performed when a list reaches the limit
         * (start or end) and can't scroll further
         */
        @ExperimentalHorologistApi
        public val ScrollLimit: RotaryHapticsType = RotaryHapticsType(3)
    }
}

/**
 * Remember disabled haptics handler
 */
@ExperimentalHorologistApi
@Composable
public fun rememberDisabledHaptic(): RotaryHapticHandler = remember {
    object : RotaryHapticHandler {

        override fun handleScrollHaptic(scrollDelta: Float) {
            // Do nothing
        }

        override fun handleSnapHaptic(scrollDelta: Float) {
            // Do nothing
        }
    }
}

/**
 * Remember rotary haptic handler.
 */
@ExperimentalHorologistApi
@Composable
public fun rememberRotaryHapticHandler(
    scrollableState: ScrollableState,
    throttleThresholdMs: Long = 40,
    hapticsChannel: Channel<RotaryHapticsType> = rememberHapticChannel(),
    rotaryHaptics: RotaryHapticFeedback = rememberDefaultRotaryHapticFeedback()
): RotaryHapticHandler {
    return remember(scrollableState, hapticsChannel, rotaryHaptics) {
        DefaultRotaryHapticHandler(scrollableState, hapticsChannel)
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

@ExperimentalHorologistApi
@Composable
public fun rememberDefaultRotaryHapticFeedback(): RotaryHapticFeedback =
    LocalView.current.let { view -> remember { DefaultRotaryHapticFeedback(view) } }

/**
 * Default Rotary implementation for [RotaryHapticFeedback]
 */
@ExperimentalHorologistApi
public class DefaultRotaryHapticFeedback(private val view: View) : RotaryHapticFeedback {

    @ExperimentalHorologistApi
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
