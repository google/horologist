/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ButtonGroup
import androidx.wear.compose.material3.ButtonGroupScope
import com.google.android.horologist.media.ui.material3.util.BUTTON_GROUP_ITEMS_COUNT
import com.google.android.horologist.media.ui.material3.util.BUTTON_GROUP_LARGE_DEVICE_SIDE_BUTTONS_SPACING_PERCENTAGE
import com.google.android.horologist.media.ui.material3.util.BUTTON_GROUP_LARGE_DEVICE_SIDE_BUTTONS_VERTICAL_MARGIN_PERCENTAGE
import com.google.android.horologist.media.ui.material3.util.BUTTON_GROUP_MIDDLE_SECTION_HORIZONTAL_MARGIN_PERCENTAGE
import com.google.android.horologist.media.ui.material3.util.BUTTON_GROUP_SMALL_DEVICE_SIDE_BUTTONS_SPACING_PERCENTAGE
import com.google.android.horologist.media.ui.material3.util.BUTTON_GROUP_SMALL_DEVICE_SIDE_BUTTONS_VERTICAL_MARGIN_PERCENTAGE
import com.google.android.horologist.media.ui.material3.util.LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
import com.google.android.horologist.media.ui.material3.util.SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
import com.google.android.horologist.media.ui.material3.util.getScreenSizeInDpFromPercentage
import com.google.android.horologist.media.ui.material3.util.isLargeScreen

/**
 * A base [ButtonGroup] for media control middle section.
 *
 * @param leftButton a composable function for the left button.
 * @param middleButton a composable function for the middle button.
 * @param rightButton a composable function for the right button.
 * @param modifier a [Modifier] for the [ButtonGroup].
 * @param interactionSources an array of [MutableInteractionSource] for the buttons.
 */
@Composable
public fun ButtonGroupLayout(
    leftButton: @Composable ButtonGroupScope.(MutableInteractionSource) -> Unit,
    middleButton: @Composable ButtonGroupScope.(MutableInteractionSource) -> Unit,
    rightButton: @Composable ButtonGroupScope.(MutableInteractionSource) -> Unit,
    modifier: Modifier = Modifier,
    interactionSources: Array<MutableInteractionSource> = remember {
        Array(BUTTON_GROUP_ITEMS_COUNT) { MutableInteractionSource() }
    },
) {
    val middleSectionHeight = ButtonGroupLayoutDefaults.middleButtonSize

    ButtonGroup(
        modifier = modifier.fillMaxWidth().height(middleSectionHeight),
        spacing = 0.dp,
        contentPadding = PaddingValues(0.dp),
        expansionWidth = ButtonGroupLayoutDefaults.ExpansionWidth,
    ) {
        leftButton(interactionSources[0])
        middleButton(interactionSources[1])
        rightButton(interactionSources[2])
    }
}

public object ButtonGroupLayoutDefaults {
    /** How much buttons grow (and neighbors shrink) when pressed. */
    internal val ExpansionWidth: Dp = 12.dp

    /** The size of the middle button as per size of the device. */
    public val middleButtonSize: Dp
        @Composable
        get() {
            val configuration = LocalConfiguration.current
            return remember(configuration) {
                if (configuration.isLargeScreen) {
                    LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
                } else {
                    SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
                }
            }
        }

    /** The padding of the side buttons as per size of the device. */
    @Composable
    public fun getSideButtonsPadding(isLeftButton: Boolean): PaddingValues {
        val configuration = LocalConfiguration.current
        val isLargeScreen = configuration.isLargeScreen
        val buttonGroupSpacing =
            remember(configuration) {
                if (configuration.isLargeScreen) {
                    BUTTON_GROUP_LARGE_DEVICE_SIDE_BUTTONS_SPACING_PERCENTAGE
                } else {
                    BUTTON_GROUP_SMALL_DEVICE_SIDE_BUTTONS_SPACING_PERCENTAGE
                }
            }
        return PaddingValues.Absolute(
            left =
                if (isLeftButton) {
                    configuration.getScreenSizeInDpFromPercentage(
                        BUTTON_GROUP_MIDDLE_SECTION_HORIZONTAL_MARGIN_PERCENTAGE,
                    )
                } else {
                    configuration.getScreenSizeInDpFromPercentage(buttonGroupSpacing)
                },
            right =
                if (!isLeftButton) {
                    configuration.getScreenSizeInDpFromPercentage(
                        BUTTON_GROUP_MIDDLE_SECTION_HORIZONTAL_MARGIN_PERCENTAGE,
                    )
                } else {
                    configuration.getScreenSizeInDpFromPercentage(buttonGroupSpacing)
                },
            top =
                configuration.getScreenSizeInDpFromPercentage(
                    if (isLargeScreen) {
                        BUTTON_GROUP_LARGE_DEVICE_SIDE_BUTTONS_VERTICAL_MARGIN_PERCENTAGE
                    } else {
                        BUTTON_GROUP_SMALL_DEVICE_SIDE_BUTTONS_VERTICAL_MARGIN_PERCENTAGE
                    },
                ),
            bottom =
                configuration.getScreenSizeInDpFromPercentage(
                    if (isLargeScreen) {
                        BUTTON_GROUP_LARGE_DEVICE_SIDE_BUTTONS_VERTICAL_MARGIN_PERCENTAGE
                    } else {
                        BUTTON_GROUP_SMALL_DEVICE_SIDE_BUTTONS_VERTICAL_MARGIN_PERCENTAGE
                    },
                ),
        )
    }
}
