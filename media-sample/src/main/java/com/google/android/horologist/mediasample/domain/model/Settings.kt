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

package com.google.android.horologist.mediasample.domain.model

import com.google.android.horologist.media3.offload.AudioOffloadStrategy
import com.google.android.horologist.media3.offload.BackgroundAudioOffloadStrategy

data class Settings(
    val showTimeTextInfo: Boolean = false,
    val podcastControls: Boolean = false,
    val loadItemsAtStartup: Boolean = true,
    val animated: Boolean = true,
    val debugOffload: Boolean = false,
    val offloadMode: OffloadMode = OffloadMode.Background,
) {
    enum class OffloadMode(val strategy: AudioOffloadStrategy) {
        Always(AudioOffloadStrategy.Always),
        Never(AudioOffloadStrategy.Never),
        Background(BackgroundAudioOffloadStrategy)
    }
}
