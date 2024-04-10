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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyNotDefined
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextEquals
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test

class ChipA11yTest : WearLegacyA11yTest() {

    @Test
    fun withSecondaryLabelAndIcon() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                onLongClick = {},
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
            )
        }

        composeRule.onNode(keyIsDefined(SemanticsProperties.Role))
            .assertHasClickAction()
            .assert(keyIsDefined(SemanticsActions.OnLongClick))
            .assertTextEquals("Primary label, Secondary label")
    }

    @Test
    fun withSecondaryLabelAndIconMaterial() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
            )
        }

        composeRule.onNode(keyIsDefined(SemanticsProperties.Role))
            .assertHasClickAction()
            .assert(keyNotDefined(SemanticsActions.OnLongClick))
    }

    @Test
    fun disabled() {
        runComponentTest {
            Chip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image.asPaintable(),
                enabled = false,
            )
        }
    }
}
