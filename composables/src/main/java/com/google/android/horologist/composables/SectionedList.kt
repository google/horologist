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

@file:OptIn(
    ExperimentalHorologistComposablesApi::class
)

package com.google.android.horologist.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import com.google.android.horologist.composables.Section.Companion.DEFAULT_LOADING_CONTENT_COUNT
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

/**
 * A list component that is split into [sections][Section].
 * Each [Section] has its own [state][Section.State] controlled individually.
 */
@ExperimentalHorologistComposablesApi
@Composable
public fun SectionedList(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState = ScalingLazyColumnDefaults.belowTimeText().create(),
    content: SectionedListScope.() -> Unit
) {
    SectionedList(
        columnState = columnState,
        modifier = modifier,
        sections = SectionedListScope().apply(content).sections
    )
}

/**
 * A list component that is split into [sections][Section].
 * Each [Section] has its own [state][Section.State] controlled individually.
 */
@ExperimentalHorologistComposablesApi
@Composable
public fun SectionedList(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState = ScalingLazyColumnDefaults.belowTimeText().create(),
    sections: List<Section<*>> = emptyList()
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier
            .fillMaxSize()
    ) {
        sections.forEach { section ->
            section.display(this)
        }
    }
}

internal fun <T> Section<T>.display(scope: ScalingLazyListScope) {
    val section = this
    section.headerContent?.let { content ->
        scope.item { SectionContentScope.content() }
    }

    when (section.state) {
        is Section.State.Loading -> {
            section.loadingContent?.let { content ->
                scope.items(section.loadingContentCount) { SectionContentScope.content() }
            }
        }

        is Section.State.Loaded -> {
            val list = section.state.list
            scope.items(list.size) { index ->
                section.loadedContent(SectionContentScope, list[index])
            }
        }

        is Section.State.Failed -> {
            section.failedContent?.let { content ->
                scope.item { SectionContentScope.content() }
            }
        }

        is Section.State.Empty -> {
            section.emptyContent?.let { content ->
                scope.item { SectionContentScope.content() }
            }
        }
    }

    section.footerContent?.let { content ->
        if (!displayFooterOnlyOnLoadedState || state is Section.State.Loaded<*>) {
            scope.item { SectionContentScope.content() }
        }
    }
}

/**
 * A section in [SectionedList].
 */
@ExperimentalHorologistComposablesApi
public data class Section<T> constructor(
    val state: State<T>,
    val headerContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val loadingContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val loadingContentCount: Int = DEFAULT_LOADING_CONTENT_COUNT,
    val loadedContent: @Composable SectionContentScope.(T) -> Unit,
    val failedContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val emptyContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val footerContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val displayFooterOnlyOnLoadedState: Boolean = true
) {
    /**
     * A state of a [Section].
     */
    public sealed class State<T> {
        public class Loading<T> : State<T>()

        public data class Loaded<T>(
            val list: List<T>
        ) : State<T>()

        public class Failed<T> : State<T>()

        public class Empty<T> : State<T>()
    }

    internal companion object {
        const val DEFAULT_LOADING_CONTENT_COUNT = 1
    }
}

/**
 * DSL marker used to distinguish between scopes of [SectionedList].
 */
@DslMarker
internal annotation class SectionScopeMarker

/**
 * Receiver scope which is used by content parameter in [SectionedList].
 */
@ExperimentalHorologistComposablesApi
@SectionScopeMarker
public class SectionedListScope {

    internal val sections: MutableList<Section<*>> = mutableListOf()

    @SectionScopeMarker
    public fun <T> section(
        state: Section.State<T>,
        displayFooterOnlyOnLoadedState: Boolean = true,
        content: SectionScope<T>.() -> Unit
    ) {
        SectionScope<T>().apply(content).let { scope ->
            sections.add(
                Section(
                    state = state,
                    headerContent = scope.headerContent,
                    loadingContent = scope.loadingContent,
                    loadingContentCount = scope.loadingContentCount,
                    loadedContent = scope.loadedContent,
                    failedContent = scope.failedContent,
                    emptyContent = scope.emptyContent,
                    footerContent = scope.footerContent,
                    displayFooterOnlyOnLoadedState = displayFooterOnlyOnLoadedState
                )
            )
        }
    }

    /**
     * Add a section in [loaded][Section.State.Loaded] state.
     */
    @SectionScopeMarker
    public fun <T> section(
        list: List<T>,
        content: SectionScope<T>.() -> Unit
    ): Unit = section(
        state = Section.State.Loaded(list),
        displayFooterOnlyOnLoadedState = true,
        content = content
    )

    /**
     * Add a section in [loaded][Section.State.Loaded] state with a single item.
     */
    @SectionScopeMarker
    public fun section(
        content: SectionScope<Unit>.() -> Unit
    ): Unit = section(
        state = Section.State.Loaded(listOf(Unit)),
        displayFooterOnlyOnLoadedState = true,
        content = content
    )
}

/**
 * Receiver scope which is used by content parameter in [SectionedListScope] functions.
 */
@ExperimentalHorologistComposablesApi
@SectionScopeMarker
public class SectionScope<T> {

    internal var headerContent: @Composable SectionContentScope.() -> Unit = { }
        private set

    internal var loadingContent: @Composable SectionContentScope.() -> Unit = { }
        private set

    internal var loadingContentCount: Int = DEFAULT_LOADING_CONTENT_COUNT
        private set

    internal var loadedContent: @Composable SectionContentScope.(T) -> Unit = { }
        private set

    internal var failedContent: @Composable SectionContentScope.() -> Unit = { }
        private set

    internal var emptyContent: @Composable SectionContentScope.() -> Unit = { }
        private set

    internal var footerContent: @Composable SectionContentScope.() -> Unit = { }
        private set

    @SectionScopeMarker
    public fun header(content: @Composable SectionContentScope.() -> Unit) {
        headerContent = content
    }

    @SectionScopeMarker
    public fun loading(
        count: Int = DEFAULT_LOADING_CONTENT_COUNT,
        content: @Composable SectionContentScope.() -> Unit
    ) {
        check(count > 0) { "count has to be greater than zero." }
        loadingContentCount = count
        loadingContent = content
    }

    @SectionScopeMarker
    public fun loaded(content: @Composable SectionContentScope.(T) -> Unit) {
        loadedContent = content
    }

    @SectionScopeMarker
    public fun failed(content: @Composable SectionContentScope.() -> Unit) {
        failedContent = content
    }

    @SectionScopeMarker
    public fun empty(content: @Composable SectionContentScope.() -> Unit) {
        emptyContent = content
    }

    @SectionScopeMarker
    public fun footer(content: @Composable SectionContentScope.() -> Unit) {
        footerContent = content
    }
}

/**
 * Receiver scope which is used by content parameter in [SectionScope] functions.
 */
@ExperimentalHorologistComposablesApi
@SectionScopeMarker
public object SectionContentScope
