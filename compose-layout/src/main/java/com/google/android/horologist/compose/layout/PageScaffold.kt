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

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Pager Scaffold to place *inside* a single page of HorizontalPager.
 * The TimeText if set will override the AppScaffold timeText.
 *
 * @param modifier the Scaffold modifier.
 * @param timeText the page specific time text.
 * @param scrollState the ScrollableState to show in a default PositionIndicator.
 * @param positionIndicator set a non default PositionIndicator or disable with an no-op lambda.
 * @param content the content block.
 */
@Composable
fun PageScaffold(
    modifier: Modifier = Modifier,
    timeText: (@Composable () -> Unit)? = null,
    scrollState: ScrollableState? = null,
    positionIndicator: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    ScreenScaffold(modifier = modifier, timeText, scrollState, null, positionIndicator, content)
}
