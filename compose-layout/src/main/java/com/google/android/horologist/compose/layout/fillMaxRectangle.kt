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

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt
@Stable
fun Modifier.fillMaxRectangle() = composed(
  inspectorInfo = debugInspectorInfo {
    name = "fillMaxRectangle"
  }
) {
  val isRound = LocalContext.current.resources.configuration.isScreenRound
  var inset: Dp = 0.dp
  if (isRound) {
    val screenHeightDp = LocalContext.current.resources.configuration.screenHeightDp
    val screenWidthDp = LocalContext.current.resources.configuration.smallestScreenWidthDp
    val maxSquareEdge = (sqrt(((screenHeightDp * screenWidthDp) / 2).toDouble()))
    inset = Dp(((screenHeightDp - maxSquareEdge) / 2).toFloat())
  }
  padding(all = inset)
}