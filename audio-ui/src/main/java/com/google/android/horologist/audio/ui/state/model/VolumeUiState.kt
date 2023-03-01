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

package com.google.android.horologist.audio.ui.state.model

/**
 * Data class holding the current state of the volume system.
 */
public data class VolumeUiState(
    public val current: Float?,
    public val isMuted: Boolean = false,
    public val adjustable: Boolean = false
) {
    public val isMax: Boolean
        get() = current == 1f

    public val isMin: Boolean
        get() = current == 0f
}
