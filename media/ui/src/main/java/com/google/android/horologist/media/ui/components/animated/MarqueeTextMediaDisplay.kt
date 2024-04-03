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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.MarqueeText
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.components.controls.MediaTitleIcon
import com.google.android.horologist.media.ui.util.isLargeScreen
import kotlin.math.roundToInt

/**
 * An animated text only display showing scrolling title and still artist in two separated rows.
 */
@ExperimentalHorologistApi
@Composable
public fun MarqueeTextMediaDisplay(
    modifier: Modifier = Modifier,
    title: String? = null,
    artist: String? = null,
    titleIcon: Paintable? = null,
    enterTransitionDelay: Int = 60,
    subtextTransitionDelay: Int = 30,
    @FloatRange(from = 0.0, to = 1.0) transitionLength: Float = 0.125f,
) {
    val isLargeScreen = LocalConfiguration.current.isLargeScreen

    fun getTransitionAnimation(delay: Int = 0): ContentTransform {
        return slideInHorizontally(animationSpec = tween(delayMillis = delay + enterTransitionDelay)) {
            (it * transitionLength).roundToInt()
        } + fadeIn(animationSpec = tween(delayMillis = delay + enterTransitionDelay)) togetherWith
            slideOutHorizontally(animationSpec = tween(delayMillis = delay)) {
                (-it * transitionLength).roundToInt()
            } + fadeOut(animationSpec = tween(delayMillis = delay))
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = title,
            transitionSpec = { getTransitionAnimation() },
            label = "AnimatedTitle",
        ) {
                currentTitle ->
            val textStyle = MaterialTheme.typography.button
            val text = buildAnnotatedString {
                if (titleIcon != null) {
                    appendInlineContent(id = "iconSlot")
                    append(" ")
                }
                append(currentTitle.orEmpty())
            }
            val inlineContent = if (titleIcon != null) {
                mapOf(
                    "iconSlot" to InlineTextContent(
                        Placeholder(textStyle.fontSize, textStyle.fontSize, PlaceholderVerticalAlign.TextCenter),
                    ) {
                        MediaTitleIcon(titleIcon)
                    },
                )
            } else {
                emptyMap()
            }
            MarqueeText(
                text = text,
                inlineContent = inlineContent,
                edgeGradientWidth = 8.dp,
                modifier = Modifier
                    // 89.76% of parent equals 4.16% of screen width applied on each side when
                    // applied on top of the 9.38% in the ConstraintLayout.
                    .fillMaxWidth(0.8976f)
                    .padding(
                        top = if (isLargeScreen) 0.dp else 2.dp,
                        bottom = if (isLargeScreen) 3.dp else 1.dp,
                    ),
                color = MaterialTheme.colors.onBackground,
                style = textStyle,
                textAlign = TextAlign.Center,
            )
        }

        AnimatedContent(
            targetState = artist,
            transitionSpec = { getTransitionAnimation(subtextTransitionDelay) },
            label = "AnimatedArtist",
        ) { currentArtist ->
            Text(
                text = currentArtist.orEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp, bottom = .6.dp),
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
