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

package com.google.android.horologist.compose.ambient

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.ZonedDateTime

/**
 * An example of using [AmbientAware]: Provides the time, at the specified update frequency, whilst
 * in interactive mode, or when ambient-generated updates occur (typically every 1 min).
 *
 * Example usage:
 *
 *  AmbientAware { stateUpdate ->
 *     Box(
 *         contentAlignment = Alignment.Center,
 *         modifier = Modifier.fillMaxSize()
 *     ) {
 *          AmbientAwareTime(stateUpdate) { dateTime, isAmbient ->
 *              // Basic example of AmbientAwareTime usage
 *              val ambientFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
 *              val interactiveFmt =
 *                  remember { DateTimeFormatter.ofPattern("HH:mm:ss") }
 *              val dateTimeStr = if (isAmbient) {
 *                  ambientFmt.format(ZonedDateTime.now())
 *              } else {
 *                  interactiveFmt.format(ZonedDateTime.now())
 *              }
 *              Text(dateTimeStr)
 *          }
 *      }
 *  }
 *
 * @param stateUpdate The state update from [AmbientAware]
 * @param updatePeriodMillis The update period, whilst in interactive mode
 * @param block The developer-supplied composable for rendering the date and time.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AmbientAwareTime(
    stateUpdate: AmbientStateUpdate,
    updatePeriodMillis: Long = 1000,
    block: @Composable (dateTime: ZonedDateTime, isAmbient: Boolean) -> Unit,
) {
    check(updatePeriodMillis >= 1000)

    var isAmbient by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf<ZonedDateTime?>(null) }

    currentTime?.let {
        block(it, isAmbient)
    }

    LaunchedEffect(stateUpdate) {
        if (stateUpdate.ambientState == AmbientState.Interactive) {
            while (isActive) {
                isAmbient = false
                currentTime = ZonedDateTime.now()
                delay(updatePeriodMillis - System.currentTimeMillis() % updatePeriodMillis)
            }
        } else {
            isAmbient = true
            currentTime = ZonedDateTime.now()
        }
    }
}
