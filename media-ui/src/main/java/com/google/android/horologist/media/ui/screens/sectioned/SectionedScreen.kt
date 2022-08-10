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

package com.google.android.horologist.media.ui.screens.sectioned

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListItemScope
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi

/**
 * A screen that is split into [sections][Section].
 * Each [Section] has its own [state][Section.State] controlled individually.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun SectionedScreen(
    sections: List<Section>,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, scalingLazyListState),
        state = scalingLazyListState
    ) {
        sections.forEach { section ->
            section.headerContent?.let { content ->
                item { content() }
            }

            when (val sectionState = section.state) {
                is Section.State.Loading -> {
                    item { sectionState.content(this) }
                }
                is Section.State.Loaded -> {
                    sectionState.contentItems.forEach { content ->
                        item { content() }
                    }
                }
                is Section.State.Failed -> {
                    item { sectionState.content(this) }
                }
                is Section.State.Empty -> {
                    item { sectionState.content(this) }
                }
            }

            section.footerContent?.let { content ->
                item { content() }
            }
        }
    }
}

/**
 * A section in [SectionedScreen].
 */
@ExperimentalHorologistMediaUiApi
public data class Section(
    val state: State,
    val headerContent: (@Composable ScalingLazyListItemScope.() -> Unit)?,
    val footerContent: (@Composable ScalingLazyListItemScope.() -> Unit)? = null
) {

    /**
     * A state of a [Section].
     */
    public sealed class State {

        public data class Loading(
            val content: @Composable ScalingLazyListItemScope.() -> Unit
        ) : State()

        public data class Loaded(
            val contentItems: List<@Composable ScalingLazyListItemScope.() -> Unit>
        ) : State()

        public data class Failed(
            val content: @Composable ScalingLazyListItemScope.() -> Unit
        ) : State()

        public data class Empty(
            val content: @Composable ScalingLazyListItemScope.() -> Unit
        ) : State()
    }
}
