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

package com.google.android.horologist.compose.snackbar

// From https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/src/commonMain/kotlin/androidx/compose/material/SnackbarHost.kt

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.resume

/**
 * State of the [SnackbarHost], controls the queue and the current [Snackbar] being shown inside
 * the [SnackbarHost].
 *
 * This state usually lives as a part of a [ScaffoldState] and provided to the [SnackbarHost]
 * automatically, but can be decoupled from it and live separately when desired.
 */
@ExperimentalHorologistComposeLayoutApi
@Stable
public class SnackbarHostState {
    /**
     * Only one [Snackbar] can be shown at a time.
     * Since a suspending Mutex is a fair queue, this manages our message queue
     * and we don't have to maintain one.
     */
    private val mutex = Mutex()

    /**
     * The current [SnackbarData] being shown by the [SnackbarHost], of `null` if none.
     */
    public var currentSnackbarData: SnackbarData? by mutableStateOf(null)
        private set

    /**
     * Shows or queues to be shown a [Snackbar] at the bottom of the [Scaffold] at
     * which this state is attached and suspends until snackbar is disappeared.
     *
     * [SnackbarHostState] guarantees to show at most one snackbar at a time. If this function is
     * called while another snackbar is already visible, it will be suspended until this snack
     * bar is shown and subsequently addressed. If the caller is cancelled, the snackbar will be
     * removed from display and/or the queue to be displayed.
     *
     * All of this allows for granular control over the snackbar queue from within:
     *
     * @sample androidx.compose.material.samples.ScaffoldWithCoroutinesSnackbar
     *
     * To change the Snackbar appearance, change it in 'snackbarHost' on the [Scaffold].
     *
     * @param message text to be shown in the Snackbar
     * @param actionLabel optional action label to show as button in the Snackbar
     * @param duration duration to control how long snackbar will be shown in [SnackbarHost], either
     * [SnackbarDuration.Short], [SnackbarDuration.Long] or [SnackbarDuration.Indefinite]
     *
     * @return [SnackbarResult.ActionPerformed] if option action has been clicked or
     * [SnackbarResult.Dismissed] if snackbar has been dismissed via timeout or by the user
     */
    public suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ): SnackbarResult = mutex.withLock {
        try {
            return suspendCancellableCoroutine { continuation ->
                currentSnackbarData = SnackbarDataImpl(message, actionLabel, duration, continuation)
            }
        } finally {
            currentSnackbarData = null
        }
    }

    @Stable
    private class SnackbarDataImpl(
        override val message: String,
        override val actionLabel: String?,
        override val duration: SnackbarDuration,
        private val continuation: CancellableContinuation<SnackbarResult>
    ) : SnackbarData {

        override fun performAction() {
            if (continuation.isActive) continuation.resume(SnackbarResult.ActionPerformed)
        }

        override fun dismiss() {
            if (continuation.isActive) continuation.resume(SnackbarResult.Dismissed)
        }
    }
}

/**
 * Host for [Snackbar]s to be used in [Scaffold] to properly show, hide and dismiss items based
 * on material specification and the [hostState].
 *
 * This component with default parameters comes build-in with [Scaffold], if you need to show a
 * default [Snackbar], use use [ScaffoldState.snackbarHostState] and
 * [SnackbarHostState.showSnackbar].
 *
 * @sample androidx.compose.material.samples.ScaffoldWithSimpleSnackbar
 *
 * If you want to customize appearance of the [Snackbar], you can pass your own version as a child
 * of the [SnackbarHost] to the [Scaffold]:
 *
 * @sample androidx.compose.material.samples.ScaffoldWithCustomSnackbar
 *
 * @param hostState state of this component to read and show [Snackbar]s accordingly
 * @param modifier optional modifier for this component
 * @param snackbar the instance of the [Snackbar] to be shown at the appropriate time with
 * appearance based on the [SnackbarData] provided as a param
 */
@ExperimentalHorologistComposeLayoutApi
@Composable
public fun SnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit
) {
    val currentSnackbarData = hostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current
    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            val duration = currentSnackbarData.duration.toMillis(
                currentSnackbarData.actionLabel != null,
                accessibilityManager
            )
            delay(duration)
            currentSnackbarData.dismiss()
        }
    }
    FadeInFadeOutWithScale(
        current = hostState.currentSnackbarData,
        modifier = modifier,
        content = snackbar
    )
}

/**
 * Interface to represent one particular [Snackbar] as a piece of the [SnackbarHostState]
 *
 * @property message text to be shown in the [Snackbar]
 * @property actionLabel optional action label to show as button in the Snackbar
 * @property duration duration of the snackbar
 */
@ExperimentalHorologistComposeLayoutApi
public interface SnackbarData {
    public val message: String
    public val actionLabel: String?
    public val duration: SnackbarDuration

    /**
     * Function to be called when Snackbar action has been performed to notify the listeners
     */
    public fun performAction()

    /**
     * Function to be called when Snackbar is dismissed either by timeout or by the user
     */
    public fun dismiss()
}

/**
 * Possible results of the [SnackbarHostState.showSnackbar] call
 */
@ExperimentalHorologistComposeLayoutApi
public enum class SnackbarResult {
    /**
     * [Snackbar] that is shown has been dismissed either by timeout of by user
     */
    Dismissed,

    /**
     * Action on the [Snackbar] has been clicked before the time out passed
     */
    ActionPerformed,
}

/**
 * Possible durations of the [Snackbar] in [SnackbarHost]
 */
@ExperimentalHorologistComposeLayoutApi
public enum class SnackbarDuration {
    /**
     * Show the Snackbar for a short period of time
     */
    Short,

    /**
     * Show the Snackbar for a long period of time
     */
    Long,

    /**
     * Show the Snackbar indefinitely until explicitly dismissed or action is clicked
     */
    Indefinite
}

// TODO: magic numbers adjustment
@ExperimentalHorologistComposeLayoutApi
internal fun SnackbarDuration.toMillis(
    hasAction: Boolean,
    accessibilityManager: AccessibilityManager?
): Long {
    val original = when (this) {
        SnackbarDuration.Indefinite -> Long.MAX_VALUE
        SnackbarDuration.Long -> 10000L
        SnackbarDuration.Short -> 4000L
    }
    if (accessibilityManager == null) {
        return original
    }
    return accessibilityManager.calculateRecommendedTimeoutMillis(
        original,
        containsIcons = true,
        containsText = true,
        containsControls = hasAction
    )
}

// TODO: to be replaced with the public customizable implementation
// it's basically tweaked nullable version of Crossfade
@Composable
@ExperimentalHorologistComposeLayoutApi
private fun FadeInFadeOutWithScale(
    current: SnackbarData?,
    modifier: Modifier = Modifier,
    content: @Composable (SnackbarData) -> Unit
) {
    val state = remember { FadeInFadeOutState<SnackbarData?>() }
    if (current != state.current) {
        state.current = current
        val keys = state.items.map { it.key }.toMutableList()
        if (!keys.contains(current)) {
            keys.add(current)
        }
        state.items.clear()
        keys.filterNotNull().mapTo(state.items) { key ->
            FadeInFadeOutAnimationItem(key) { children ->
                val isVisible = key == current
                val duration = if (isVisible) SnackbarFadeInMillis else SnackbarFadeOutMillis
                val delay = SnackbarFadeOutMillis + SnackbarInBetweenDelayMillis
                val animationDelay = if (isVisible && keys.filterNotNull().size != 1) delay else 0
                val opacity = animatedOpacity(
                    animation = tween(
                        easing = LinearEasing,
                        delayMillis = animationDelay,
                        durationMillis = duration
                    ),
                    visible = isVisible,
                    onAnimationFinish = {
                        if (key != state.current) {
                            // leave only the current in the list
                            state.items.removeAll { it.key == key }
                            state.scope?.invalidate()
                        }
                    }
                )
                val scale = animatedScale(
                    animation = tween(
                        easing = FastOutSlowInEasing,
                        delayMillis = animationDelay,
                        durationMillis = duration
                    ),
                    visible = isVisible
                )
                Box(
                    Modifier
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            alpha = opacity.value
                        )
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                            dismiss { key.dismiss(); true }
                        }
                ) {
                    children()
                }
            }
        }
    }
    Box(modifier) {
        state.scope = currentRecomposeScope
        state.items.fastForEach { (item, opacity) ->
            key(item) {
                opacity {
                    content(item!!)
                }
            }
        }
    }
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
    contract { callsInPlace(action) }
    for (index in indices) {
        val item = get(index)
        action(item)
    }
}

private class FadeInFadeOutState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: Any? = Any()
    var items = mutableListOf<FadeInFadeOutAnimationItem<T>>()
    var scope: RecomposeScope? = null
}

private data class FadeInFadeOutAnimationItem<T>(
    val key: T,
    val transition: FadeInFadeOutTransition
)

private typealias FadeInFadeOutTransition = @Composable (content: @Composable () -> Unit) -> Unit

@Composable
private fun animatedOpacity(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    onAnimationFinish: () -> Unit = {}
): State<Float> {
    val alpha = remember { Animatable(if (!visible) 1f else 0f) }
    LaunchedEffect(visible) {
        alpha.animateTo(
            if (visible) 1f else 0f,
            animationSpec = animation
        )
        onAnimationFinish()
    }
    return alpha.asState()
}

@Composable
private fun animatedScale(animation: AnimationSpec<Float>, visible: Boolean): State<Float> {
    val scale = remember { Animatable(if (!visible) 1f else 0.8f) }
    LaunchedEffect(visible) {
        scale.animateTo(
            if (visible) 1f else 0.8f,
            animationSpec = animation
        )
    }
    return scale.asState()
}

private const val SnackbarFadeInMillis = 150
private const val SnackbarFadeOutMillis = 75
private const val SnackbarInBetweenDelayMillis = 0
