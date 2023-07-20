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

package com.google.android.horologist.media.ui.state.mapper

import com.google.android.horologist.media.model.PlaybackState
import com.google.android.horologist.media.model.PlaybackStateEvent
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TrackPositionUiModelMapperTest {

    @Test
    fun givenMediaPosition_thenMapsCorrectly() {
        // given
        val current = 1.seconds
        val duration = 2.seconds
        val playbackStateEvent = PlaybackStateEvent(
            PlaybackState(
                playerState = PlayerState.Playing,
                isLive = false,
                currentPosition = current,
                duration = duration,
                playbackSpeed = 1f
            ),
            timestamp = 0.toDuration(DurationUnit.SECONDS),
            cause = PlaybackStateEvent.Cause.PositionDiscontinuity
        )

        // when
        val result = TrackPositionUiModelMapper.map(playbackStateEvent)

        // then
        assertThat(result).isInstanceOf(TrackPositionUiModel.Predictive::class.java)
        result as TrackPositionUiModel.Predictive
        assertThat(result.predictor.predictPercent(0)).isEqualTo(0.5f)
        assertThat(result.predictor.predictPercent(duration.inWholeMilliseconds)).isEqualTo(1f)
    }

    @Test
    fun givenMediaPositionNotPlaying_thenMapsCorrectly() {
        // given
        val current = 1.seconds
        val duration = 2.seconds
        val playbackStateEvent = PlaybackStateEvent(
            PlaybackState(
                playerState = PlayerState.Stopped,
                isLive = false,
                currentPosition = current,
                duration = duration,
                playbackSpeed = 1f
            ),
            timestamp = 0.toDuration(DurationUnit.SECONDS),
            cause = PlaybackStateEvent.Cause.PositionDiscontinuity
        )

        // when
        val result = TrackPositionUiModelMapper.map(playbackStateEvent)

        // then
        assertThat(result).isInstanceOf(TrackPositionUiModel.Actual::class.java)
        result as TrackPositionUiModel.Actual
        assertThat(result.percent).isEqualTo(0.5f)
    }

    @Test
    fun givenLoadingPlayerState_thenMapsCorrectly() {
        // given
        val current = 1.seconds
        val duration = 2.seconds
        val playbackStateEvent = PlaybackStateEvent(
            PlaybackState(
                playerState = PlayerState.Loading,
                isLive = false,
                currentPosition = current,
                duration = duration,
                playbackSpeed = 1f
            ),
            cause = PlaybackStateEvent.Cause.PositionDiscontinuity
        )

        // when
        val result = TrackPositionUiModelMapper.map(playbackStateEvent)

        // then
        assertThat(result).isInstanceOf(TrackPositionUiModel.Loading::class.java)
        result as TrackPositionUiModel.Loading
        assertThat(result.isLoading).isTrue()
    }

    @Test
    fun givenUnknownMediaPosition_thenMapsCorrectly() {
        // given
        val playbackState = PlaybackState.IDLE

        // when
        val result = TrackPositionUiModelMapper.map(PlaybackStateEvent(playbackState, PlaybackStateEvent.Cause.Initial))

        // then
        assertThat(result).isInstanceOf(TrackPositionUiModel.Hidden::class.java)
        assertThat(result.showProgress).isEqualTo(false)
    }
}
