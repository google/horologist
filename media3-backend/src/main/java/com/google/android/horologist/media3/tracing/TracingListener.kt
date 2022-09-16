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

package com.google.android.horologist.media3.tracing

import androidx.media3.common.Player
import androidx.tracing.Trace

public class TracingListener : Player.Listener {
    private var isPlaying: Boolean = false
    private var isLoading: Boolean = false

    override fun onIsPlayingChanged(newIsPlaying: Boolean) {
        if (newIsPlaying != isPlaying) {
            if (newIsPlaying) {
                Trace.beginAsyncSection("Player.isPlaying", 0)
            } else {
                Trace.endAsyncSection("Player.isPlaying", 0)
            }
        }

        this.isPlaying = newIsPlaying
    }

    override fun onIsLoadingChanged(newIsLoading: Boolean) {
        if (newIsLoading != isLoading) {
            if (newIsLoading) {
                Trace.beginAsyncSection("Player.isLoading", 0)
            } else {
                Trace.endAsyncSection("Player.isLoading", 0)
            }
        }

        this.isLoading = newIsLoading
    }
}
