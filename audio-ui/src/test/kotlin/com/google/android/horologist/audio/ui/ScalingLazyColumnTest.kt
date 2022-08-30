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

@file:OptIn(ExperimentalHorologistAudioApi::class)

package com.google.android.horologist.audio.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test
import kotlin.reflect.full.declaredMemberProperties

class ScalingLazyColumnTest {
    private val maxPercentDifference = 0.1

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = maxPercentDifference,
        snapshotHandler = WearSnapshotHandler(determineHandler(maxPercentDifference))
    )

    @Test
    fun volumeScreenAtMinimum() {
        paparazzi.snapshot {
            val state = rememberScalingLazyListState(
                initialCenterItemIndex = 10,
                initialCenterItemScrollOffset = 0
            )

            PreviewScalingLazyList(state) {
                ScalingLazyColumn(modifier = Modifier.fillMaxSize(), state = state) {
                    items(100) {
                        Text(text = "$it")
                    }
                }
            }
        }
    }

    @Composable
    fun PreviewScalingLazyList(state: ScalingLazyListState, block: @Composable () -> Unit) {
        LaunchedEffect(state) {
            state.forceState()
        }

        block()
    }
}

private fun ScalingLazyListState.forceState() {
    this.lazyListState.scrollPositionScrollOffset.value = 0
    this.lazyListState.scrollPositionIndex = 10
    this.initialized.value = true
}

val ScalingLazyListState.initialized: MutableState<Boolean>
    get() {
        return ScalingLazyListState::class.declaredMemberProperties.first { it.name == "initialized" }
            .get(this) as MutableState<Boolean>
    }

val ScalingLazyListState.lazyListState: LazyListState
    get() {
        return ScalingLazyListState::class.declaredMemberProperties.first { it.name == "lazyListState" }
            .get(this) as LazyListState
    }

val LazyListState.scrollPosition: Any
    get() {
        return LazyListState::class.java.declaredFields.first { it.name == "scrollPosition" }
            .apply {
                isAccessible = true
            }
            .get(this) as Any
    }

var LazyListState.scrollPositionIndex: Int
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

val LazyListState.scrollPositionScrollOffset: MutableState<Int>
    get() {
        val type = Class.forName("androidx.compose.foundation.lazy.LazyListScrollPosition")
        return type.declaredFields
            .first { it.name == "scrollOffset\$delegate" }.apply {
                isAccessible = true
            }
            .get(scrollPosition) as MutableState<Int>
    }
