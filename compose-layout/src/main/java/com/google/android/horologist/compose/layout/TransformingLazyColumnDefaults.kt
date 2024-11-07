/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType

/**
 * Calculates and remembers padding values for a Wear column based on screen size and item types.
 *
 * This function is designed to provide responsive padding for columns, specifically in Wear OS,
 * taking into account the screen shape (round or rectangular) and the types of items
 * at the beginning and end of the column.
 *
 * @param first The type of the first item in the column. Used to determine top padding.
 *              Defaults to a safe value of [ItemType.Unspecified].
 * @param last The type of the last item in the column. Used to determine bottom padding.
 *             Defaults to a safe value of [ItemType.Unspecified].
 * @param horizontalPercent The percentage of the screen width to use for horizontal padding.
 *                          Defaults to 5.2%.
 *
 * @return A [PaddingValues] object containing the calculated padding values.
 */
@Composable
public fun rememberResponsiveColumnPadding(
    first: ColumnItemType = ItemType.Unspecified,
    last: ColumnItemType = ItemType.Unspecified,
    horizontalPercent: Float = 0.052f,
): PaddingValues {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    val horizontalPadding = screenWidthDp * horizontalPercent

    return PaddingValues(
        top = first.topPadding(horizontalPercent),
        bottom = last.bottomPadding(horizontalPercent),
        start = horizontalPadding,
        end = horizontalPadding,
    )
}

/**
 * Represents the types of items that can be placed in a Wear column and how to calculate an
 * optimal or safe padding.
 */
public interface ColumnItemType {
    /**
     * Calculates the padding for the top of the Column based on the provided horizontal padding.
     */
    @Composable
    public fun topPadding(horizontalPercent: Float): Dp

    /**
     * Calculates the padding for the bottom of the Column based on the provided horizontal padding.
     */
    @Composable
    public fun bottomPadding(horizontalPercent: Float): Dp

    companion object {
        val Button: ColumnItemType
            get() = ItemType.Chip

        val Title: ColumnItemType
            get() = ItemType.Text

        val BodyText: ColumnItemType
            get() = ItemType.BodyText

        val Card: ColumnItemType
            get() = ItemType.Card

        val IconButton: ColumnItemType
            get() = ItemType.SingleButton

        val ButtonRow: ColumnItemType
            get() = ItemType.MultiButton
    }
}
