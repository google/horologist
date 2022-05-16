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

import org.junit.Assert.assertThrows
import org.junit.Test

class MediaItemPositionTest {

    @Test
    fun givenCurrentPositionIsNegative_whenCreateKnownPosition_thenExceptionIsThrown() {
        // given
        val current = -1L

        // when
        val whenBlock = { MediaItemPosition.create(current = current, duration = 10L) }

        // then
        assertThrows(IllegalStateException::class.java) { whenBlock() }
    }

    @Test
    fun givenDurationIsZero_whenCreateKnownPosition_thenExceptionIsThrown() {
        // given
        val duration = 0L

        // when
        val whenBlock = { MediaItemPosition.create(current = 0L, duration = duration) }

        // then
        assertThrows(IllegalStateException::class.java) { whenBlock() }
    }

    @Test
    fun givenCurrentPositionIsGreaterThanDuration_whenCreateKnownPosition_thenExceptionIsThrown() {
        // given
        val current = 2L
        val duration = 1L

        // when
        val whenBlock = { MediaItemPosition.create(current = current, duration = duration) }

        // then
        assertThrows(IllegalStateException::class.java) { whenBlock() }
    }
}
