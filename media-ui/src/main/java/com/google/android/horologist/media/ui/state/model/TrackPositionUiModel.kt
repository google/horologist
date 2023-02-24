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

package com.google.android.horologist.media.ui.state.model

import com.google.android.horologist.media.model.PositionPredictor
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import kotlin.time.Duration

@ExperimentalHorologistMediaUiApi
public sealed class TrackPositionUiModel {
    public abstract val showProgress: Boolean
    public abstract val shouldAnimate: Boolean
    public abstract val isLoading: Boolean

    public data class Predictive(
        public val predictor: PositionPredictor,
        public override val shouldAnimate: Boolean = false,
        public override val isLoading: Boolean = false
    ) : TrackPositionUiModel() {
        override val showProgress: Boolean get() = true
    }

    public data class Actual(
        public val percent: Float,
        public val duration: Duration,
        public val position: Duration,
        public override val shouldAnimate: Boolean = false,
        public override val isLoading: Boolean = false
    ) : TrackPositionUiModel() {
        override val showProgress: Boolean get() = true

        public companion object {
            public val ZERO: Actual = Actual(0f, Duration.ZERO, Duration.ZERO)
        }
    }

    public object Loading : TrackPositionUiModel() {
        override val showProgress: Boolean
            get() = true
        override val shouldAnimate: Boolean
            get() = true
        override val isLoading: Boolean
            get() = true
    }

    public object Hidden : TrackPositionUiModel() {
        override val showProgress: Boolean get() = false
        override val shouldAnimate: Boolean get() = false
        override val isLoading: Boolean get() = false
    }
}
