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

@file:OptIn(ExperimentalHorologistComposablesApi::class)

package com.google.android.horologist.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.ScalingLazyListScope
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.ScalingParams
import com.google.android.horologist.compose.navscaffold.scrollableColumn

/**
 * A list component that is split into [sections][Section].
 * Each [Section] has its own [state][Section.State] controlled individually.
 */
@ExperimentalHorologistComposablesApi
@Composable
public fun SectionedList(
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    scope: SectionedListScope.() -> Unit
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, scalingLazyListState),
        state = scalingLazyListState,
        scalingParams = scalingParams
    ) {
        SectionedListScope().apply(scope).sections.forEach { section ->
            section.display(this)
        }
    }
}

internal fun <T> Section<T>.display(scope: ScalingLazyListScope) {
    val section = this
    section.headerContent?.let { content ->
        scope.item { content() }
    }

    when (section.state) {
        Section.State.Loading -> {
            section.loadingContent?.let { content ->
                scope.item { content() }
            }
        }
        is Section.State.Loaded<*> -> {
            val list = section.state.list
            scope.items(list.size) { index ->
                @Suppress("UNCHECKED_CAST")
                section.loadedContent(list[index] as T)
            }
        }
        Section.State.Failed -> {
            section.failedContent?.let { content ->
                scope.item { content() }
            }
        }
        Section.State.Empty -> {
            section.emptyContent?.let { content ->
                scope.item { content() }
            }
        }
    }

    section.footerContent?.let { content ->
        if (!displayFooterOnlyOnLoadedState || state is Section.State.Loaded<*>) {
            scope.item { content() }
        }
    }
}

/**
 * A section in [SectionedList].
 */
@ExperimentalHorologistComposablesApi
public data class Section<T> internal constructor(
    val state: State,
    val headerContent: (@Composable () -> Unit)? = null,
    val loadingContent: (@Composable () -> Unit)? = null,
    val loadedContent: @Composable (T) -> Unit,
    val failedContent: (@Composable () -> Unit)? = null,
    val emptyContent: (@Composable () -> Unit)? = null,
    val footerContent: (@Composable () -> Unit)? = null,
    val displayFooterOnlyOnLoadedState: Boolean = true
) {
    /**
     * A state of a [Section].
     */
    public sealed class State {
        public object Loading : State()

        public data class Loaded<T>(
            val list: List<T>
        ) : State()

        public object Failed : State()

        public object Empty : State()
    }
}

/**
 * Receiver scope which is used by [SectionedList].
 */
@ExperimentalHorologistComposablesApi
public class SectionedListScope {

    internal val sections: MutableList<Section<*>> = mutableListOf()

    public fun <T> section(
        state: Section.State,
        displayFooterOnlyOnLoadedState: Boolean = true,
        builder: SectionContentScope<T>.() -> Unit
    ) {
        with(SectionContentScope<T>().apply(builder)) {
            sections.add(
                Section(
                    state = state,
                    headerContent = this.headerContent,
                    loadingContent = this.loadingContent,
                    loadedContent = this.loadedContent,
                    failedContent = this.failedContent,
                    emptyContent = this.emptyContent,
                    footerContent = this.footerContent,
                    displayFooterOnlyOnLoadedState = displayFooterOnlyOnLoadedState
                )
            )
        }
    }

    /**
     * Add a section in [loaded][Section.State.Loaded] state.
     */
    public fun <T> section(
        list: List<T>,
        builder: SectionContentScope<T>.() -> Unit
    ): Unit = section(
        state = Section.State.Loaded(list),
        displayFooterOnlyOnLoadedState = true,
        builder = builder
    )

    /**
     * Add a section in [loaded][Section.State.Loaded] state with a single item.
     */
    public fun section(
        builder: SectionContentScope<Unit>.() -> Unit
    ): Unit = section(
        state = Section.State.Loaded(listOf(Unit)),
        displayFooterOnlyOnLoadedState = true,
        builder = builder
    )
}

/**
 * Receiver scope which is used by [SectionedListScope].
 */
@ExperimentalHorologistComposablesApi
public class SectionContentScope<T> {

    internal var headerContent: @Composable () -> Unit = { }
        private set

    internal var loadingContent: @Composable () -> Unit = { }
        private set

    internal var loadedContent: @Composable (T) -> Unit = { }
        private set

    internal var failedContent: @Composable () -> Unit = { }
        private set

    internal var emptyContent: @Composable () -> Unit = { }
        private set

    internal var footerContent: @Composable () -> Unit = { }
        private set

    public fun header(content: @Composable () -> Unit) {
        headerContent = content
    }

    public fun loading(content: @Composable () -> Unit) {
        loadingContent = content
    }

    public fun loaded(content: @Composable (T) -> Unit) {
        loadedContent = content
    }

    public fun failed(content: @Composable () -> Unit) {
        failedContent = content
    }

    public fun empty(content: @Composable () -> Unit) {
        emptyContent = content
    }

    public fun footer(content: @Composable () -> Unit) {
        footerContent = content
    }
}
