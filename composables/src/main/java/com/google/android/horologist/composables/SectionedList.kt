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

package com.google.android.horologist.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.Section.Companion.ALL_STATES
import com.google.android.horologist.composables.Section.Companion.DEFAULT_LOADING_CONTENT_COUNT
import com.google.android.horologist.composables.Section.Companion.LOADED_STATE_ONLY
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

/**
 * A list component that is split into [sections][Section].
 * Each [Section] has its own [state][Section.State] controlled individually.
 */
@ExperimentalHorologistApi
@Composable
public fun SectionedList(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState,
    content: SectionedListScope.() -> Unit,
) {
    SectionedList(
        columnState = columnState,
        modifier = modifier,
        sections = SectionedListScope().apply(content).sections,
    )
}

/**
 * A list component that is split into [sections][Section].
 * Each [Section] has its own [state][Section.State] controlled individually.
 */
@ExperimentalHorologistApi
@Composable
public fun SectionedList(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState,
    sections: List<Section<*>> = emptyList(),
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier,
    ) {
        for (section in sections) {
            section.display(this)
        }
    }
}

internal fun <T> shouldDisplay(
    visibleStates: Section.VisibleStates,
    state: Section.State<T>,
): Boolean {
    return when (state) {
        Section.State.Empty -> visibleStates.empty
        Section.State.Failed -> visibleStates.failed
        is Section.State.Loaded -> visibleStates.loaded
        Section.State.Loading -> visibleStates.loading
    }
}

internal fun <T> Section<T>.display(scope: ScalingLazyListScope) {
    val section = this

    section.headerContent?.let { content ->
        if (shouldDisplay(headerVisibleStates, section.state)) {
            scope.item { SectionContentScope.content() }
        }
    }

    when (section.state) {
        Section.State.Loading -> {
            loadingContent?.let { content ->
                scope.items(loadingContentCount) { SectionContentScope.content() }
            }
        }

        is Section.State.Loaded -> {
            loadedContent?.let { content ->
                val list = section.state.list
                scope.items(list.size) { index ->
                    content(SectionContentScope, list[index])
                }
            }
        }

        Section.State.Failed -> {
            failedContent?.let { content ->
                scope.item { SectionContentScope.content() }
            }
        }

        Section.State.Empty -> {
            emptyContent?.let { content ->
                scope.item { SectionContentScope.content() }
            }
        }
    }

    footerContent?.let { content ->
        if (shouldDisplay(footerVisibleStates, section.state)) {
            scope.item { SectionContentScope.content() }
        }
    }
}

/**
 * A section in [SectionedList].
 */
@ExperimentalHorologistApi
public data class Section<T>(
    val state: State<T>,
    val headerContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val headerVisibleStates: VisibleStates = ALL_STATES,
    val loadingContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val loadingContentCount: Int = DEFAULT_LOADING_CONTENT_COUNT,
    val loadedContent: (@Composable SectionContentScope.(T) -> Unit)? = null,
    val failedContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val emptyContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val footerContent: (@Composable SectionContentScope.() -> Unit)? = null,
    val footerVisibleStates: VisibleStates = LOADED_STATE_ONLY,
) {
    /**
     * A state of a [Section].
     */
    public sealed class State<out T> {
        public object Loading : State<Nothing>()

        public data class Loaded<T>(
            val list: List<T>,
        ) : State<T>()

        public object Failed : State<Nothing>()

        public object Empty : State<Nothing>()
    }

    /**
     * Define on which states the section's header or footer should be visible.
     */
    @ExperimentalHorologistApi
    public data class VisibleStates(
        val loading: Boolean,
        val loaded: Boolean,
        val failed: Boolean,
        val empty: Boolean,
    )

    @ExperimentalHorologistApi
    public companion object {
        internal const val DEFAULT_LOADING_CONTENT_COUNT: Int = 1

        public val ALL_STATES: VisibleStates = VisibleStates(
            loading = true,
            loaded = true,
            failed = true,
            empty = true,
        )

        public val LOADED_STATE_ONLY: VisibleStates = VisibleStates(
            loading = false,
            loaded = true,
            failed = false,
            empty = false,
        )

        public val NO_STATES: VisibleStates = VisibleStates(
            loading = false,
            loaded = false,
            failed = false,
            empty = false,
        )
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
@ExperimentalHorologistApi
@SectionScopeMarker
public class SectionedListScope {

    internal val sections: MutableList<Section<*>> = mutableListOf()

    @SectionScopeMarker
    public fun <T> section(
        state: Section.State<T>,
        content: SectionScope<T>.() -> Unit,
    ) {
        SectionScope<T>().apply(content).let { scope ->
            sections.add(
                Section(
                    state = state,
                    headerContent = scope.headerContent,
                    headerVisibleStates = scope.headerVisibleStates,
                    loadingContent = scope.loadingContent,
                    loadingContentCount = scope.loadingContentCount,
                    loadedContent = scope.loadedContent,
                    failedContent = scope.failedContent,
                    emptyContent = scope.emptyContent,
                    footerContent = scope.footerContent,
                    footerVisibleStates = scope.footerVisibleStates,
                ),
            )
        }
    }

    /**
     * Add a section in [loaded][Section.State.Loaded] state.
     */
    @SectionScopeMarker
    public fun <T> section(
        list: List<T>,
        content: SectionScope<T>.() -> Unit,
    ): Unit = section(
        state = Section.State.Loaded(list),
        content = content,
    )

    /**
     * Add a section in [loaded][Section.State.Loaded] state with a single item.
     */
    @SectionScopeMarker
    public fun section(
        content: SectionScope<Unit>.() -> Unit,
    ): Unit = section(
        state = Section.State.Loaded(listOf(Unit)),
        content = content,
    )
}

/**
 * Receiver scope which is used by content parameter in [SectionedListScope] functions.
 */
@ExperimentalHorologistApi
@SectionScopeMarker
public class SectionScope<T> {

    internal var headerContent: (@Composable SectionContentScope.() -> Unit)? = null
        private set

    internal var headerVisibleStates: Section.VisibleStates = ALL_STATES
        private set

    internal var loadingContent: (@Composable SectionContentScope.() -> Unit)? = null
        private set

    internal var loadingContentCount: Int = DEFAULT_LOADING_CONTENT_COUNT
        private set

    internal var loadedContent: (@Composable SectionContentScope.(T) -> Unit)? = null
        private set

    internal var failedContent: (@Composable SectionContentScope.() -> Unit)? = null
        private set

    internal var emptyContent: (@Composable SectionContentScope.() -> Unit)? = null
        private set

    internal var footerContent: (@Composable SectionContentScope.() -> Unit)? = null
        private set

    internal var footerVisibleStates: Section.VisibleStates = LOADED_STATE_ONLY
        private set

    @SectionScopeMarker
    public fun header(
        visibleStates: Section.VisibleStates = ALL_STATES,
        content: @Composable SectionContentScope.() -> Unit,
    ) {
        headerVisibleStates = visibleStates
        headerContent = content
    }

    @SectionScopeMarker
    public fun loading(
        count: Int = DEFAULT_LOADING_CONTENT_COUNT,
        content: @Composable SectionContentScope.() -> Unit,
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
    public fun footer(
        visibleStates: Section.VisibleStates = LOADED_STATE_ONLY,
        content: @Composable SectionContentScope.() -> Unit,
    ) {
        footerVisibleStates = visibleStates
        footerContent = content
    }
}

/**
 * Receiver scope which is used by content parameter in [SectionScope] functions.
 */
@ExperimentalHorologistApi
@SectionScopeMarker
public object SectionContentScope
