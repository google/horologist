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

package com.google.android.horologist.audio.ui

/*
* A UI state for volume which contains the volume ui state info including a timestamp. The
* timestamp is used for compose to know that a trigger has happened so @compose
* VolumePositionIndicator can pick up the change to make itself visible even at min or max volume.
* */
public data class VolumeUiState(
    val current: Int = 0,
    val max: Int = 1,
    val isMax: Boolean = false,
    val isMin: Boolean = false
)
