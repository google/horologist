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

@file:Suppress("DEPRECATION")

package com.google.android.horologist.compose.rotaryinput

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.annotation.ChecksSdkIntAtLeast
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
@Deprecated("Replaced by wear compose")
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
 * on the [scrollableState], scrolled pixels and [hapticsThresholdPx].
 * Haptic is not fired in this class, instead it's sent to [hapticsChannel]
 * where it'll performed later.
 *
 * @param scrollableState Haptic performed based on this state
 * @param hapticsChannel Channel to which haptic events will be sent
 * @param hapticsThresholdPx A scroll threshold after which haptic is produced.
 */
@Deprecated("Replaced by wear compose")
public class DefaultRotaryHapticHandler(
    private val scrollableState: ScrollableState,
    private val hapticsChannel: Channel<RotaryHapticsType>,
    private val hapticsThresholdPx: Long = 50,
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

            if (diff >= hapticsThresholdPx) {
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
@Deprecated("Replaced by wear compose")
@ExperimentalHorologistApi
public interface RotaryHapticFeedback {
    @ExperimentalHorologistApi
    public fun performHapticFeedback(type: RotaryHapticsType)
}

/**
 * Rotary haptic types
 */
@Deprecated("Replaced by wear compose")
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
@Deprecated("Replaced by wear compose")
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
 * @param scrollableState A scrollableState, used to determine whether the end of the scrollable
 * was reached or not.
 * @param throttleThresholdMs Throttling events within specified timeframe.
 * Only first and last events will be received. Check [throttleLatest] for more info.
 * @param hapticsThresholdPx A scroll threshold after which haptic is produced.
 * @param hapticsChannel Channel to which haptic events will be sent
 * @param rotaryHaptics Interface for Rotary haptic feedback which performs haptics
 */
@Deprecated("Replaced by wear compose")
@ExperimentalHorologistApi
@Composable
public fun rememberRotaryHapticHandler(
    scrollableState: ScrollableState,
    throttleThresholdMs: Long = 30,
    hapticsThresholdPx: Long = 50,
    hapticsChannel: Channel<RotaryHapticsType> = rememberHapticChannel(),
    rotaryHaptics: RotaryHapticFeedback = rememberDefaultRotaryHapticFeedback(),
): RotaryHapticHandler {
    return remember(scrollableState, hapticsChannel, rotaryHaptics) {
        DefaultRotaryHapticHandler(scrollableState, hapticsChannel, hapticsThresholdPx)
    }.apply {
        LaunchedEffect(hapticsChannel) {
            hapticsChannel.receiveAsFlow()
                .throttleLatest(throttleThresholdMs)
                .collect { hapticType ->
                    // 'withContext' launches performHapticFeedback in a separate thread,
                    // as otherwise it produces a visible lag (b/219776664)
                    withContext(Dispatchers.Default) {
                        rotaryHaptics.performHapticFeedback(hapticType)
                    }
                }
        }
    }
}

@Deprecated("Replaced by wear compose")
@Composable
private fun rememberHapticChannel() =
    remember {
        Channel<RotaryHapticsType>(
            capacity = 2,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }

@Deprecated("Replaced by wear compose")
@ExperimentalHorologistApi
@Composable
public fun rememberDefaultRotaryHapticFeedback(): RotaryHapticFeedback =
    LocalView.current.let { view -> remember { findDeviceSpecificHapticFeedback(view) } }

internal fun findDeviceSpecificHapticFeedback(view: View): RotaryHapticFeedback =
    if (isGalaxyWatchClassic() || isGalaxyWatch()) {
        GalaxyWatchHapticFeedback(view)
    } else if (isWear3point5(view.context)) {
        Wear3point5RotaryHapticFeedback(view)
    } else if (isWear4AtLeast()) {
        Wear4AtLeastRotaryHapticFeedback(view)
    } else {
        DefaultRotaryHapticFeedback(view)
    }

/**
 * Default Rotary implementation for [RotaryHapticFeedback]
 */
@Deprecated("Replaced by wear compose")
@ExperimentalHorologistApi
private class DefaultRotaryHapticFeedback(private val view: View) : RotaryHapticFeedback {

    @ExperimentalHorologistApi
    override fun performHapticFeedback(
        type: RotaryHapticsType,
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
}

/**
 * Implementation of [RotaryHapticFeedback] for Pixel Watch
 */
@Deprecated("Replaced by wear compose")
@ExperimentalHorologistApi
private class Wear3point5RotaryHapticFeedback(private val view: View) : RotaryHapticFeedback {

    @ExperimentalHorologistApi
    override fun performHapticFeedback(
        type: RotaryHapticsType,
    ) {
        when (type) {
            RotaryHapticsType.ScrollItemFocus -> {
                view.performHapticFeedback(WEAR_SCROLL_ITEM_FOCUS)
            }

            RotaryHapticsType.ScrollTick -> {
                view.performHapticFeedback(WEAR_SCROLL_TICK)
            }

            RotaryHapticsType.ScrollLimit -> {
                view.performHapticFeedback(WEAR_SCROLL_LIMIT)
            }
        }
    }

    private companion object {
        // Hidden constants from HapticFeedbackConstants.java specific for Wear 3.5
        // API 30, Wear 3.5
        public const val WEAR_SCROLL_TICK: Int = 10002
        public const val WEAR_SCROLL_ITEM_FOCUS: Int = 10003
        public const val WEAR_SCROLL_LIMIT: Int = 10003
    }
}

/**
 * Implementation of [RotaryHapticFeedback] for Pixel Watch
 */
@Deprecated("Replaced by wear compose")
@ExperimentalHorologistApi
private class Wear4AtLeastRotaryHapticFeedback(private val view: View) : RotaryHapticFeedback {

    @ExperimentalHorologistApi
    override fun performHapticFeedback(
        type: RotaryHapticsType,
    ) {
        when (type) {
            RotaryHapticsType.ScrollItemFocus -> {
                view.performHapticFeedback(ROTARY_SCROLL_ITEM_FOCUS)
            }

            RotaryHapticsType.ScrollTick -> {
                view.performHapticFeedback(ROTARY_SCROLL_TICK)
            }

            RotaryHapticsType.ScrollLimit -> {
                view.performHapticFeedback(ROTARY_SCROLL_LIMIT)
            }
        }
    }

    private companion object {
        // Hidden constants from HapticFeedbackConstants.java
        // API 33
        public const val ROTARY_SCROLL_TICK: Int = 18
        public const val ROTARY_SCROLL_ITEM_FOCUS: Int = 19
        public const val ROTARY_SCROLL_LIMIT: Int = 20
    }
}

/**
 * Implementation of [RotaryHapticFeedback] for Galaxy Watches
 */
@Deprecated("Replaced by wear compose")
@ExperimentalHorologistApi
private class GalaxyWatchHapticFeedback(private val view: View) : RotaryHapticFeedback {

    @ExperimentalHorologistApi
    override fun performHapticFeedback(
        type: RotaryHapticsType,
    ) {
        when (type) {
            RotaryHapticsType.ScrollItemFocus -> {
                view.performHapticFeedback(102)
            }

            RotaryHapticsType.ScrollTick -> {
                view.performHapticFeedback(102)
            }

            RotaryHapticsType.ScrollLimit -> {
                view.performHapticFeedback(50107)
            }
        }
    }
}

private fun isGalaxyWatch(): Boolean =
    Build.MANUFACTURER.contains("Samsung", ignoreCase = true)

private fun isGalaxyWatchClassic(): Boolean =
    Build.MODEL.matches("SM-R(?:8[89][05]|9[56][05])".toRegex())

private fun isWear3point5(context: Context): Boolean =
    Build.VERSION.SDK_INT == 30 && getWearPlatformMrNumber(context) >= 5

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
private fun isWear4AtLeast(): Boolean =
    Build.VERSION.SDK_INT >= 33

internal fun getWearPlatformMrNumber(context: Context): Int =
    Settings.Global
        .getString(context.contentResolver, WEAR_PLATFORM_MR_NUMBER)?.toIntOrNull() ?: 0

internal const val WEAR_PLATFORM_MR_NUMBER: String = "wear_platform_mr_number"
