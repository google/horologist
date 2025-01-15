/*
 * Copyright 2024 The Android Open Source Project
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
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.images.base.paintable.Paintable

@ExperimentalHorologistApi
/** A sealed class representing the UI state of a media item. */
public sealed class MediaUiModel {

    /**
     * Represents the UI state when media data is available.
     *
     * @param id The unique identifier of the media item.
     * @param title The title of the media item.
     * @param subtitle The subtitle of the media item (optional).
     * @param artwork The artwork to display for the media item (optional).
     * @param artworkColor The primary color to use for the artwork background (optional).
     * @param artworkColorSeed The seed color to use for generating the artwork color pallet
     *   (optional).
     * @param titleIcon An icon to display next to the title (optional).
     * @param selectedAudioOutput The audio output on which the media is currently playing (optional).
     */
    public data class Ready(
        val id: String,
        val title: String,
        val subtitle: String = "",
        val clientPackageName: String? = null,
        val artwork: Paintable? = null,
        val artworkColor: Color? = null,
        val artworkColorSeed: Color? = null,
        val titleIcon: Paintable? = null,
        val selectedAudioOutput: AudioOutput? = null,
    ) : MediaUiModel()
    public object Loading : MediaUiModel()
}
