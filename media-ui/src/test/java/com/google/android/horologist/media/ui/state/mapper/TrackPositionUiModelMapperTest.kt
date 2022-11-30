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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.model.MediaPosition
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class TrackPositionUiModelMapperTest {

    @Test
    fun givenMediaPosition_thenMapsCorrectly() {
        // given
        val current = 1.seconds
        val duration = 2.seconds
        val mediaPosition = MediaPosition.create(current, duration)

        // when
        val result = TrackPositionUiModelMapper.map(mediaPosition)

        // then
        assertThat(result.current).isEqualTo(current.inWholeMilliseconds)
        assertThat(result.duration).isEqualTo(duration.inWholeMilliseconds)
        assertThat(result.percent).isEqualTo(0.5f)
        assertThat(result.showProgress).isEqualTo(true)
    }

    @Test
    fun givenUnknownMediaPosition_thenMapsCorrectly() {
        // given
        val mediaPosition = MediaPosition.Unknown

        // when
        val result = TrackPositionUiModelMapper.map(mediaPosition)

        // then
        assertThat(result.current).isEqualTo(0L)
        assertThat(result.duration).isEqualTo(0L)
        assertThat(result.percent).isEqualTo(0F)
        assertThat(result.showProgress).isEqualTo(false)
    }
}
