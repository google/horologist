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
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyNotDefined
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHasClickAction
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test

class ButtonA11yTest : WearLegacyA11yTest() {

    @Test
    fun default() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                onLongClick = {},
            )
        }

        composeRule.onNode(keyIsDefined(SemanticsProperties.Role))
            .assertHasClickAction()
            .assert(keyIsDefined(SemanticsActions.OnLongClick))
            .assertContentDescriptionEquals("contentDescription")
    }

    @Test
    fun material() {
        runComponentTest {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
            )
        }

        composeRule.onNode(keyIsDefined(SemanticsProperties.Role))
            .assertHasClickAction()
            .assert(keyNotDefined(SemanticsActions.OnLongClick))
            .assertContentDescriptionEquals("contentDescription")
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

        composeRule.onNode(keyIsDefined(SemanticsProperties.Role))
            .assertHasClickAction()
            .assertContentDescriptionEquals("contentDescription")
    }
}
