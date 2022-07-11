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
import androidx.media3.common.Player
import androidx.media3.test.utils.StubPlayer
import com.google.android.horologist.media.data.PlayerRepositoryImpl.Companion.readPosition
import com.google.android.horologist.media.model.MediaItemPosition
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.milliseconds

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PlayerRepositoryStateTest {
    var _currentPosition: Long = 0L
    var _duration: Long = C.TIME_UNSET

    val player = object : StubPlayer() {
        override fun getCurrentPosition(): Long {
            return _currentPosition
        }

        override fun getDuration(): Long {
            return _duration
        }
    }

    @Test
    fun testPositionNotPlayer() {
        val position = (null as Player?).readPosition()

        assertThat(position).isNull()
    }

    @Test
    fun testPositionDurationUnset() {
        _currentPosition = 10L

        val position = player.readPosition() as MediaItemPosition.UnknownDuration

        assertThat(position.current).isEqualTo(10.milliseconds)
    }

    @Test
    fun testPositionBeyondEnd() {
        _currentPosition = 100L
        _duration = 99L

        val position = player.readPosition() as MediaItemPosition.KnownDuration

        assertThat(position.current).isEqualTo(100.milliseconds)
        assertThat(position.duration).isEqualTo(100.milliseconds)
    }

    @Test
    fun testPositionNormal() {
        _currentPosition = 100L
        _duration = 1000L

        val position = player.readPosition() as MediaItemPosition.KnownDuration

        assertThat(position.current).isEqualTo(100.milliseconds)
        assertThat(position.duration).isEqualTo(1000.milliseconds)
    }
}
