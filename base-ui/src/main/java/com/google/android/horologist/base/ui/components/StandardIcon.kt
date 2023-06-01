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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.wear.compose.material.Icon
import com.google.android.horologist.annotations.ExperimentalHorologistApi

@ExperimentalHorologistApi
@Composable
public fun StandardIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    rtlMode: IconRtlMode = IconRtlMode.Default
) {
    val shouldMirror =
        rtlMode == IconRtlMode.Mirrored && LocalLayoutDirection.current == LayoutDirection.Rtl
    Icon(
        modifier = modifier
            .scale(
                scaleX = if (shouldMirror) -1f else 1f,
                scaleY = 1f
            ),
        imageVector = imageVector,
        contentDescription = contentDescription
    )
}

@ExperimentalHorologistApi
public enum class IconRtlMode {
    Default,
    Mirrored
}
