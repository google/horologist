/*
 * Copyright 2023 The Android Open Source Project
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
    ExperimentalWearFoundationApi::class, ExperimentalWearFoundationApi::class,
    ExperimentalTestApi::class
)

package com.google.android.horologist.compose.rotaryinput

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performRotaryScrollInput
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path.Companion.toPath
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [30],
    qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReplayScrollEffectTest {

    @get:Rule
    val rule = createComposeRule().apply {
        mainClock.autoAdvance = false
    }

    val fileSystem = FileSystem.RESOURCES

    @Test
    fun slow_mid_fast_fling() = runTest {
        val testFile = readFile("slow_mid_fast_fling")

        val input = testFile.filter { it.side == Event.Side.In }
        val output = mutableListOf<Event>()

        val state = ScrollableState {
            output.add(Event(Event.Side.Out, it, rule.mainClock.currentTime))
            it
        }

        rule.setContent {
            val focusRequester = rememberActiveFocusRequester()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotaryWithFling(focusRequester, state),
            )
        }

        rule.awaitIdle()

        var timestamp = 0L
        input.forEach {
            rule.mainClock.advanceTimeBy(it.timestamp - timestamp)
            timestamp = it.timestamp

            rule.onRoot().performRotaryScrollInput {
                rotateToScrollHorizontally(it.scrollDistance)
            }
        }

        assertThat(output).isEqualTo(testFile.filter { it.side == Event.Side.Out })
    }

    private fun readFile(name: String): List<Event> = fileSystem.read("/$name.txt".toPath()) {
        val fileList = buildList {
            while (true) {
                val line = readUtf8Line() ?: break

                add(Event.fromString(line))
            }
        }

        val firstTimestamp = fileList.first().timestamp
        fileList.map { it.copy(timestamp = it.timestamp - firstTimestamp) }
    }

    data class Event(
        val side: Side,
        val scrollDistance: Float,
        val timestamp: Long
    ) {
        enum class Side {
            In, Out
        }

        companion object {
            fun fromString(line: String): Event {
                val (direction, scrollDistance, timestamp) = line.split(',', limit = 3)
                return Event(Side.valueOf(direction), scrollDistance.toFloat(), timestamp.toLong())
            }
        }
    }
}