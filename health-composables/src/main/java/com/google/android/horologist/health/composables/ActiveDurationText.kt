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

package com.google.android.horologist.health.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay

/**
 * Composable to make it easier to create a chronometer from Health Services exercise data.
 *
 * [ActiveDurationText] provides a text-based chronometer, which can be styled using the same means
 * as a [Text] component, and which should only recompose once-per second as the chronometer ticks.
 *
 * Things to note that [ActiveDurationText] aims to workaround:
 *
 * 1.  Health Services does not guarantee [ExerciseUpdate] messages every second, so these must not
 *     be used to drive a ticker.
 * 2.  The ActiveDuration delivered in an [ExerciseUpdate] will not fall on a second-boundary, so
 *     the delivery time of these messages cannot be used to determine when to update any content
 *     on the screen through a ticker.
 *
 * @param checkpoint The checkpoint from the most recent state change. This is supplied in the
 *     [ExerciseUpdate] messages from [ExerciseClient].
 * @param state The current state of the exercise. This is supplied in the [ExerciseUpdate] messages
 *     from [ExerciseClient].
 * @param modifier Compose modifier.
 * @param formatTemplate A string representing the format for the active duration. A collection of
 *     useful defaults are available in [ActiveDurationDefaults], but a custom value can be
 *     specified to handle specific scenarios or locales. See [ActiveDurationDefaults] for examples.
 * @param color The text color.
 * @param textAlign Horizontal text alignment.
 * @param style The text styling.
 * @param lifecycleOwner lifecycle owner.
 */

@ExperimentalHorologistHealthComposablesApi
@Composable
public fun ActiveDurationText(
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint,
    state: ExerciseState,
    modifier: Modifier = Modifier,
    formatTemplate: String = ActiveDurationDefaults.HH_MM_SS,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    style: TextStyle = LocalTextStyle.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    var lifecycleEnabled by remember { mutableStateOf(true) }
    var activeSeconds by remember {
        mutableStateOf(
            calculateDurationSeconds(
                checkpoint,
                state
            )
        )
    }
    val formattedActiveDuration by remember {
        derivedStateOf {
            val hours = activeSeconds / 3600
            val minutes = (activeSeconds % 3600) / 60
            val seconds = activeSeconds % 60
            formatTemplate.format(hours, minutes, seconds)
        }
    }

    // When the component is not visible, then the ticker should stop, and only resume when the UI
    // component becomes visible again.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                lifecycleEnabled = true
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                lifecycleEnabled = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    /**
     * The starting and stopping of the ticker depends on two things: (1) Whether or not the
     * exercise is in the ACTIVE state, and (2) whether the component is visible. The app should
     * handle any ambient behaviour separately.
     */
    LaunchedEffect(state, lifecycleEnabled) {
        if (state == ExerciseState.ACTIVE && lifecycleEnabled) {
            val absoluteOffset = absoluteTimeOffsetMillis(checkpoint)
            while (true) {
                val now = System.currentTimeMillis()
                // Delay until the next active duration second boundary
                val delayInterval = nextTimeForOffset(now, absoluteOffset) - now
                // Delay should delay for _at least_ the interval specified.
                delay(delayInterval)
                activeSeconds = calculateDurationSeconds(checkpoint, state)
            }
        }
    }

    Text(
        modifier = modifier,
        text = formattedActiveDuration,
        color = color,
        textAlign = textAlign,
        style = style
    )
}

/**
 * Returns the offset within physical time, in milliseconds (so 0-999) where the ActiveDuration
 * second boundary falls.
 *
 * For example, if WHS reports at 10:23:15.2 that the active duration is 11.4 seconds, then the
 * active duration second boundary is 800ms (e.g. The active duration will advance a second at
 * 10:23:15.8, 10:23:16.8, 10:23:17.8, ...
 *
 * This is useful because the component should recalculate the active duration each time that
 * boundary is passed. (This assumes the state is active).
 *
 * @param checkpoint An active duration checkpoint.
 * @return The offset, in milliseconds.
 */
private fun absoluteTimeOffsetMillis(checkpoint: ExerciseUpdate.ActiveDurationCheckpoint) =
    (checkpoint.time.toEpochMilli() - checkpoint.activeDuration.toMillis()) % 1000

/**
 * Calculates the next physical time when the active duration second boundary will be passed, given
 * a point in time.
 *
 * For example, if the active duration offset is 800ms, as per the example above, then the following
 * should be returned:
 *
 * 10:23:15.2 -> returns 10:23:15.8
 * 10:23:15.9 -> returns 10:23:16.8
 *
 * @param timeMillis The physical time after which to find the next active duration second boundary.
 * @param offsetMillis The offset in physical time, between 0-999 where the active duration second
 *     ticks over.
 * @return The next physical time where the active duration boundary is crossed.
 */
private fun nextTimeForOffset(timeMillis: Long, offsetMillis: Long) =
    ((timeMillis - offsetMillis) / 1000) * 1000 + 1000 + offsetMillis

/**
 * Calculates the active duration, taking into account any elapsed time since the
 * [ExerciseUpdate.ActiveDurationCheckpoint] was delivered from Health Services.
 *
 * @param checkpoint The checkpoint from the most recent state change.
 * @param state The current state of the exercise.
 * @return The current active duration, in seconds since epoch.
 */
private fun calculateDurationSeconds(
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint,
    state: ExerciseState
): Long {
    val delta = if (state == ExerciseState.ACTIVE) {
        System.currentTimeMillis() - checkpoint.time.toEpochMilli()
    } else {
        0L
    }
    return checkpoint.activeDuration.plusMillis(delta).seconds
}
