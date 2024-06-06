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
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Check
import androidx.wear.compose.material.ButtonDefaults
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test
import org.robolectric.annotation.Config

internal class ButtonTest : WearLegacyComponentTest() {

    @Test
    fun default() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
            )
        }
    }

    @Test
    fun disabled() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                enabled = false,
            )
        }
    }

    @Test
    fun large() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                buttonSize = ButtonSize.Large,
            )
        }
    }

    @Test
    fun small() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                buttonSize = ButtonSize.Small,
            )
        }
    }

    @Test
    fun customSize() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                buttonSize = ButtonSize.Custom(
                    customIconSize = ButtonDefaults.SmallIconSize,
                    customTapTargetSize = ButtonDefaults.LargeButtonSize,
                ),
            )
        }
    }

    @Test
    fun withSecondaryButtonColors() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                colors = ButtonDefaults.secondaryButtonColors(),
            )
        }
    }

    @Test
    fun withIconButtonColors() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                colors = ButtonDefaults.iconButtonColors(),
            )
        }
    }

    @Test
    fun usingDrawableResAsIcon() {
        runComponentTest {
            Button(
                id = android.R.drawable.ic_media_play,
                contentDescription = "contentDescription",
                onClick = { },
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun usingDrawableResAsIconRtl() {
        runComponentTest {
            Button(
                id = android.R.drawable.ic_media_play,
                contentDescription = "contentDescription",
                onClick = { },
            )
        }
    }

    @Test
    fun usingDrawableResAsIconMirrored() {
        runComponentTest {
            Button(
                id = android.R.drawable.ic_media_play,
                contentDescription = "contentDescription",
                onClick = { },
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun usingDrawableResAsIconMirroredRtl() {
        runComponentTest {
            Button(
                id = android.R.drawable.ic_media_play,
                contentDescription = "contentDescription",
                onClick = { },
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun defaultRtl() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
            )
        }
    }

    @Test
    fun mirrored() {
        runComponentTest {
            Button(
                imageVector = Icons.AutoMirrored.Default.DirectionsBike,
                contentDescription = "contentDescription",
                onClick = { },
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun mirroredRtl() {
        runComponentTest {
            Button(
                imageVector = Icons.AutoMirrored.Default.DirectionsBike,
                contentDescription = "contentDescription",
                onClick = { },
                iconRtlMode = IconRtlMode.Mirrored,
            )
        }
    }
}
