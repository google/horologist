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

package com.google.android.horologist.mediasample.data.datasource

import com.google.android.horologist.mediasample.data.mapper.PlaylistMapper
import com.google.android.horologist.test.toolbox.testdoubles.FakeUampService
import com.google.android.horologist.test.toolbox.testdoubles.InMemoryErrorReporter
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

class PlaylistRemoteDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()

    private val uampService = FakeUampService()

    private val errorLogger = InMemoryErrorReporter()

    private lateinit var sut: PlaylistRemoteDataSource

    @Before
    fun setUp() {
        sut = PlaylistRemoteDataSource(
            ioDispatcher = testDispatcher,
            uampService = uampService,
            errorReporter = errorLogger
        )
    }

    @Test
    fun getPlaylists_backedByUampServiceCatalog() = runTest(testDispatcher) {
        // given
        val uampServiceCatalogResult = PlaylistMapper.map(uampService.catalog())

        // when
        val result = sut.getPlaylists().first()

        // then
        assertThat(result.getOrNull()).isEqualTo(uampServiceCatalogResult)
    }

    @Test
    fun handlesFailures() = runTest(testDispatcher) {
        // given
        uampService.failing = IOException()

        // when
        val result = sut.getPlaylists().first()

        // then
        assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
        assertThat(errorLogger.messages).isNotEmpty()
    }
}
