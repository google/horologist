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

package com.google.android.horologist.media3.offload

import androidx.media3.exoplayer.ExoPlayer
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import com.google.android.horologist.media3.logging.ErrorReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable

@ExperimentalHorologistMedia3BackendApi
/**
 * Strategy for enabling or disabling the audio offload mode.
 */
interface AudioOffloadStrategy {
    suspend fun connect(exoPlayer: ExoPlayer, errorReporter: ErrorReporter)

    object Always : AudioOffloadStrategy {
        override suspend fun connect(exoPlayer: ExoPlayer, errorReporter: ErrorReporter) {
            exoPlayer.experimentalSetOffloadSchedulingEnabled(true)
        }
    }

    object Never : AudioOffloadStrategy {
        override suspend fun connect(exoPlayer: ExoPlayer, errorReporter: ErrorReporter) {
            exoPlayer.experimentalSetOffloadSchedulingEnabled(false)
        }
    }
}