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

package com.google.android.horologist.media.ui.material3.screens.entity

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A screen that displays a media collection and allow actions to be taken on it.
 */
@ExperimentalHorologistApi
@Composable
public fun EntityScreen(
    headerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    buttonsContent: (@Composable () -> Unit)? = null,
    content: (ScalingLazyListScope.() -> Unit)? = null,
) {
    val scrollState = rememberScalingLazyListState()

    ScreenScaffold(scrollState = scrollState) {
        ScalingLazyColumn(
            state = scrollState,
            modifier = modifier,
        ) {
            item {
                headerContent()
            }

            buttonsContent?.let {
                item {
                    buttonsContent()
                }
            }

            content?.let {
                content()
            }
        }
    }
}

/**
 * A screen that displays a [Media] collection and allow actions to be taken on it.
 */
@ExperimentalHorologistApi
@Composable
public fun <Media> EntityScreen(
    headerContent: @Composable () -> Unit,
    mediaList: List<Media>,
    mediaContent: @Composable (media: Media) -> Unit,
    modifier: Modifier = Modifier,
    buttonsContent: (@Composable () -> Unit)? = null,
) {
    EntityScreen(
        headerContent = headerContent,
        modifier = modifier,
        buttonsContent = buttonsContent,
        content = {
            items(count = mediaList.size) { index ->
                mediaContent(mediaList[index])
            }
        },
    )
}

/**
 * A screen that displays a [Media] collection and allow actions to be taken on it.
 * The content displayed is based on the screen's [state][EntityScreenState].
 */
@ExperimentalHorologistApi
@Composable
public fun <Media> EntityScreen(
    entityScreenState: EntityScreenState<Media>,
    headerContent: @Composable () -> Unit,
    loadingContent: ScalingLazyListScope.() -> Unit,
    mediaContent: @Composable (media: Media) -> Unit,
    modifier: Modifier = Modifier,
    buttonsContent: (@Composable () -> Unit)? = null,
    failedContent: (@Composable () -> Unit)? = null,
) {
    when (entityScreenState) {
        EntityScreenState.Loading -> {
            EntityScreen(
                headerContent = headerContent,
                modifier = modifier,
                buttonsContent = buttonsContent,
                content = loadingContent,
            )
        }

        is EntityScreenState.Loaded -> {
            EntityScreen(
                headerContent = headerContent,
                mediaList = entityScreenState.mediaList,
                mediaContent = mediaContent,
                modifier = modifier,
                buttonsContent = buttonsContent,
            )
        }

        EntityScreenState.Failed -> {
            EntityScreen(
                headerContent = headerContent,
                modifier = modifier,
                buttonsContent = buttonsContent,
                content = failedContent?.let {
                    {
                        item { failedContent() }
                    }
                },
            )
        }
    }
}

/**
 * Represents the state of [EntityScreen].
 */
@ExperimentalHorologistApi
public sealed class EntityScreenState<out Media> {
    public object Loading : EntityScreenState<Nothing>()

    public data class Loaded<Media>(
        val mediaList: List<Media>,
    ) : EntityScreenState<Media>()

    public object Failed : EntityScreenState<Nothing>()
}

/**
 * A default implementation of a header for [EntityScreen].
 */
@ExperimentalHorologistApi
@Composable
public fun DefaultEntityScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    ListHeader(modifier = modifier) { Text(title) }
}
