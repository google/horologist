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
private inline fun debugLog(generateMsg: () -> String) {
    if (DEBUG) {
        println("RotaryScroll: ${generateMsg()}")
    }
}

/**
 * A modifier which connects rotary events with scrollable.
 * This modifier supports fling.
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
@Suppress("ComposableModifierFactory")
@Composable
public fun Modifier.rotaryWithFling(
    focusRequester: FocusRequester,
    scrollableState: ScrollableState,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    rotaryHaptics: RotaryHapticFeedback = rememberRotaryHapticFeedback(),
): Modifier = rotaryHandler(
    rotaryScrollHandler = RotaryDefaults.rememberFlingHandler(scrollableState, flingBehavior),
    rotaryHaptics = rotaryHaptics
)
    .focusRequester(focusRequester)
    .focusable()

/**
 * A modifier which connects rotary events with scrollable.
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
 * @param rotaryHaptics Class which will handle haptic feedback
 */
@Suppress("ComposableModifierFactory")
@Composable
public fun Modifier.rotaryWithScroll(
    focusRequester: FocusRequester,
    scrollableState: ScrollableState,
    rotaryHaptics: RotaryHapticFeedback = rememberRotaryHapticFeedback(),
): Modifier = rotaryHandler(
    rotaryScrollHandler = RotaryDefaults.rememberFlingHandler(scrollableState, null),
    rotaryHaptics = rotaryHaptics
)
    .focusRequester(focusRequester)
    .focusable()

/**
 * An adapter which connects scrollableState to Rotary
 */
public interface RotaryScrollAdapter {

    /**
     * A scrollable state. Used for performing scroll when Rotary events received
     */
    val scrollableState: ScrollableState

    /**
     * Average size of an item. Used for estimating the scrollable distance
     */
    fun averageItemSize(): Float

    /**
     * A current item index. Used for scrolling
     */
    fun currentItemIndex(): Int

    /**
     * An offset from the centre or the border of the current item.
     */
    fun currentItemOffset(): Float
}

/**
 * Defaults for rotary modifiers
 */
public object RotaryDefaults {

    /**
     * Handles scroll with fling.
     * @param scrollableState Scrollable state which will be scrolled while receiving rotary events
     * @param flingBehavior Logic describing Fling behaviour. If null - fling will not happen
     * @param isLowRes Whether the input is Low-res (a bezel) or high-res(a crown/rsb)
     */
    @Composable
    public fun rememberFlingHandler(
        scrollableState: ScrollableState,
        flingBehavior: FlingBehavior? = null,
        isLowRes: Boolean = isLowResInput(),
    ): RotaryScrollHandler {
        val viewConfiguration = ViewConfiguration.get(LocalContext.current)

        return remember(scrollableState, flingBehavior, isLowRes) {
            fun rotaryFlingBehavior() = flingBehavior?.run {
                DefaultRotaryFlingBehavior(
                    scrollableState,
                    flingBehavior,
                    viewConfiguration
                )
            }

            fun scrollBehavior() = AnimationScrollBehavior(scrollableState)

            if (isLowRes) {
                LowResRotaryScrollHandler(
                    rotaryFlingBehaviorFactory = { rotaryFlingBehavior() },
                    scrollBehaviourFactory = { scrollBehavior() }
                )
            } else {
                HighResRotaryScrollHandler(
                    rotaryFlingBehaviorFactory = { rotaryFlingBehavior() },
                    scrollBehaviourFactory = { scrollBehavior() }
                )
            }
        }
    }

    @Composable
    private fun isLowResInput(): Boolean = LocalContext.current.packageManager
        .hasSystemFeature("android.hardware.rotaryencoder.lowres")
}

/**
 * An interface for handling scroll events
 */
public interface RotaryScrollHandler {
    /**
     * Handles scrolling events
     * @param coroutineScope A scope for performing async actions
     * @param event A scrollable event from rotary input, containing scrollable delta and timestamp
     * @param rotaryHaptics
     */
    suspend fun handleScrollEvent(
        coroutineScope: CoroutineScope,
        event: TimestampedDelta,
        rotaryHaptics: RotaryHapticFeedback,
    )
}

/**
 * An interface for scrolling behaviour
 */
public interface RotaryScrollBehavior {
    /**
     * Handles scroll event with [pixelsToScroll] and [timestamp]
     */
    suspend fun handleEvent(
        pixelsToScroll: Float,
        timestamp: Long,
    )
}

/**
 * Default implementation of [RotaryFlingBehavior]
 */
public class DefaultRotaryFlingBehavior(
    val scrollableState: ScrollableState,
    val flingBehavior: FlingBehavior,
    viewConfiguration: ViewConfiguration,
) : RotaryFlingBehavior {

    val rangeToFling = 100L
    val lastEventFlingDelay = 60L

    //  Constant which was taken from SysUi fling CL https://critique.corp.google.com/cl/462558138
    val flingScaleFactor = 0.7f

    var previousVelocity = 0f

    val rotaryVelocityTracker = RotaryVelocityTracker()

    val minFlingSpeed = viewConfiguration.scaledMinimumFlingVelocity.toFloat()
    val maxFlingSpeed = viewConfiguration.scaledMaximumFlingVelocity.toFloat()
    var latestEventTimestamp: Long = 0

    var flingVelocity: Float = 0f
    var flingTimestamp: Long = 0

    override fun startFlingTracking(timestamp: Long) {
        rotaryVelocityTracker.start(timestamp)
        latestEventTimestamp = timestamp
        previousVelocity = 0f
    }

    override fun observeEvent(timestamp: Long, delta: Float) {
        rotaryVelocityTracker.move(timestamp, delta)
        latestEventTimestamp = timestamp
    }

    override suspend fun trackFling(beforeFling: () -> Unit) {
        val currentVelocity = rotaryVelocityTracker.velocity
        debugLog { "currentVelocity: $currentVelocity" }

        if (abs(currentVelocity) >= abs(previousVelocity)) {
            flingTimestamp = latestEventTimestamp
            flingVelocity = currentVelocity * flingScaleFactor
        }
        previousVelocity = currentVelocity

        // Waiting for a fixed amount of time before checking the fling
        delay(lastEventFlingDelay)

        // For making a fling 2 criteria should be met:
        // 1) no more than
        // `rangeToFling` ms should pass between last fling detection
        // and the time of last motion event
        // 2) flingVelocity should exceed the minFlingSpeed
        debugLog {
            "Check fling:  flingVelocity: ${flingVelocity} " +
                "minFlingSpeed: $minFlingSpeed, maxFlingSpeed: $maxFlingSpeed"
        }
        if (latestEventTimestamp - flingTimestamp < rangeToFling &&
            abs(flingVelocity) > minFlingSpeed
        ) {
            //Stops scrollAnimationCoroutine because a fling will be performed
            beforeFling()
            val velocity = flingVelocity.coerceIn(-maxFlingSpeed, maxFlingSpeed)
            scrollableState.scroll(MutatePriority.UserInput) {
                with(flingBehavior) {
                    debugLog { "Flinging with velocity ${velocity}" }
                    performFling(velocity)
                }
            }
        }
    }
}

/**
 * An interface for flinging with rotary
 */
public interface RotaryFlingBehavior {

    /**
     * Observing new event within a fling tracking session with new timestamp and delta
     */
    fun observeEvent(timestamp: Long, delta: Float)

    /**
     * Performing fling if necessary and calling [beforeFling] lambda before it is triggered
     */
    suspend fun trackFling(beforeFling: () -> Unit)

    /**
     * Starts a new fling tracking session
     * with specified timestamp
     */
    fun startFlingTracking(timestamp: Long)
}

/**
 * A rotary event object which contains a [timestamp] of the rotary event and a scrolled [delta].
 */
data class TimestampedDelta(val timestamp: Long, val delta: Float)

/** Animation implementation of ScrollBehaviour.
 * This class does a smooth animation when the scroll by N pixels is done.
 * This animation works well on Rsb(high-res) and Bezel(low-res) devices.
 */
public class AnimationScrollBehavior(
    val scrollableState: ScrollableState,
) : RotaryScrollBehavior {
    var rotaryScrollDistance = 0f

    var sequentialAnimation = false
    var scrollAnimation = AnimationState(0f)
    var prevPosition = 0f
    var previousScrollEventTime = 0L

    override suspend fun handleEvent(
        pixelsToScroll: Float,
        timestamp: Long,
    ) {
        rotaryScrollDistance += pixelsToScroll
        debugLog { "Rotary scroll distance ${rotaryScrollDistance}" }

        previousScrollEventTime = timestamp
        scrollableState.scroll(MutatePriority.UserInput) {
            debugLog {
                "Rotary scroll distance ${rotaryScrollDistance}, " +
                    "ScrollAnimation value before start: ${scrollAnimation.value}"
            }

            scrollAnimation.animateTo(
                rotaryScrollDistance,
                animationSpec = spring(),
                sequentialAnimation = sequentialAnimation
            ) {
                val delta = value - prevPosition
                debugLog { "Animated by $delta, value: ${value}" }
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
@OptIn(ExperimentalComposeUiApi::class)
public fun Modifier.rotaryHandler(
    rotaryScrollHandler: RotaryScrollHandler,
    batchTimeframe: Long = 0L,
    rotaryHaptics: RotaryHapticFeedback,
): Modifier = composed {
    val channel = rememberTimestampChannel()
    val eventsFlow = remember(channel) { channel.receiveAsFlow() }

    composed {
        LaunchedEffect(eventsFlow) {
            eventsFlow
                //TODO: batching causes additional delays.
                // Do we really need to do this on this level?
                .batchRequestsWithinTimeframe(batchTimeframe)
                .collectLatest {
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
@OptIn(ExperimentalCoroutinesApi::class)
public fun Flow<TimestampedDelta>.batchRequestsWithinTimeframe(timeframe: Long): Flow<TimestampedDelta> {
    var delta = 0f
    var lastTimestamp = -timeframe
    return if (timeframe == 0L)
        this
    else
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

/**
 * A scroll handler for RSB(high-res) without snapping and with or without fling
 * A list is scrolled by the number of pixels received from the rotary device.
 *
 * This class is a little bit different from LowResScrollHandler class - it has a filtering
 * for events which are coming with wrong sign ( this happens to rsb devices,
 * especially at the end of the scroll)
 *
 * This scroll handler supports fling. It can be set with FlingBehaviour.
 */
internal class HighResRotaryScrollHandler(
    val rotaryFlingBehaviorFactory: () -> RotaryFlingBehavior?,
    val scrollBehaviourFactory: () -> RotaryScrollBehavior,
) : RotaryScrollHandler {

    // This constant is specific for high-res devices. Because that input values
    // can sometimes come with different sign, we have to filter them in this threshold
    val gestureThresholdTime = 200L
    var scrollJob: Job = CompletableDeferred<Unit>()
    var flingJob: Job = CompletableDeferred<Unit>()

    var previousScrollEventTime = 0L
    var rotaryScrollDistance = 0f

    var rotaryFlingBehavior: RotaryFlingBehavior? = rotaryFlingBehaviorFactory()
    var scrollBehaviour: RotaryScrollBehavior = scrollBehaviourFactory()

    private var prevHapticsPosition = 0f
    private var currScrollPosition = 0f
    private val hapticsThreshold: Long = 100

    override suspend fun handleScrollEvent(
        coroutineScope: CoroutineScope,
        event: TimestampedDelta,
        rotaryHaptics: RotaryHapticFeedback,
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
            scrollBehaviour.handleEvent(event.delta, event.timestamp)
        }

        flingJob = coroutineScope.async {
            rotaryFlingBehavior?.trackFling(
                beforeFling = {
                    debugLog { "Calling before fling section" }
                    scrollJob.cancel()
                    scrollBehaviour = scrollBehaviourFactory()
                })
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
        scrollBehaviour = scrollBehaviourFactory()
        rotaryFlingBehavior = rotaryFlingBehaviorFactory()
        rotaryFlingBehavior?.startFlingTracking(timestamp)
    }

    private fun handleHaptic(rotaryHaptics: RotaryHapticFeedback, scrollDelta: Float) {
        currScrollPosition += scrollDelta
        val diff = abs(currScrollPosition - prevHapticsPosition)

        if (diff >= hapticsThreshold) {
            rotaryHaptics.performHapticFeedback(RotaryHapticsType.ScrollItemFocus)
            prevHapticsPosition = currScrollPosition
        }
    }
}

/**
 * A scroll handler for Bezel(low-res) without snapping.
 * This scroll handler supports fling. It can be set with RotaryFlingBehavior.
 */
internal class LowResRotaryScrollHandler(
    val rotaryFlingBehaviorFactory: () -> RotaryFlingBehavior?,
    val scrollBehaviourFactory: () -> RotaryScrollBehavior,
) : RotaryScrollHandler {

    val gestureThresholdTime = 200L
    var previousScrollEventTime = 0L
    var rotaryScrollDistance = 0f

    var scrollJob: Job = CompletableDeferred<Unit>()

    var rotaryFlingBehavior: RotaryFlingBehavior? = rotaryFlingBehaviorFactory()
    var scrollBehaviour: RotaryScrollBehavior = scrollBehaviourFactory()

    override suspend fun handleScrollEvent(
        coroutineScope: CoroutineScope,
        event: TimestampedDelta,
        rotaryHaptics: RotaryHapticFeedback,
    ) {
        scrollJob.cancel()

        val time = event.timestamp

        if (isNewScrollEvent(time)) {
            resetTracking(time)
        } else {
            rotaryFlingBehavior?.observeEvent(event.timestamp, event.delta)
        }

        rotaryHaptics.performHapticFeedback(RotaryHapticsType.ScrollItemFocus)
        debugLog { "Rotary scroll distance: $rotaryScrollDistance" }

        previousScrollEventTime = time
        scrollJob = coroutineScope.async {
            scrollBehaviour.handleEvent(event.delta, event.timestamp)
        }

        rotaryFlingBehavior?.trackFling(
            beforeFling = {
                scrollBehaviour = scrollBehaviourFactory()
            }
        )
    }

    private fun isNewScrollEvent(timestamp: Long): Boolean {
        val timeDelta = timestamp - previousScrollEventTime
        return previousScrollEventTime == 0L || timeDelta > gestureThresholdTime
    }

    private fun resetTracking(timestamp: Long) {
        scrollBehaviour = scrollBehaviourFactory()
        debugLog { "Velocity tracker reset" }
        rotaryFlingBehavior = rotaryFlingBehaviorFactory()
        rotaryFlingBehavior?.startFlingTracking(timestamp)
    }
}

@Composable
private fun rememberTimestampChannel() =
    remember {
        Channel<TimestampedDelta>(capacity = Channel.CONFLATED)
    }
