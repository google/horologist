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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class TitleTest : WearLegacyComponentTest() {

    @Test
    fun defaultPrimary() {
        runComponentTest {
            Title(
                text = "Title",
            )
        }
    }

    @Test
    fun primaryWithVeryLongText() {
        runComponentTest {
            Title(
                text = "Title with a very very very very very very very very very very very very " +
                    "very very very very very very very very very very very very very very very " +
                    "very very very very very very long text",
            )
        }
    }

    @Test
    fun defaultSecondary() {
        runComponentTest {
            SecondaryTitle(
                text = "Title",
            )
        }
    }

    @Test
    fun secondaryWithVeryLongText() {
        runComponentTest {
            SecondaryTitle(
                text = "Title with a very very very very very very very very very very very very " +
                    "very very very very very very very very very very very very very very very " +
                    "very very very very very very long text",
            )
        }
    }

    @Test
    fun defaultSecondaryWithIcon() {
        runComponentTest {
            SecondaryTitle(
                text = "Title",
                icon = Icons.Outlined.MusicNote,
                iconTint = Color(0xFF946EB1),
            )
        }
    }

    @Test
    fun secondaryWithIconAndVeryLongText() {
        runComponentTest {
            SecondaryTitle(
                text = "Title with a very very very very very very very very very very very very " +
                    "very very very very very very very very very very very very very very very " +
                    "very very very very very very long text",
                icon = Icons.Outlined.MusicNote,
                iconTint = Color(0xFF946EB1),
            )
        }
    }

    @Test
    fun defaultPrimaryRtl() {
        runComponentTest {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Title(
                    text = "Title",
                )
            }
        }
    }

    @Test
    fun defaultSecondaryRtl() {
        runComponentTest {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                SecondaryTitle(
                    text = "Title",
                )
            }
        }
    }

    @Test
    fun mirroredSecondary() {
        runComponentTest {
            SecondaryTitle(
                text = "Title",
                icon = Icons.Outlined.MusicNote,
                iconTint = Color(0xFF946EB1),
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }

    @Test
    fun mirroredRtlSecondary() {
        runComponentTest {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                SecondaryTitle(
                    text = "Title",
                    icon = Icons.Outlined.MusicNote,
                    iconTint = Color(0xFF946EB1),
                    iconRtlMode = IconRtlMode.Mirrored,
                )
            }
        }
    }
}
