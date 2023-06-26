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

package com.google.android.horologist.compose.material

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Shapes
import androidx.wear.compose.material.Typography
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test

class ChipThemeTest : ScreenshotBaseTest() {

    @Test
    fun withCustomTheme() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            MaterialTheme(
                colors = CustomColors,
                typography = CustomTypography,
                shapes = CustomShapes
            ) {
                Chip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label"
                )
            }
        }
    }

    companion object {
        private val CustomColors = Colors(
            primary = Color.Blue,
            onPrimary = Color.Magenta
        )

        private val CustomTypography = Typography(
            button = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp
            ),
            caption2 = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        )

        private val CustomShapes = Shapes(
            small = CutCornerShape(
                topStart = 10.dp,
                topEnd = 20.dp,
                bottomStart = 10.dp,
                bottomEnd = 20.dp
            )
        )
    }
}
