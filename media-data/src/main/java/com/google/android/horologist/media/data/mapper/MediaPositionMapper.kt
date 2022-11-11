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

package com.google.android.horologist.media.data.mapper

import androidx.media3.common.C
import androidx.media3.common.Player
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.model.MediaPosition
import kotlin.time.Duration.Companion.milliseconds

/**
 * Maps a [Media3 player][Player] position into a [MediaPosition].
 */
@ExperimentalHorologistMediaDataApi
public object MediaPositionMapper {
    public fun map(player: Player?): MediaPosition? {
        return if (player == null || player.currentMediaItem == null) {
            null
        } else if (player.duration == C.TIME_UNSET || player.duration <= 0L) {
            MediaPosition.UnknownDuration(player.currentPosition.milliseconds)
        } else {
            MediaPosition.create(
                current = player.currentPosition.milliseconds,
                // Ensure progress is max 100%, even given faulty media metadata
                duration = (player.duration.coerceAtLeast(player.currentPosition)).milliseconds
            )
        }
    }
}
