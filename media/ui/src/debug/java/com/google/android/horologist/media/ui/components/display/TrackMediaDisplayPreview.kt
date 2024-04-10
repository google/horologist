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

package com.google.android.horologist.media.ui.components.display

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.compose.tools.WearPreview
import com.google.android.horologist.media.ui.state.model.MediaUiModel

@WearPreview
@Composable
fun TrackMediaDisplayPreview() {
    TrackMediaDisplay(
        media = MediaUiModel.Ready(
            id = "id",
            title = "Song title",
            subtitle = "Artist name",
        ),
    )
}

@Preview(
    "With long text",
    backgroundColor = 0xff000000,
    showBackground = true,
)
@Composable
fun TrackMediaDisplayPreviewLongText() {
    TrackMediaDisplay(
        media = MediaUiModel.Ready(
            id = "id",
            title = "I Predict That You Look Good In A Riot",
            subtitle = "Arctic Monkeys feat Kaiser Chiefs",
        ),
    )
}
