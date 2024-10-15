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

package com.google.android.horologist.audio.ui.mapper

import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumeUiState

/**
 * Functions to map a [VolumeUiState] from a [VolumeState].
 */
public object VolumeUiStateMapper {
    public fun map(volumeState: VolumeState): VolumeUiState = VolumeUiState(
        current = volumeState.current,
        max = volumeState.max,
        min = volumeState.min,
    )
}
