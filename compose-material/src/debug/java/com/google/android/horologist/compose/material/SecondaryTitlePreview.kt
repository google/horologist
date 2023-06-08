/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.compose.material

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.compose.tools.WearPreview

@WearPreview
@Composable
fun SecondaryTitlePreview() {
    SecondaryTitle(
        text = "Title"
    )
}

@WearPreview
@Composable
fun SecondaryTitlePreviewWithLongText() {
    SecondaryTitle(
        text = "Title with a very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long text"
    )
}

@WearPreview
@Composable
fun SecondaryTitlePreviewWithIcon() {
    SecondaryTitle(
        text = "Title",
        icon = Icons.Filled.Add,
        iconTint = Color(0xFF946EB1)
    )
}

@WearPreview
@Composable
fun SecondaryTitlePreviewWithIconAndLongText() {
    SecondaryTitle(
        text = "Title with a very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long text",
        icon = Icons.Filled.Add
    )
}
