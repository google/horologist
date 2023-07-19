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

package com.google.android.horologist.test.toolbox.matchers

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasStateDescription

fun SemanticsNodeInteraction.assertHasStateDescription(value: String): SemanticsNodeInteraction =
    assert(hasStateDescription(value))

fun SemanticsNodeInteraction.assertHasClickLabel(expectedValue: String): SemanticsNodeInteraction =
    assert(
        SemanticsMatcher("${SemanticsActions.OnClick.name} = '$expectedValue'") {
            it.config.getOrNull(SemanticsActions.OnClick)?.label == expectedValue
        },
    )
