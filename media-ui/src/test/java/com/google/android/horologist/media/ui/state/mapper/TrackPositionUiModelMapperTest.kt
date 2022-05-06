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

@file:OptIn(ExperimentalMediaUiApi::class)

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.data.model.TrackPosition
import com.google.android.horologist.media.ui.ExperimentalMediaUiApi
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TrackPositionUiModelMapperTest {

    @Test
    fun givenTrackPosition_thenMapsCorrectly() {
        // given
        val current = 1L
        val duration = 2L
        val trackPosition = TrackPosition(current, duration)

        // when
        val result = TrackPositionUiModelMapper.map(trackPosition)

        // then
        assertThat(result.current).isEqualTo(current)
        assertThat(result.duration).isEqualTo(duration)
        assertThat(result.percent).isEqualTo(0.5f)
    }
}
