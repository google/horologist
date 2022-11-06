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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Test

internal class SquareSegmentedProgressTest {

    @Test
    fun `SquareSegmentedProgress SHOULD return correct offset WHEN linetype is End`() {
        val size = Size(100f, 100f)
        val expected = Offset(100f, 13.5f) to Offset(100f, 86.5f)

        val actual = SquareSegmentedProgress().calculateLineProgress(
            progress = 0.35f,
            lineType = LineType.End,
            size = size,
            cornerRadius = 13.5f,
            strokeEndDelta = 0f,
            sectionEndProgress = 0.125f,
            startOffset = 0f
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `SquareSegmentedProgress SHOULD return correct offset WHEN linetype is End under progress`() {
        val size = Size(100f, 100f)
        val expected = Offset(100f, 45f) to Offset(100f, 35f)

        val actual = SquareSegmentedProgress().calculateLineProgress(
            progress = 0.20f,
            lineType = LineType.End,
            size = size,
            cornerRadius = 45f,
            strokeEndDelta = 0f,
            sectionEndProgress = 0.25f,
            startOffset = 0f
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `SquareSegmentedProgress SHOULD return correct offset WHEN linetype is Bottom`() {
        val size = Size(100f, 100f)
        val expected = Offset(86.5f, 100f) to Offset(13.5f, 100f)

        val actual = SquareSegmentedProgress().calculateLineProgress(
            progress = 0.60f,
            lineType = LineType.Bottom,
            size = size,
            cornerRadius = 13.5f,
            strokeEndDelta = 0f,
            sectionEndProgress = 0.375f,
            startOffset = 0f
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `SquareSegmentedProgress SHOULD return correct offset WHEN linetype is Bottom under progress`() {
        val size = Size(100f, 100f)
        val expected = Offset(86.5f, 100f) to Offset(13.5f, 100f)

        val actual = SquareSegmentedProgress().calculateLineProgress(
            progress = 0.9f,
            lineType = LineType.Bottom,
            size = size,
            cornerRadius = 13.5f,
            strokeEndDelta = 0f,
            sectionEndProgress = 0.5f,
            startOffset = 0f
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `SquareSegmentedProgress SHOULD return correct offset WHEN linetype is Start`() {
        val size = Size(100f, 100f)
        val expected = Offset(0.0f, 86.5f) to Offset(0.0f, 16.5f)

        val actual = SquareSegmentedProgress().calculateLineProgress(
            progress = 0.8f,
            lineType = LineType.Start,
            size = size,
            cornerRadius = 13.5f,
            strokeEndDelta = 0f,
            sectionEndProgress = 0.625f,
            startOffset = 0f
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `SquareSegmentedProgress SHOULD return correct offset WHEN linetype is Start under progress`() {
        val size = Size(100f, 100f)
        val expected = Offset(0f, 55f) to Offset(0f, 45f)

        val actual = SquareSegmentedProgress().calculateLineProgress(
            progress = 0.6875f,
            lineType = LineType.Start,
            size = size,
            cornerRadius = 45f,
            strokeEndDelta = 0f,
            sectionEndProgress = 0.625f,
            startOffset = 0f
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `SquareSegmentedProgress SHOULD return correct offset WHEN linetype is Top`() {
        val size = Size(100f, 100f)
        val expected = Offset(13.5f, 0f) to Offset(86.5f, 0f)

        val actual = SquareSegmentedProgress().calculateLineProgress(
            progress = 1.2f,
            lineType = LineType.Top,
            size = size,
            cornerRadius = 13.5f,
            strokeEndDelta = 0f,
            sectionEndProgress = 0.7f,
            startOffset = 0f
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `SquareSegmentedProgress SHOULD return correct offset WHEN linetype is Top under progress`() {
        val size = Size(100f, 100f)
        val expected = Offset(45f, 0f) to Offset(55f, 0f)

        val actual = SquareSegmentedProgress().calculateLineProgress(
            progress = 0.9375f,
            lineType = LineType.Top,
            size = size,
            cornerRadius = 45f,
            strokeEndDelta = 0f,
            sectionEndProgress = 0.7f,
            startOffset = 0f
        )

        assertEquals(expected, actual)
    }
}
