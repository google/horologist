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

@file:Suppress("UNCHECKED_CAST")

package com.google.android.horologist.compose.tools.a11y

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.wear.compose.material.ScalingLazyListState
import kotlin.reflect.full.declaredMemberProperties

public fun ScalingLazyListState.forceState(topIndex: Int, topScrollOffset: Int) {
    this.lazyListState.scrollPositionScrollOffset.value = topScrollOffset
    this.lazyListState.scrollPositionIndex = topIndex
    this.initialized.value = true
}

private val ScalingLazyListState.initialized: MutableState<Boolean>
    get() {
        return ScalingLazyListState::class.declaredMemberProperties.first { it.name == "initialized" }
            .get(this) as MutableState<Boolean>
    }

private val ScalingLazyListState.lazyListState: LazyListState
    get() {
        return ScalingLazyListState::class.declaredMemberProperties.first { it.name == "lazyListState" }
            .get(this) as LazyListState
    }

private val LazyListState.scrollPosition: Any
    get() {
        return LazyListState::class.java.declaredFields.first { it.name == "scrollPosition" }
            .apply {
                isAccessible = true
            }
            .get(this) as Any
    }

private var LazyListState.scrollPositionIndex: Int
    get() {
        val positionType = Class.forName("androidx.compose.foundation.lazy.LazyListScrollPosition")
        val dataIndexType = Class.forName("androidx.compose.foundation.lazy.DataIndex")
        val dataIndex = positionType.declaredFields
            .first { it.name == "index\$delegate" }.apply {
                isAccessible = true
            }
            .get(scrollPosition) as MutableState<Any>
        return dataIndexType.declaredFields
            .first { it.name == "value" }.apply {
                isAccessible = true
            }
            .get(dataIndex.value) as Int
    }
    set(value) {
        val positionType = Class.forName("androidx.compose.foundation.lazy.LazyListScrollPosition")
        val dataIndexType = Class.forName("androidx.compose.foundation.lazy.DataIndex")
        val dataIndex = positionType.declaredFields
            .first { it.name == "index\$delegate" }.apply {
                isAccessible = true
            }
            .get(scrollPosition) as MutableState<Any>
        dataIndexType.declaredFields
            .first { it.name == "value" }.apply {
                isAccessible = true
            }
            .set(dataIndex.value, value)
    }

private val LazyListState.scrollPositionScrollOffset: MutableState<Int>
    get() {
        val type = Class.forName("androidx.compose.foundation.lazy.LazyListScrollPosition")
        return type.declaredFields
            .first { it.name == "scrollOffset\$delegate" }.apply {
                isAccessible = true
            }
            .get(scrollPosition) as MutableState<Int>
    }
