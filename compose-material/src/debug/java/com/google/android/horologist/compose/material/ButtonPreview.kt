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
import androidx.wear.compose.material.ButtonDefaults
import com.google.android.horologist.compose.tools.WearPreview

@WearPreview
@Composable
fun ButtonPreview() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { }
    )
}

@WearPreview
@Composable
fun ButtonPreviewLarge() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        buttonSize = ButtonSize.Large
    )
}

@WearPreview
@Composable
fun ButtonPreviewSmall() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        buttonSize = ButtonSize.Small
    )
}

@WearPreview
@Composable
fun ButtonPreviewExtraSmall() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        buttonSize = ButtonSize.ExtraSmall
    )
}

@WearPreview
@Composable
fun ButtonPreviewDisabled() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        enabled = false
    )
}

@WearPreview
@Composable
fun ButtonPreviewWithSecondaryButtonColors() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        colors = ButtonDefaults.secondaryButtonColors()
    )
}

@WearPreview
@Composable
fun ButtonPreviewWithIconButtonColors() {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = "contentDescription",
        onClick = { },
        colors = ButtonDefaults.iconButtonColors()
    )
}
