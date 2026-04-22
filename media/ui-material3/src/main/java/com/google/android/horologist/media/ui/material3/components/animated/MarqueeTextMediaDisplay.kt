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

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.media.ui.material3.components.controls.MediaTitleIcon
import com.google.android.horologist.media.ui.material3.composables.MarqueeText
import com.google.android.horologist.media.ui.material3.util.MEDIA_TITLE_EDGE_GRADIENT_WIDTH
import com.google.android.horologist.media.ui.material3.util.MEDIA_TITLE_ICON_SIZE
import com.google.android.horologist.media.ui.material3.util.TRACK_SUBTITLE_HEIGHT
import com.google.android.horologist.media.ui.material3.util.TRACK_TITLE_HEIGHT
import com.google.android.horologist.media.ui.material3.util.isLargeScreen
import kotlin.math.roundToInt

/**
 * An animated text only display showing scrolling title and still artist in two separated rows.
 *
 * @param modifier Modifier for the component.
 * @param title The title to display.
 * @param artist The artist to display.
 * @param titleIcon The icon to display next to the title.
 * @param enterTransitionDelay The delay before the title starts to animate.
 * @param subtextTransitionDelay The delay before the artist starts to animate.
 * @param transitionLength The fraction of the screen width to slide in and out.
 * @param colorScheme The color scheme to use.
 */
@Composable
public fun MarqueeTextMediaDisplay(
    modifier: Modifier = Modifier,
    title: String? = null,
    artist: String? = null,
    titleIcon: Paintable? = null,
    enterTransitionDelay: Int = 60,
    subtextTransitionDelay: Int = 30,
    @FloatRange(from = 0.0, to = 1.0) transitionLength: Float = 0.125f,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    val isLargeScreen = LocalConfiguration.current.isLargeScreen
    val density = LocalDensity.current
    val columnHeight = TRACK_TITLE_HEIGHT + TRACK_SUBTITLE_HEIGHT

    fun getTransitionAnimation(delay: Int = 0): ContentTransform {
        return slideInHorizontally(animationSpec = tween(delayMillis = delay + enterTransitionDelay)) {
            (it * transitionLength).roundToInt()
        } + fadeIn(animationSpec = tween(delayMillis = delay + enterTransitionDelay)) togetherWith
            slideOutHorizontally(animationSpec = tween(delayMillis = delay)) {
                (-it * transitionLength).roundToInt()
            } + fadeOut(animationSpec = tween(delayMillis = delay))
    }

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = density.density,
            fontScale = density.fontScale.coerceAtMost(1f),
        ),
    ) {
        Column(
            modifier = modifier.height(columnHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AnimatedContent(
                targetState = title,
                transitionSpec = { getTransitionAnimation() },
                label = "AnimatedTitle",
            ) { currentTitle ->
                Row(
                    modifier = Modifier.fillMaxWidth(titleIcon?.let { 0.648f } ?: 0.6672f)
                        .height(TRACK_TITLE_HEIGHT),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    titleIcon?. let {
                        Box(modifier = Modifier.size(MEDIA_TITLE_ICON_SIZE)) {
                            MediaTitleIcon(
                                paintableRes = it,
                                tint = colorScheme.primary,
                            )
                        }
                    }
                    MarqueeText(
                        text = currentTitle.orEmpty(),
                        edgeGradientWidth = MEDIA_TITLE_EDGE_GRADIENT_WIDTH,
                        startGap = if (titleIcon != null) MEDIA_TITLE_EDGE_GRADIENT_WIDTH else 0.dp,
                        color = colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            AnimatedContent(
                targetState = artist,
                transitionSpec = { getTransitionAnimation(subtextTransitionDelay) },
                label = "AnimatedArtist",
            ) { currentArtist ->
                currentArtist?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(
                            if (isLargeScreen) 0.71f else 0.75f,
                        ).height(TRACK_SUBTITLE_HEIGHT),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = currentArtist,
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}
