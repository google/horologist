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
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalAudioApi
import com.google.android.horologist.audio.SystemAudioOutputRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Test

@OptIn(ExperimentalAudioApi::class)
class AudioOutputRepositoryTest {
    @Test
    fun testAudioOutputRepository() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        SystemAudioOutputRepository.fromContext(context).use { repository ->
            assertThat(repository.audioOutput.value).isNotNull()
            assertThat(repository.available.value).isNotEmpty()

            repository.close()

            assertThat(repository.audioOutput.value).isEqualTo(AudioOutput.None)
            assertThat(repository.available.value).isEmpty()
        }
    }
}
