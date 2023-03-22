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

package com.google.android.horologist.audio

import androidx.test.annotation.UiThreadTest
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalHorologistApi::class)
@MediumTest
class SystemAudioRepositoryTest {
    @Test
    @UiThreadTest
    fun testAudioOutputRepository() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        SystemAudioRepository.fromContext(context).use { repository ->
            assertThat(repository.audioOutput.value).isNotNull()
            assertThat(repository.available.value).isNotEmpty()

            repository.close()

            assertThat(repository.audioOutput.value).isEqualTo(AudioOutput.None)
            assertThat(repository.available.value).isEmpty()
        }
    }

    @Test
    fun testVolumeRepository() = runTest(dispatchTimeoutMs = 10000) {
        withContext(Dispatchers.Main) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            SystemAudioRepository.fromContext(context).use { repository ->
                if (repository.volumeState.value.isMax) {
                    repository.decreaseVolume()
                }

                val startingVolume = repository.volumeState.value.current

                repository.increaseVolume()

                val newVolume = repository.volumeState
                    .map { it.current }
                    .filter { it > startingVolume }
                    .first()

                assertThat(newVolume).isGreaterThan(startingVolume)
                repository.close()
            }
        }
    }
}
