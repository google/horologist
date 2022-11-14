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

import android.view.ViewConfiguration
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transformLatest
import kotlin.math.abs
import kotlin.math.sign

private const val DEBUG = false

/**
 * Debug logging that can be enabled.
 */
private inline fun debugLog(generateMsg: () -> String) {
    if (DEBUG) {
        println("RotaryScroll: ${generateMsg()}")
    }
}

/**
 * A modifier which connects rotary events with scrollable.
 * This modifier supports fling.
 *
 *  Fling algorithm:
 * - A scroll with RSB/ Bezel happens.
 * - If this is a first rotary event after the threshold ( by default 200ms), a new scroll
 * session starts by resetting all necessary parameters
 * - A delta value is added into VelocityTracker and a new speed is calculated.
 * - If the current speed is bigger than the previous one,  this value is remembered as
 * a latest fling speed with a timestamp
 * - After each scroll event a fling countdown starts ( by default 70ms) which
 * resets if new scroll event is received
 * - If fling countdown is finished - it means that the finger was probably raised from RSB, there will be no other events and probably
 * this is the last event during this session. After it a fling is triggered.
 * - Fling is stopped when a new scroll event happens
 *
 * The screen containing the scrollable item should request the focus
 * by calling [requestFocus] method
 *
 * ```
 * LaunchedEffect(Unit) {
 *   focusRequester.requestFocus()
 * }
 * ```
 * @param focusRequester Requests the focus for rotary input
 * @param scrollableState Scrollable state which will be scrolled while receiving rotary events
 * @param flingBehavior Logic describing fling behavior.
 * @param rotaryHaptics Class which will handle haptic feedback
 */
@ExperimentalHorologistComposeLayoutApi
@Suppress("ComposableModifierFactory")
@Composable
public fun Modifier.rotaryWithFling(
    focusRequester: FocusRequester,
    scrollableState: ScrollableState,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    rotaryHaptics: RotaryHapticFeedback = rememberRotaryHapticFeedback()
): Modifier = rotaryHandler(
    rotaryScrollHandler = RotaryDefaults.rememberFlingHandler(scrollableState, flingBehavior),
    rotaryHaptics = rotaryHaptics
)
    .focusRequester(focusRequester)
    .focusable()

/**
 * A modifier which connects rotary events with scrollable.
 * This modifier only supports scroll without fling or snap.
 * The screen containing the scrollable item should request the focus
 * by calling [requestFocus] method
 *
 * ```
 * LaunchedEffect(Unit) {
 *   focusRequester.requestFocus()
 * }
 * ```
 * @param focusRequester Requests the focus for rotary input
 * @param scrollableState Scrollable state which will be scrolled while receiving rotary events
 * @param rotaryHaptics Class which will handle haptic feedback
 */
@ExperimentalHorologistComposeLayoutApi
@Suppress("ComposableModifierFactory")
@Composable
public fun Modifier.rotaryWithScroll(
    focusRequester: FocusRequester,
    scrollableState: ScrollableState,
    rotaryHaptics: RotaryHapticFeedback = rememberRotaryHapticFeedback()
): Modifier = rotaryHandler(
    rotaryScrollHandler = RotaryDefaults.rememberFlingHandler(scrollableState, null),
    rotaryHaptics = rotaryHaptics
)
    .focusRequester(focusRequester)
    .focusable()

/**
 * An adapter which connects scrollableState to Rotary
 */
@ExperimentalHorologistComposeLayoutApi
public interface RotaryScrollAdapter {

    /**
     * A scrollable state. Used for performing scroll when Rotary events received
     */
    @ExperimentalHorologistComposeLayoutApi
    public val scrollableState: ScrollableState

    /**
     * Average size of an item. Used for estimating the scrollable distance
     */
    @ExperimentalHorologistComposeLayoutApi
    public fun averageItemSize(): Float

    /**
     * A current item index. Used for scrolling
     */
    @ExperimentalHorologistComposeLayoutApi
    public fun currentItemIndex(): Int

    /**
     * An offset from the centre or the border of the current item.
     */
    @ExperimentalHorologistComposeLayoutApi
    public fun currentItemOffset(): Float
}

/**
 * Defaults for rotary modifiers
 */
@ExperimentalHorologistComposeLayoutApi
public object RotaryDefaults {

    /**
     * Handles scroll with fling.
     * @param scrollableState Scrollable state which will be scrolled while receiving rotary events
     * @param flingBehavior Logic describing Fling behavior. If null - fling will not happen
     * @param isLowRes Whether the input is Low-res (a bezel) or high-res(a crown/rsb)
     */
    @ExperimentalHorologistComposeLayoutApi
    @Composable
    public fun rememberFlingHandler(
        scrollableState: ScrollableState,
        flingBehavior: FlingBehavior? = null,
        isLowRes: Boolean = isLowResInput()
    ): RotaryScrollHandler {
        val viewConfiguration = ViewConfiguration.get(LocalContext.current)

        return remember(scrollableState, flingBehavior, isLowRes) {
            debugLog { "isLowRes : $isLowRes" }
            fun rotaryFlingBehavior() = flingBehavior?.run {
                DefaultRotaryFlingBehavior(
                    scrollableState,
                    flingBehavior,
                    viewConfiguration,
                    flingTimeframe = if (isLowRes) lowResFlingTimeframe else highResFlingTimeframe
                )
            }

            fun scrollBehavior() = AnimationScrollBehavior(scrollableState)

            if (isLowRes) {
                LowResRotaryScrollHandler(
                    rotaryFlingBehaviorFactory = { rotaryFlingBehavior() },
                    scrollBehaviorFactory = { scrollBehavior() }
                )
            } else {
                HighResRotaryScrollHandler(
                    rotaryFlingBehaviorFactory = { rotaryFlingBehavior() },
                    scrollBehaviorFactory = { scrollBehavior() }
                )
            }
        }
    }

    @ExperimentalHorologistComposeLayoutApi
    @Composable
    private fun isLowResInput(): Boolean = LocalContext.current.packageManager
        .hasSystemFeature("android.hardware.rotaryencoder.lowres")

    private val lowResFlingTimeframe: Long = 100L
    private val highResFlingTimeframe: Long = 40L
}

/**
 * An interface for handling scroll events
 */
@ExperimentalHorologistComposeLayoutApi
public interface RotaryScrollHandler {
    /**
     * Handles scrolling events
     * @param coroutineScope A scope for performing async actions
     * @param event A scrollable event from rotary input, containing scrollable delta and timestamp
     * @param rotaryHaptics
     */
    @ExperimentalHorologistComposeLayoutApi
    public suspend fun handleScrollEvent(
        coroutineScope: CoroutineScope,
        event: TimestampedDelta,
        rotaryHaptics: RotaryHapticFeedback
    )
}

/**
 * An interface for scrolling behavior
 */
@ExperimentalHorologistComposeLayoutApi
public interface RotaryScrollBehavior {
    /**
     * Handles scroll event with [pixelsToScroll] and [timestamp]
     */
    @ExperimentalHorologistComposeLayoutApi
    public suspend fun handleEvent(
        pixelsToScroll: Float,
        timestamp: Long
    )
}

/**
 * Default implementation of [RotaryFlingBehavior]
 */
@ExperimentalHorologistComposeLayoutApi
public class DefaultRotaryFlingBehavior(
    private val scrollableState: ScrollableState,
    private val flingBehavior: FlingBehavior,
    viewConfiguration: ViewConfiguration,
    private val flingTimeframe: Long
) : RotaryFlingBehavior {

    // A time range during which the fling is valid.
    // For simplicity it's twice as long as [flingTimeframe]
    private val timeRangeToFling = flingTimeframe * 2

    //  A default fling factor for making fling slower
    private val flingScaleFactor = 0.7f

    private var previousVelocity = 0f

    private val rotaryVelocityTracker = RotaryVelocityTracker()

    private val minFlingSpeed = viewConfiguration.scaledMinimumFlingVelocity.toFloat()
    private val maxFlingSpeed = viewConfiguration.scaledMaximumFlingVelocity.toFloat()
    private var latestEventTimestamp: Long = 0

    private var flingVelocity: Float = 0f
    private var flingTimestamp: Long = 0

    @ExperimentalHorologistComposeLayoutApi
    override fun startFlingTracking(timestamp: Long) {
        rotaryVelocityTracker.start(timestamp)
        latestEventTimestamp = timestamp
        previousVelocity = 0f
    }

    @ExperimentalHorologistComposeLayoutApi
    override fun observeEvent(timestamp: Long, delta: Float) {
        rotaryVelocityTracker.move(timestamp, delta)
        latestEventTimestamp = timestamp
    }

    @ExperimentalHorologistComposeLayoutApi
    override suspend fun trackFling(beforeFling: () -> Unit) {
        val currentVelocity = rotaryVelocityTracker.velocity
        debugLog { "currentVelocity: $currentVelocity" }

        if (abs(currentVelocity) >= abs(previousVelocity)) {
            flingTimestamp = latestEventTimestamp
            flingVelocity = currentVelocity * flingScaleFactor
        }
        previousVelocity = currentVelocity

        // Waiting for a fixed amount of time before checking the fling
        delay(flingTimeframe)

        // For making a fling 2 criteria should be met:
        // 1) no more than
        // `rangeToFling` ms should pass between last fling detection
        // and the time of last motion event
        // 2) flingVelocity should exceed the minFlingSpeed
        debugLog {
            "Check fling:  flingVelocity: $flingVelocity " +
                "minFlingSpeed: $minFlingSpeed, maxFlingSpeed: $maxFlingSpeed"
        }
        if (latestEventTimestamp - flingTimestamp < timeRangeToFling &&
            abs(flingVelocity) > minFlingSpeed
        ) {
            // Stops scrollAnimationCoroutine because a fling will be performed
            beforeFling()
            val velocity = flingVelocity.coerceIn(-maxFlingSpeed, maxFlingSpeed)
            scrollableState.scroll(MutatePriority.UserInput) {
                with(flingBehavior) {
                    debugLog { "Flinging with velocity $velocity" }
                    performFling(velocity)
                }
            }
        }
    }
}

/**
 * An interface for flinging with rotary
 */
@ExperimentalHorologistComposeLayoutApi
public interface RotaryFlingBehavior {

    /**
     * Observing new event within a fling tracking session with new timestamp and delta
     */
    @ExperimentalHorologistComposeLayoutApi
    public fun observeEvent(timestamp: Long, delta: Float)

    /**
     * Performing fling if necessary and calling [beforeFling] lambda before it is triggered
     */
    @ExperimentalHorologistComposeLayoutApi
    public suspend fun trackFling(beforeFling: () -> Unit)

    /**
     * Starts a new fling tracking session
     * with specified timestamp
     */
    @ExperimentalHorologistComposeLayoutApi
    public fun startFlingTracking(timestamp: Long)
}

/**
 * A rotary event object which contains a [timestamp] of the rotary event and a scrolled [delta].
 */
@ExperimentalHorologistComposeLayoutApi
public data class TimestampedDelta(val timestamp: Long, val delta: Float)

/** Animation implementation of [RotaryScrollBehavior].
 * This class does a smooth animation when the scroll by N pixels is done.
 * This animation works well on Rsb(high-res) and Bezel(low-res) devices.
 */
@ExperimentalHorologistComposeLayoutApi
public class AnimationScrollBehavior(
    private val scrollableState: ScrollableState
) : RotaryScrollBehavior {
    private var rotaryScrollDistance = 0f

    private var sequentialAnimation = false
    private var scrollAnimation = AnimationState(0f)
    private var prevPosition = 0f
    private var previousScrollEventTime = 0L

    @ExperimentalHorologistComposeLayoutApi
    override suspend fun handleEvent(
        pixelsToScroll: Float,
        timestamp: Long
    ) {
        rotaryScrollDistance += pixelsToScroll

        previousScrollEventTime = timestamp
        scrollableState.scroll(MutatePriority.UserInput) {
            debugLog { "ScrollAnimation value before start: ${scrollAnimation.value}" }

            scrollAnimation.animateTo(
                rotaryScrollDistance,
                animationSpec = spring(),
                sequentialAnimation = sequentialAnimation
            ) {
                val delta = value - prevPosition
                debugLog { "Animated by $delta, value: $value" }
                scrollBy(delta)
                prevPosition = value
                sequentialAnimation = value != this.targetValue
            }
        }
    }
}

/**
 * A modifier which handles rotary events.
 * It accepts ScrollHandler as the input - a class where main logic about how
 * scroll should be handled is lying
 */
@ExperimentalHorologistComposeLayoutApi
@OptIn(ExperimentalComposeUiApi::class)
public fun Modifier.rotaryHandler(
    rotaryScrollHandler: RotaryScrollHandler,
    batchTimeframe: Long = 0L,
    rotaryHaptics: RotaryHapticFeedback
): Modifier = composed {
    val channel = rememberTimestampChannel()
    val eventsFlow = remember(channel) { channel.receiveAsFlow() }

    composed {
        LaunchedEffect(eventsFlow) {
            eventsFlow
                // TODO: batching causes additional delays.
                // Do we really need to do this on this level?
                .batchRequestsWithinTimeframe(batchTimeframe)
                .collectLatest {
                    debugLog {
                        "Scroll event received: " +
                            "delta:${it.delta}, timestamp:${it.timestamp}"
                    }
                    rotaryScrollHandler.handleScrollEvent(this, it, rotaryHaptics)
                }
        }
        this
            .onRotaryScrollEvent {
                channel.trySend(
                    TimestampedDelta(
                        it.uptimeMillis,
                        it.verticalScrollPixels
                    )
                )
                true
            }
    }
}

/**
 * Batching requests for scrolling events. This function combines all events together
 * (except first) within specified timeframe. Should help with performance on high-res devices.
 */
@ExperimentalHorologistComposeLayoutApi
@OptIn(ExperimentalCoroutinesApi::class)
public fun Flow<TimestampedDelta>.batchRequestsWithinTimeframe(timeframe: Long): Flow<TimestampedDelta> {
    var delta = 0f
    var lastTimestamp = -timeframe
    return if (timeframe == 0L) {
        this
    } else {
        this.transformLatest {
            delta += it.delta
            debugLog { "Batching requests. delta:$delta" }
            if (lastTimestamp + timeframe <= it.timestamp) {
                lastTimestamp = it.timestamp
                debugLog { "No events before, delta= $delta" }
                emit(TimestampedDelta(it.timestamp, delta))
            } else {
                delay(timeframe)
                debugLog { "After delay, delta= $delta" }
                if (delta > 0f) {
                    emit(TimestampedDelta(it.timestamp, delta))
                }
            }
            delta = 0f
        }
    }
}

/**
 * A scroll handler for RSB(high-res) without snapping and with or without fling
 * A list is scrolled by the number of pixels received from the rotary device.
 *
 * This class is a little bit different from LowResScrollHandler class - it has a filtering
 * for events which are coming with wrong sign ( this happens to rsb devices,
 * especially at the end of the scroll)
 *
 * This scroll handler supports fling. It can be set with [RotaryFlingBehavior].
 */
@OptIn(ExperimentalHorologistComposeLayoutApi::class)
internal class HighResRotaryScrollHandler(
    private val rotaryFlingBehaviorFactory: () -> RotaryFlingBehavior?,
    private val scrollBehaviorFactory: () -> RotaryScrollBehavior,
    private val hapticsThreshold: Long = 50
) : RotaryScrollHandler {

    // This constant is specific for high-res devices. Because that input values
    // can sometimes come with different sign, we have to filter them in this threshold
    private val gestureThresholdTime = 200L
    private var scrollJob: Job = CompletableDeferred<Unit>()
    private var flingJob: Job = CompletableDeferred<Unit>()

    private var previousScrollEventTime = 0L
    private var rotaryScrollDistance = 0f

    private var rotaryFlingBehavior: RotaryFlingBehavior? = rotaryFlingBehaviorFactory()
    private var scrollBehavior: RotaryScrollBehavior = scrollBehaviorFactory()

    private var prevHapticsPosition = 0f
    private var currScrollPosition = 0f

    override suspend fun handleScrollEvent(
        coroutineScope: CoroutineScope,
        event: TimestampedDelta,
        rotaryHaptics: RotaryHapticFeedback
    ) {
        val time = event.timestamp

        if (isNewScrollEvent(time)) {
            debugLog { "New scroll event" }
            resetTracking(time)
            rotaryScrollDistance = event.delta
        } else {
            // Filter out opposite axis values from end of scroll, also some values
            // at the start of motion which sometimes appear with a different sign
            if (isOppositeValueAfterScroll(event.delta)) {
                debugLog { "Opposite value after scroll. Filtering:${event.delta}" }
                return
            }
            rotaryScrollDistance += event.delta
            rotaryFlingBehavior?.observeEvent(event.timestamp, event.delta)
        }

        scrollJob.cancel()
        flingJob.cancel()

        handleHaptic(rotaryHaptics, event.delta)
        debugLog { "Rotary scroll distance: $rotaryScrollDistance" }

        previousScrollEventTime = time
        scrollJob = coroutineScope.async {
            scrollBehavior.handleEvent(event.delta, event.timestamp)
        }

        flingJob = coroutineScope.async {
            rotaryFlingBehavior?.trackFling(
                beforeFling = {
                    debugLog { "Calling before fling section" }
                    scrollJob.cancel()
                    scrollBehavior = scrollBehaviorFactory()
                }
            )
        }
    }

    private fun isOppositeValueAfterScroll(delta: Float): Boolean =
        sign(rotaryScrollDistance) * sign(delta) == -1f &&
            (abs(delta) < abs(rotaryScrollDistance))

    private fun isNewScrollEvent(timestamp: Long): Boolean {
        val timeDelta = timestamp - previousScrollEventTime
        return previousScrollEventTime == 0L || timeDelta > gestureThresholdTime
    }

    private fun resetTracking(timestamp: Long) {
        scrollBehavior = scrollBehaviorFactory()
        rotaryFlingBehavior = rotaryFlingBehaviorFactory()
        rotaryFlingBehavior?.startFlingTracking(timestamp)
    }

    private fun handleHaptic(rotaryHaptics: RotaryHapticFeedback, scrollDelta: Float) {
        currScrollPosition += scrollDelta
        val diff = abs(currScrollPosition - prevHapticsPosition)

        if (diff >= hapticsThreshold) {
            rotaryHaptics.performHapticFeedback(RotaryHapticsType.ScrollTick)
            prevHapticsPosition = currScrollPosition
        }
    }
}

/**
 * A scroll handler for Bezel(low-res) without snapping.
 * This scroll handler supports fling. It can be set with RotaryFlingBehavior.
 */
@OptIn(ExperimentalHorologistComposeLayoutApi::class)
internal class LowResRotaryScrollHandler(
    private val rotaryFlingBehaviorFactory: () -> RotaryFlingBehavior?,
    private val scrollBehaviorFactory: () -> RotaryScrollBehavior
) : RotaryScrollHandler {

    private val gestureThresholdTime = 200L
    private var previousScrollEventTime = 0L
    private var rotaryScrollDistance = 0f

    private var scrollJob: Job = CompletableDeferred<Unit>()
    private var flingJob: Job = CompletableDeferred<Unit>()

    private var rotaryFlingBehavior: RotaryFlingBehavior? = rotaryFlingBehaviorFactory()
    private var scrollBehavior: RotaryScrollBehavior = scrollBehaviorFactory()

    override suspend fun handleScrollEvent(
        coroutineScope: CoroutineScope,
        event: TimestampedDelta,
        rotaryHaptics: RotaryHapticFeedback
    ) {
        val time = event.timestamp

        if (isNewScrollEvent(time)) {
            resetTracking(time)
        } else {
            rotaryFlingBehavior?.observeEvent(event.timestamp, event.delta)
        }

        scrollJob.cancel()
        flingJob.cancel()

        rotaryHaptics.performHapticFeedback(RotaryHapticsType.ScrollItemFocus)
        debugLog { "Rotary scroll distance: $rotaryScrollDistance" }

        previousScrollEventTime = time
        scrollJob = coroutineScope.async {
            scrollBehavior.handleEvent(event.delta, event.timestamp)
        }

        flingJob = coroutineScope.async {
            rotaryFlingBehavior?.trackFling(
                beforeFling = {
                    debugLog { "Calling before fling section" }
                    scrollJob.cancel()
                    scrollBehavior = scrollBehaviorFactory()
                }
            )
        }
    }

    private fun isNewScrollEvent(timestamp: Long): Boolean {
        val timeDelta = timestamp - previousScrollEventTime
        return previousScrollEventTime == 0L || timeDelta > gestureThresholdTime
    }

    private fun resetTracking(timestamp: Long) {
        scrollBehavior = scrollBehaviorFactory()
        debugLog { "Velocity tracker reset" }
        rotaryFlingBehavior = rotaryFlingBehaviorFactory()
        rotaryFlingBehavior?.startFlingTracking(timestamp)
    }
}

@OptIn(ExperimentalHorologistComposeLayoutApi::class)
@Composable
private fun rememberTimestampChannel() =
    remember {
        Channel<TimestampedDelta>(capacity = Channel.CONFLATED)
    }
