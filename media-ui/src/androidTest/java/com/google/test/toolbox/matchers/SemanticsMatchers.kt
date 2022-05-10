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

package com.google.test.toolbox.matchers

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.semantics.CustomSemanticsProperties.IconImageVectorKey

@OptIn(ExperimentalHorologistMediaUiApi::class)
fun hasIconImageVector(imageVector: ImageVector): SemanticsMatcher =
    SemanticsMatcher.expectValue(IconImageVectorKey, imageVector)

fun hasProgressBar(): SemanticsMatcher =
    SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
