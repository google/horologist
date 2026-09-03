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

package com.google.android.horologist.media.ui.components.animated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.model.LottieCompositionCache

internal class ReadyLottieCompositionResult(override val value: LottieComposition) :
  LottieCompositionResult {
  override val error: Throwable? = null
  override val isLoading: Boolean = false
  override val isComplete: Boolean = true
  override val isFailure: Boolean = false
  override val isSuccess: Boolean = true

  override suspend fun await(): LottieComposition = value
}

@Composable
internal fun rememberPrecachedLottieComposition(
  spec: LottieCompositionSpec
): LottieCompositionResult {
  val context = LocalContext.current
  val isInspection = LocalInspectionMode.current

  val precached =
    remember(spec) {
      when (spec) {
        is LottieCompositionSpec.Asset -> {
          val cacheKey = "asset_${spec.assetName}"
          LottieCompositionCache.getInstance().get(cacheKey)
            ?: if (isInspection) {
              LottieCompositionFactory.fromAssetSync(context, spec.assetName).value
            } else {
              null
            }
        }
        is LottieCompositionSpec.RawRes -> {
          if (isInspection) {
            LottieCompositionFactory.fromRawResSync(context, spec.resId).value
          } else {
            null
          }
        }
        else -> null
      }
    }

  return if (precached != null) {
    remember(precached) { ReadyLottieCompositionResult(precached) }
  } else {
    rememberLottieComposition(spec)
  }
}
