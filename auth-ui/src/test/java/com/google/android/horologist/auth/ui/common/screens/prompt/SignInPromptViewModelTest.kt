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
    ExperimentalHorologistAuthUiApi::class,
    ExperimentalCoroutinesApi::class
)

package com.google.android.horologist.auth.ui.common.screens.prompt

import app.cash.turbine.test
import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.test.toolbox.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignInPromptViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeAuthUserRepository = AuthUserRepositoryStub()

    private lateinit var sut: SignInPromptViewModel

    @Before
    fun setUp() {
        sut = SignInPromptViewModel(fakeAuthUserRepository)
    }

    @Test
    fun givenInitialState_thenStateIsIdle() {
        // when
        val result = sut.uiState.value

        // then
        assertThat(result).isEqualTo(SignInPromptScreenState.Idle)
    }

    @Test
    fun givenInitialState_whenOnIdleStateObserved_thenStateIsLoading() = runTest {
        // when
        val whenBlock = { sut.onIdleStateObserved() }

        // then
        sut.uiState.test {
            skipItems(1)

            whenBlock()

            assertThat(awaitItem()).isEqualTo(SignInPromptScreenState.Loading)

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
            assertThat(awaitItem()).isNotEqualTo(SignInPromptScreenState.Idle)

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
            assertThat(awaitItem()).isEqualTo(SignInPromptScreenState.SignedOut)
        }
    }

    @Test
    fun givenUserAuthenticated_whenOnIdleStateObserved_thenStateIsSignedIn() = runTest {
        // given
        fakeAuthUserRepository.authUser = AuthUser()

        // when
        sut.onIdleStateObserved()

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(SignInPromptScreenState.SignedIn)
        }
    }

    private class AuthUserRepositoryStub : AuthUserRepository {

        var authUser: AuthUser? = null

        override suspend fun getAuthenticated(): AuthUser? {
            return authUser
        }
    }
}
