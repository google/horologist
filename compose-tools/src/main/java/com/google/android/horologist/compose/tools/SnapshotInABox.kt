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

package com.google.android.horologist.compose.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.Paparazzi

/**
 * Paparazzi extension function to default wrap our snapshots in a box
 * with black background.
 *
 * If you need to change up how the box is rendered with the composable, a modifier for the
 * box is exposed.
 *
 * @param boxModifier Modifier for the box that the snapshot is wrapped in.
 * @param content Composable content that we want to snapshot.
 */
public fun Paparazzi.snapshotInABox(
    boxModifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    this.snapshot {
        Box(
            modifier = boxModifier.background(Color.Black),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
