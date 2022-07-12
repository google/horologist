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

package com.google.android.horologist.media.data

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import com.google.android.horologist.media.model.MediaItemPosition
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class MediaItemPositionMapperTest {
    val fakeStatePlayer = FakeStatePlayer()

    @Test
    fun `check position calculations null`() {
        val position = MediaItemPositionMapper.map(null)
        assertThat(position).isNull()
    }

    @Test
    fun `check position calculations unknown duration`() {
        val position = mediaItemPosition(10L, C.TIME_UNSET) as MediaItemPosition.UnknownDuration
        assertThat(position.current).isEqualTo(10.milliseconds)
    }

    @Test
    fun `check position calculations past end`() {
        val position = mediaItemPosition(100L, 99L) as MediaItemPosition.KnownDuration
        assertThat(position.current).isEqualTo(100.milliseconds)
        assertThat(position.duration).isEqualTo(100.milliseconds)
    }

    @Test
    fun `check position calculations during`() {
        val position = mediaItemPosition(100L, 1000L) as MediaItemPosition.KnownDuration
        assertThat(position.current).isEqualTo(100.milliseconds)
        assertThat(position.duration).isEqualTo(1000.milliseconds)
    }

    private fun mediaItemPosition(
        currentPosition: Long,
        duration: Long,
        currentMediaItem: MediaItem? = null
    ): MediaItemPosition? {
        fakeStatePlayer.overridePosition(
            currentPosition = currentPosition,
            duration = duration,
            currentMediaItem = currentMediaItem
        )
        return MediaItemPositionMapper.map(fakeStatePlayer)
    }
}
