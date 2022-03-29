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

package com.google.android.horologist.compose.layout

import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.audio.ExperimentalAudioApi
import com.google.android.horologist.audio.SystemVolumeRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Test

@OptIn(ExperimentalAudioApi::class)
class VolumeRepositoryTest {
    @Test
    fun testVolumeRepository() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        SystemVolumeRepository.fromContext(context).use { repository ->
            if (repository.volumeState.value.isMax) {
                repository.decreaseVolume()
            }

            val startingVolume = repository.volumeState.value

            repository.increaseVolume()

            val newVolume = repository.volumeState.value

            assertThat(newVolume.current).isGreaterThan(startingVolume.current)

            repository.close()
        }
    }
}
