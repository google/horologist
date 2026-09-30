/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.lottie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.horologist.lottie.composables.LottieDemoModePlayer
import com.google.android.horologist.lottie.composables.LottieDetailPlayer
import com.google.android.horologist.lottie.composables.LottieGalleryList

@Composable
fun LottieScreen(modifier: Modifier = Modifier) {
  var viewMode by remember { mutableStateOf<LottieViewMode>(LottieViewMode.Gallery) }

  when (val currentMode = viewMode) {
    is LottieViewMode.Gallery -> {
      LottieGalleryList(
        catalog = LottieDemoCatalog,
        onSelect = { index -> viewMode = LottieViewMode.Detail(index) },
        onStartDemo = { viewMode = LottieViewMode.Demo(0) },
        modifier = modifier,
      )
    }
    is LottieViewMode.Detail -> {
      val index = currentMode.index.coerceIn(0, LottieDemoCatalog.lastIndex)
      LottieDetailPlayer(
        item = LottieDemoCatalog[index],
        currentIndex = index,
        totalCount = LottieDemoCatalog.size,
        onPrevious = {
          viewMode =
            LottieViewMode.Detail((index - 1 + LottieDemoCatalog.size) % LottieDemoCatalog.size)
        },
        onNext = { viewMode = LottieViewMode.Detail((index + 1) % LottieDemoCatalog.size) },
        onClose = { viewMode = LottieViewMode.Gallery },
        modifier = modifier,
      )
    }
    is LottieViewMode.Demo -> {
      LottieDemoModePlayer(
        catalog = LottieDemoCatalog,
        initialIndex = currentMode.index,
        onClose = { viewMode = LottieViewMode.Gallery },
        modifier = modifier,
      )
    }
  }
}
