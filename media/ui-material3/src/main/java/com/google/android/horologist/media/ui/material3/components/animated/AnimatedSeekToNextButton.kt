/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components.animated

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.IconButtonColors
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.horologist.media.ui.material3.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.model.R

@Composable
public fun AnimatedSeekToNextButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onRepeatableClick: (() -> Unit)? = null,
  onRepeatableClickEnd: (() -> Unit)? = null,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  buttonPadding: PaddingValues = PaddingValues(0.dp),
  colors: IconButtonColors = MediaButtonDefaults.mediaButtonDefaultColors(),
  lottieAnimatable: LottieAnimatable = rememberLottieAnimatable(),
  content: @Composable (BoxScope.() -> Unit)? = null,
) {
  val compositionResult =
    rememberLottieComposition(spec = LottieCompositionSpec.Asset("lottie/M3Next.json"))
  val contentDescription =
    stringResource(id = R.string.horologist_seek_to_next_button_content_description)
  AnimatedMediaButton(
    modifier = modifier,
    onClick = onClick,
    contentDescription = contentDescription,
    enabled = enabled,
    colors = colors,
    buttonPadding = buttonPadding,
    compositionResult = compositionResult,
    onRepeatableClick = onRepeatableClick,
    onRepeatableClickEnd = onRepeatableClickEnd,
    interactionSource = interactionSource,
    lottieAnimatable = lottieAnimatable,
  ) {
    content?.invoke(this)
      ?: AnimatedSeekToNextButtonContent(
        compositionResult = compositionResult,
        contentDescription = contentDescription,
        lottieAnimatable = lottieAnimatable,
      )
  }
}

@Composable
public fun BoxScope.AnimatedSeekToNextButtonContent(
  modifier: Modifier = Modifier,
  contentDescription: String =
    stringResource(id = R.string.horologist_seek_to_next_button_content_description),
  compositionResult: LottieCompositionResult =
    rememberLottieComposition(spec = LottieCompositionSpec.Asset("lottie/M3Next.json")),
  lottieAnimatable: LottieAnimatable = rememberLottieAnimatable(),
) {
  MediaButtonContent(
    modifier = modifier,
    compositionResult = compositionResult,
    contentDescription = contentDescription,
    lottieAnimatable = lottieAnimatable,
  )
}
