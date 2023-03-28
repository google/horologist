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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.MarqueeText
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi

/**
 * An animated text only display showing scrolling title and still artist in two separated rows.
 */

@Composable
public fun MarqueeTextMediaDisplay(
    modifier: Modifier = Modifier,
    title: String? = null,
    artist: String? = null
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = title,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() with
                slideOutHorizontally { -it } + fadeOut()
            }
        ) {
            currentTitle ->
            MarqueeText(
                text = currentTitle.orEmpty(),
                modifier = Modifier.fillMaxWidth(0.7f),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.button,
                textAlign = TextAlign.Center
            )
        }
<<<<<<< HEAD
        Text(
            text = artist.orEmpty(),
            modifier = Modifier.fillMaxWidth(0.8f),
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.body2
        )
=======
>>>>>>> bc556b24 (Added animation for artist and song name while clicking seek next/previous button)
    }

    AnimatedContent(
            targetState = artist,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() with
                slideOutHorizontally { -it } + fadeOut()
            }
        ) {
            currentArtist ->
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

