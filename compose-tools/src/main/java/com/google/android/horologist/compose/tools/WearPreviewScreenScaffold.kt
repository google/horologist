/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.compose.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText

public object PinnedTimeSource : TimeSource {
  override val currentTime: String
    @Composable get() = "10:10"
}

public class FixedTimeSource(private val time: String = "10:10") : TimeSource {
  override val currentTime: String
    @Composable get() = time
}

@Composable
public fun WearPreviewScreenScaffold(
  modifier: Modifier = Modifier,
  scrollState: ScalingLazyListState? = null,
  timeText: @Composable () -> Unit = { TimeText(timeSource = PinnedTimeSource) },
  positionIndicator: @Composable () -> Unit = {
    scrollState?.let { PositionIndicator(scalingLazyListState = it) }
  },
  content: @Composable () -> Unit,
) {
  MaterialTheme {
    Scaffold(
      modifier = modifier.fillMaxSize().background(Color.Black),
      timeText = timeText,
      positionIndicator = positionIndicator,
    ) {
      content()
    }
  }
}
