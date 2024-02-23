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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText

/**
 *  An app scaffold, to be used to wrap a [SwipeDismissableNavHost].
 * The [TimeText] will be shown here, but can be customised in either [ScreenScaffold] or
 * [PagerScaffold].
 *
 * Without this, the vanilla [Scaffold] is likely placed on each individual screen and [TimeText]
 * moves with the screen, or shown twice when swiping to dimiss.
 *
 * @param modifier the Scaffold modifier.
 * @param timeText the app default time text, defaults to TimeText().
 * @param content the content block.
 */
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    timeText: @Composable () -> Unit = { ResponsiveTimeText() },
    content: @Composable BoxScope.() -> Unit,
) {
    val scaffoldState = LocalScaffoldState.current.apply {
        appTimeText.value = timeText
    }

    Scaffold(
        modifier = modifier,
        timeText = scaffoldState.timeText,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

internal val LocalScaffoldState = compositionLocalOf { ScaffoldState() }
