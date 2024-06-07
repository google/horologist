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
import androidx.compose.ui.res.stringResource
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

class TitleTest : WearLegacyComponentTest() {

    @Test
    fun defaultPrimary() {
        runComponentTest {
            Title(
                text = stringResource(id = R.string.title),
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
                text = stringResource(id = R.string.title),
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
                text = stringResource(id = R.string.title),
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
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun defaultPrimaryRtl() {
        runComponentTest {
            Title(
                text = stringResource(id = R.string.title),
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun defaultSecondaryRtl() {
        runComponentTest {
            SecondaryTitle(
                text = stringResource(id = R.string.title),
            )
        }
    }

    @Test
    fun mirroredSecondary() {
        runComponentTest {
            SecondaryTitle(
                text = stringResource(id = R.string.title),
                icon = Icons.Outlined.MusicNote,
                iconTint = Color(0xFF946EB1),
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun mirroredRtlSecondary() {
        runComponentTest {
            SecondaryTitle(
                text = stringResource(id = R.string.title),
                icon = Icons.Outlined.MusicNote,
                iconTint = Color(0xFF946EB1),
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }
}
