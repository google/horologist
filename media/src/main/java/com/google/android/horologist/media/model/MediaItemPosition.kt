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

package com.google.android.horologist.media.model

/**
 * Represents the current [media item][MediaItem] position, duration and percent progress.
 * Current position and duration are measured in milliseconds.
 */
public data class MediaItemPosition(
    val current: Long,
    val duration: Long,
) {
    val percent: Float = current.toFloat() / duration.toFloat()
}
