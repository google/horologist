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
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import com.google.android.horologist.compose.tools.WearPreview

@WearPreview
@Composable
fun ButtonSecondaryPreview() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        buttonType = ButtonType.Secondary
    )
}

@WearPreview
@Composable
fun ButtonSecondaryPreviewLarge() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        buttonType = ButtonType.Secondary,
        buttonSize = ButtonSize.Large
    )
}

@WearPreview
@Composable
fun ButtonSecondaryPreviewSmall() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        buttonType = ButtonType.Secondary,
        buttonSize = ButtonSize.Small
    )
}

@WearPreview
@Composable
fun ButtonSecondaryPreviewExtraSmall() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        buttonType = ButtonType.Secondary,
        buttonSize = ButtonSize.ExtraSmall
    )
}

@WearPreview
@Composable
fun ButtonSecondaryPreviewDisabled() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        buttonType = ButtonType.Secondary,
        enabled = false
    )
}
