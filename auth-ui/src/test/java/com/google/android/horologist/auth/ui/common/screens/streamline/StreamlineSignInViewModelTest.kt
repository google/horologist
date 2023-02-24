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
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.mapper.AccountUiModelMapper
import com.google.android.horologist.test.toolbox.rules.MainDispatcherRule
import com.google.android.horologist.test.toolbox.testdoubles.StreamlineAccountRepositoryStub
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StreamlineSignInViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val streamlineAccountRepository = StreamlineAccountRepositoryStub<AuthUser>()

    private lateinit var sut: StreamlineSignInViewModel<AuthUser, AccountUiModel>

    @Before
    fun setUp() {
        sut = StreamlineSignInViewModel(
            streamlineAccountRepository = streamlineAccountRepository,
            uiModelMapper = AccountUiModelMapper::map
        )
    }

    @Test
    fun givenInitialState_thenStateIsParentStateIdle() {
        // when
        val result = sut.uiState.value

        // then
        assertThat(result).isEqualTo(StreamlineSignInScreenState.Idle)
    }

    @Test
    fun givenInitialState_whenOnIdleStateObserved_thenStateIsParentStateLoading() = runTest {
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
    fun givenNoAccountsAvailable_whenOnIdleStateObserved_thenStateIsNoAccountsAvailable() = runTest {
        // when
        sut.onIdleStateObserved()

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(StreamlineSignInScreenState.NoAccountsAvailable)
        }
    }

    @Test
    fun givenSingleAccountAvailable_whenOnIdleStateObserved_thenStateIsSingleAccountAvailable() = runTest {
        // given
        val email = "user@example.com"
        streamlineAccountRepository.accountList = listOf(AuthUser(email = email))

        // when
        sut.onIdleStateObserved()

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(
                StreamlineSignInScreenState.SignedIn(AccountUiModel(email = email))
            )
        }
    }

    @Test
    fun whenOnAccountSelected_thenStateIsSignedIn() = runTest {
        // given
        val account = AccountUiModel(email = "email@example.com")

        // when
        sut.onAccountSelected(account)

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(StreamlineSignInScreenState.SignedIn(account))
        }
    }
}
