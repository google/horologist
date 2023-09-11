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

package com.google.android.horologist.compose.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.wear.compose.material.Typography

@Composable
public fun Typography.copy(fn: TextStyle.() -> TextStyle): Typography = remember(this) {
    this.copy(
        display1 = fn(this.display1),
        display2 = fn(this.display2),
        display3 = fn(this.display3),
        title1 = fn(this.title1),
        title2 = fn(this.title2),
        title3 = fn(this.title3),
        body1 = fn(this.body1),
        body2 = fn(this.body2),
        button = fn(this.button),
        caption1 = fn(this.caption1),
        caption2 = fn(this.caption2),
        caption3 = fn(this.caption3),
    )
}
