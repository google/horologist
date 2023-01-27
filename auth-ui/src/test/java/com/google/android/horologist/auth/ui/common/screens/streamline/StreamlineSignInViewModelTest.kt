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

@file:OptIn(ExperimentalHorologistAuthUiApi::class, ExperimentalCoroutinesApi::class)

package com.google.android.horologist.auth.ui.common.screens.streamline

import app.cash.turbine.test
import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.test.toolbox.rules.MainDispatcherRule
import com.google.android.horologist.test.toolbox.testdoubles.AuthUserRepositoryStub
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StreamlineSignInViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeAuthUserRepository = AuthUserRepositoryStub()

    private lateinit var sut: StreamlineSignInViewModel

    @Before
    fun setUp() {
        sut = StreamlineSignInViewModel(fakeAuthUserRepository)
    }

    @Test
    fun givenInitialState_thenStateIsIdle() {
        // when
        val result = sut.uiState.value

        // then
        assertThat(result).isEqualTo(StreamlineSignInScreenState.Idle)
    }

    @Test
    fun givenInitialState_whenOnIdleStateObserved_thenStateIsLoading() = runTest {
        // when
        val whenBlock = { sut.onIdleStateObserved() }

        // then
        sut.uiState.test {
            skipItems(1)

            whenBlock()

            assertThat(awaitItem()).isEqualTo(StreamlineSignInScreenState.Loading)

            skipItems(1)
        }
    }

    @Test
    fun givenNonIdleState_whenOnIdleStateObserved_thenStateIsTheSame() = runTest {
        // when
        sut.onIdleStateObserved()
        val whenBlock = { sut.onIdleStateObserved() }

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isNotEqualTo(StreamlineSignInScreenState.Idle)

            whenBlock()

            expectNoEvents()
        }
    }

    @Test
    fun givenNoUserAuthenticated_whenOnIdleStateObserved_thenStateIsSignedOut() = runTest {
        // when
        sut.onIdleStateObserved()

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(StreamlineSignInScreenState.SignedOut)
        }
    }

    @Test
    fun givenUserAuthenticated_whenOnIdleStateObserved_thenStateIsSignedIn() = runTest {
        // given
        val authUser = AuthUser()
        fakeAuthUserRepository.authUser = authUser

        // when
        sut.onIdleStateObserved()

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(StreamlineSignInScreenState.SignedIn(authUser))
        }
    }
}
