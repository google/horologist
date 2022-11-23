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

package com.google.android.horologist.media.ui.screens.entity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.ScalingLazyListScope
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.ScalingParams
import com.google.android.horologist.base.ui.ExperimentalHorologistBaseUiApi
import com.google.android.horologist.base.ui.components.Title
import com.google.android.horologist.compose.focus.RequestFocusWhenActive
import com.google.android.horologist.compose.rotaryinput.rotaryWithFling
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi

/**
 * A screen that displays a media collection and allow actions to be taken on it.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun EntityScreen(
    headerContent: @Composable () -> Unit,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    autoCentering: AutoCenteringParams? = AutoCenteringParams(),
    buttonsContent: (@Composable () -> Unit)? = null,
    content: (ScalingLazyListScope.() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .rotaryWithFling(focusRequester, scalingLazyListState),
        state = scalingLazyListState,
        scalingParams = scalingParams,
        autoCentering = autoCentering
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

    RequestFocusWhenActive(focusRequester)
}

/**
 * A screen that displays a [Media] collection and allow actions to be taken on it.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun <Media> EntityScreen(
    headerContent: @Composable () -> Unit,
    mediaList: List<Media>,
    mediaContent: @Composable (media: Media) -> Unit,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    autoCentering: AutoCenteringParams? = AutoCenteringParams(),
    buttonsContent: (@Composable () -> Unit)? = null
) {
    EntityScreen(
        headerContent = headerContent,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        scalingParams = scalingParams,
        autoCentering = autoCentering,
        buttonsContent = buttonsContent,
        content = {
            items(count = mediaList.size) { index ->
                mediaContent(mediaList[index])
            }
        }
    )
}

/**
 * A screen that displays a [Media] collection and allow actions to be taken on it.
 * The content displayed is based on the screen's [state][EntityScreenState].
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun <Media> EntityScreen(
    entityScreenState: EntityScreenState<Media>,
    headerContent: @Composable () -> Unit,
    loadingContent: ScalingLazyListScope.() -> Unit,
    mediaContent: @Composable (media: Media) -> Unit,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    autoCentering: AutoCenteringParams? = AutoCenteringParams(),
    buttonsContent: (@Composable () -> Unit)? = null,
    failedContent: (@Composable () -> Unit)? = null
) {
    when (entityScreenState) {
        is EntityScreenState.Loading -> {
            EntityScreen(
                headerContent = headerContent,
                scalingLazyListState = scalingLazyListState,
                modifier = modifier,
                scalingParams = scalingParams,
                autoCentering = autoCentering,
                buttonsContent = buttonsContent,
                content = loadingContent
            )
        }

        is EntityScreenState.Loaded -> {
            EntityScreen(
                headerContent = headerContent,
                mediaList = entityScreenState.mediaList,
                mediaContent = mediaContent,
                scalingLazyListState = scalingLazyListState,
                modifier = modifier,
                scalingParams = scalingParams,
                autoCentering = autoCentering,
                buttonsContent = buttonsContent
            )
        }

        is EntityScreenState.Failed -> {
            EntityScreen(
                headerContent = headerContent,
                scalingLazyListState = scalingLazyListState,
                modifier = modifier,
                autoCentering = autoCentering,
                scalingParams = scalingParams,
                buttonsContent = buttonsContent,
                content = failedContent?.let {
                    { item { failedContent() } }
                }
            )
        }
    }
}

/**
 * Represents the state of [EntityScreen].
 */
@ExperimentalHorologistMediaUiApi
public sealed class EntityScreenState<Media> {
    public class Loading<Media> : EntityScreenState<Media>()

    public data class Loaded<Media>(
        val mediaList: List<Media>
    ) : EntityScreenState<Media>()

    public class Failed<Media> : EntityScreenState<Media>()
}

/**
 * A default implementation of a header for [EntityScreen].
 */
@ExperimentalHorologistBaseUiApi
@ExperimentalHorologistMediaUiApi
@Composable
public fun DefaultEntityScreenHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Title(text = title, modifier = modifier.padding(bottom = 12.dp))
}
