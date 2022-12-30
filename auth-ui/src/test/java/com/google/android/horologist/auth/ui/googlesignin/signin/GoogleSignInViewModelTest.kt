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

package com.google.android.horologist.auth.ui.googlesignin.signin

import app.cash.turbine.test
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.horologist.auth.data.googlesignin.GoogleSignInEventListener
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.test.toolbox.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GoogleSignInViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val spyGoogleSignInEventListener = GoogleSignInEventListenerSpy()

    private lateinit var sut: GoogleSignInViewModel

    @Before
    fun setUp() {
        sut = GoogleSignInViewModel(spyGoogleSignInEventListener)
    }

    @Test
    fun givenInitialState_thenStateIsIdle() = runTest {
        // when
        val result = sut.uiState.value

        // then
        assertThat(result).isEqualTo(GoogleSignInScreenState.Idle)
    }

    @Test
    fun givenInitialState_whenOnIdleStateObserved_thenStateIsSelectAccount() = runTest {
        // when
        val whenBlock = { sut.onIdleStateObserved() }

        // then
        sut.uiState.test {
            skipItems(1)

            whenBlock()

            assertThat(awaitItem()).isEqualTo(GoogleSignInScreenState.SelectAccount)
        }
    }

    @Test
    fun givenNonIdleState_whenOnIdleStateObserved_thenStateIsTheSame() = runTest {
        // when
        sut.onIdleStateObserved()
        val whenBlock = { sut.onIdleStateObserved() }

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isNotEqualTo(GoogleSignInScreenState.Idle)

            whenBlock()

            expectNoEvents()
        }
    }

    @Test
    fun whenOnAccountSelected_thenStateIsSuccess() = runTest {
        // when
        val account = GoogleSignInAccount.createDefault()
        sut.onAccountSelected(account)

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isInstanceOf(GoogleSignInScreenState.Success::class.java)
        }
        assertThat(spyGoogleSignInEventListener.onSignedInAccount).isEqualTo(account)
    }

    @Test
    fun whenOnAccountSelectionFailed_thenStateIsFailed() = runTest {
        // when
        sut.onAccountSelectionFailed()

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(GoogleSignInScreenState.Failed)
        }
    }

    @Test
    fun whenOnAuthCancelled_thenStateIsCancelled() = runTest {
        // when
        sut.onAuthCancelled()

        // then
        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(GoogleSignInScreenState.Cancelled)
        }
    }

    private class GoogleSignInEventListenerSpy : GoogleSignInEventListener {

        var onSignedInAccount: GoogleSignInAccount? = null
            private set

        override suspend fun onSignedIn(account: GoogleSignInAccount) {
            onSignedInAccount = account
        }
    }
}
