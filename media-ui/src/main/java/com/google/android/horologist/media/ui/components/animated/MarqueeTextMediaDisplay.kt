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

package com.google.android.horologist.media.ui.components.animated

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.MarqueeText

/**
 * An animated text only display showing scrolling title and still artist in two separated rows.
 */
@ExperimentalHorologistApi
@Composable
public fun MarqueeTextMediaDisplay(
  modifier: Modifier = Modifier,
  title: String? = null,
  artist: String? = null,
  enterTransitionDelay: Int = 60,
  subtextTransitionDelay: Int = 30,
  @FloatRange(from = 0.0, to = 1.0) transitionLength: Float = 0.125f,
) {
  fun getTransitionAnimation(delay: Int = 0): ContentTransform {
    return slideInHorizontally(animationSpec = tween(delayMillis = delay + enterTransitionDelay)) {
      Math.round(it * transitionLength).toInt()
    } + fadeIn(animationSpec = tween(delayMillis = delay + enterTransitionDelay)) with
      slideOutHorizontally(animationSpec = tween(delayMillis = delay)) {
        Math.round(-it * transitionLength).toInt()
      } + fadeOut(animationSpec = tween(delayMillis = delay))
  }

  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    AnimatedContent(targetState = title, transitionSpec = { getTransitionAnimation() }) {
      currentTitle ->
      MarqueeText(
        text = currentTitle.orEmpty(),
        modifier = Modifier.fillMaxWidth(0.7f),
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.button,
        textAlign = TextAlign.Center
      )
    }

    AnimatedContent(
      targetState = artist,
      transitionSpec = { getTransitionAnimation(subtextTransitionDelay) }
    ) { currentArtist ->
      Text(
        text = currentArtist.orEmpty(),
        modifier = Modifier.fillMaxWidth(0.8f),
        color = MaterialTheme.colors.onBackground,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = MaterialTheme.typography.body2
      )
    }
  }
}

