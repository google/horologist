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

import androidx.compose.ui.graphics.Color
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.base.paintable.Paintable

@ExperimentalHorologistApi
public sealed class MediaUiModel {
    public data class Ready(
        val id: String,
        val title: String,
        val subtitle: String = "",
        val artwork: Paintable? = null,
        val artworkColor: Color? = null,
        val titleIcon: Paintable? = null,
    ) : MediaUiModel()
    public object Loading : MediaUiModel()
}
