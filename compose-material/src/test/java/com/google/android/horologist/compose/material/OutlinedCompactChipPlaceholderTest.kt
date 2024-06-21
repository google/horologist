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

package com.google.android.horologist.compose.material

import com.google.android.horologist.compose.material.BasePlaceholderTest.Item.Companion.Full
import com.google.android.horologist.compose.material.BasePlaceholderTest.Item.Companion.LabelOnly
import com.google.android.horologist.images.base.paintable.Conversions.orPlaceholder
import org.junit.Test

class OutlinedCompactChipPlaceholderTest : BasePlaceholderTest() {

    @Test
    fun full() {
        runPlaceholderTest(item = Full) { item, placeholderState ->
            OutlinedCompactChip(
                label = item?.label.orEmpty(),
                icon = item?.icon.orPlaceholder(),
                onClick = {},
                enabled = item != null,
                placeholderState = placeholderState
            )
        }
    }

    @Test
    fun labelOnly() {
        runPlaceholderTest(item = LabelOnly) { item, placeholderState ->
            OutlinedCompactChip(
                label = item?.label.orEmpty(),
                onClick = {},
                enabled = item != null,
                placeholderState = placeholderState
            )
        }
    }
}
