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

@file:OptIn(ExperimentalHorologistComposablesApi::class)

package com.google.android.horologist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.ExperimentalHorologistComposablesApi
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DatePickerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDate() {
        var date by mutableStateOf<LocalDate?>(null)
        composeTestRule.setContent {
            if (date == null) {
                DatePicker(
                    onDateConfirm = {
                        date = it
                    },
                    date = LocalDate.of(2022, 4, 25)
                )
            } else {
                Text(modifier = Modifier.testTag("date"), text = "$date")
            }
        }

        composeTestRule.onNodeWithContentDescription("check").performClick()

        composeTestRule.onNodeWithTag("date").assertTextEquals("2022-04-25")
    }
}
