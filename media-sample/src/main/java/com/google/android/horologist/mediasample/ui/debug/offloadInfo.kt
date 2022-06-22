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

package com.google.android.horologist.mediasample.ui.debug

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.Format
import androidx.wear.compose.foundation.CurvedScope
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedComposable
import androidx.wear.compose.material.curvedText

private val Format?.display: String
    get() = if (this == null) {
        "none"
    } else {
        this.sampleMimeType + "\n" + sampleRate
    }

fun CurvedScope.offloadInfo(
    offloadState: OffloadState,
    style: CurvedTextStyle,
    showFormat: Boolean = false
) {
    curvedText(
        text = "${offloadState.times.percent} Off",
        color = Color.Gray,
        style = style
    )
    if (showFormat) {
        curvedComposable {
            Spacer(modifier = Modifier.size(8.dp))
        }
        curvedText(text = offloadState.format.display, color = Color.Gray, style = style)
    }
}
