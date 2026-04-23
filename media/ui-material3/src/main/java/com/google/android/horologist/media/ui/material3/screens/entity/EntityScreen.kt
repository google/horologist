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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnItemScope
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.Box
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
    content: (TransformingLazyColumnScope.() -> Unit)? = null,
) {
    val scrollState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = scrollState) { contentPadding ->
        TransformingLazyColumn(
            state = scrollState,
            contentPadding = contentPadding,
            modifier = modifier,
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            with(transformationSpec) {
                                applyContainerTransformation(scrollProgress)
                            }
                        }
                        .transformedHeight(this, transformationSpec)
                ) {
                    headerContent()
                }
            }

            buttonsContent?.let {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                with(transformationSpec) {
                                    applyContainerTransformation(scrollProgress)
                                }
                            }
                            .transformedHeight(this, transformationSpec)
                    ) {
                        buttonsContent()
                    }
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
    mediaContent: @Composable TransformingLazyColumnItemScope.(media: Media) -> Unit,
    modifier: Modifier = Modifier,
    buttonsContent: (@Composable () -> Unit)? = null,
) {
    EntityScreen(
        headerContent = headerContent,
        modifier = modifier,
        buttonsContent = buttonsContent,
        content = {
            items(count = mediaList.size) { index ->
                this.mediaContent(mediaList[index])
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
    loadingContent: TransformingLazyColumnScope.() -> Unit,
    mediaContent: @Composable TransformingLazyColumnItemScope.(media: Media) -> Unit,
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
