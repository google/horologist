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

package com.google.android.horologist.composables

import androidx.compose.ui.graphics.drawscope.Stroke
import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class SquareSegmentedProgressTest {

    @Test
    fun testSquareMeasures() {
        val measures = Measures(
            width = 10f,
            height = 10f,
            cornerRadius = 0f,
            stroke = Stroke(0f)
        )
        assertThat(measures.topRightPercent).isWithin(0.0001f).of(1f / 8)
        assertThat(measures.rightTopCornerPercent).isWithin(0.0001f).of(1f / 8)
        assertThat(measures.leftBottomCornerPercent).isWithin(0.0001f).of(5f / 8)
        assertThat(measures.topLeftPercent).isWithin(0.0001f).of(1f)
    }

    @Test
    fun testRectangleMeasures() {
        val measures = Measures(
            width = 10f,
            height = 40f,
            cornerRadius = 0f,
            stroke = Stroke(0f)
        )
        assertThat(measures.topRightPercent).isWithin(0.0001f).of(0.5f / 10f)
        assertThat(measures.rightTopCornerPercent).isWithin(0.0001f).of(0.5f / 10f)
        assertThat(measures.leftBottomCornerPercent).isWithin(0.0001f).of(5.5f / 10f)
        assertThat(measures.topLeftPercent).isWithin(0.0001f).of(1f)
    }

    @Test
    fun testCircleMeasures() {
        val measures = Measures(
            width = 1f,
            height = 1f,
            cornerRadius = 0.5f,
            stroke = Stroke(0f)
        )
        assertThat(measures.topRightPercent).isWithin(0.0001f).of(0f)
        assertThat(measures.rightTopCornerPercent).isWithin(0.0001f).of(0.25f)
        assertThat(measures.leftBottomCornerPercent).isWithin(0.0001f).of(0.75f)
        assertThat(measures.topLeftPercent).isWithin(0.0001f).of(1f)
    }
}
