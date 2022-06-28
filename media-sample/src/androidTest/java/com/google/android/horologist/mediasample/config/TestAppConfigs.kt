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

package com.google.android.horologist.mediasample.config

import androidx.media3.exoplayer.audio.DefaultAudioSink
import com.google.android.horologist.media3.rules.PlaybackRules
import com.google.android.horologist.mediasample.AppConfig

val OffloadDisabled = AppConfig(
    offloadEnabled = false, offloadMode = DefaultAudioSink.OFFLOAD_MODE_DISABLED
)

val OffloadNotRequired = AppConfig(
    offloadEnabled = true, offloadMode = DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_NOT_REQUIRED
)

val OffloadRequired = AppConfig(
    offloadEnabled = true, offloadMode = DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_REQUIRED
)

val SpeakerAllowed = AppConfig(
    offloadEnabled = true,
    offloadMode = DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_NOT_REQUIRED,
    playbackRules = PlaybackRules.SpeakerAllowed
)
