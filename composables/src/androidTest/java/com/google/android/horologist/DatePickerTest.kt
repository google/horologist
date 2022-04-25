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

@file:OptIn(ExperimentalComposablesApi::class)

package com.google.android.horologist

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.ExperimentalComposablesApi
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DatePickerTest {
    @get:Rule
    val composeTestRule = createComposeRule().apply {
        mainClock.autoAdvance = false
    }

    @Test
    fun testDate() {
        var date: LocalDate? = null
        composeTestRule.setContent {
            DatePicker(
                buttonIcon = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "check",
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(align = Alignment.Center),
                    )
                },
                onClick = {
                    date = it
                },
                initial = LocalDate.of(2022, 4, 25)
            )
        }

        composeTestRule.onNodeWithContentDescription("check").performClick()

        assertThat(date).isEqualTo(LocalDate.of(2022, 4, 25))
    }
}
