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

package com.google.android.horologist.base.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.android.horologist.annotations.ExperimentalHorologistApi

@Suppress("DEPRECATION")
@Deprecated(
    "Replaced by Icon in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "Icon",
        "com.google.android.horologist.compose.material.Icon"
    )
)
@ExperimentalHorologistApi
@Composable
public fun StandardIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    rtlMode: IconRtlMode = IconRtlMode.Default
) {
    com.google.android.horologist.compose.material.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        rtlMode = when (rtlMode) {
            IconRtlMode.Default -> com.google.android.horologist.compose.material.IconRtlMode.Default
            IconRtlMode.Mirrored -> com.google.android.horologist.compose.material.IconRtlMode.Mirrored
        }
    )
}

@Deprecated(
    "Replaced by IconRtlMode in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "IconRtlMode",
        "com.google.android.horologist.compose.material.IconRtlMode"
    )
)
@ExperimentalHorologistApi
public enum class IconRtlMode {
    Default,
    Mirrored
}
