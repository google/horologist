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

package com.google.android.horologist.auth.sample.screens.tokenshare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.auth.data.tokenshare.TokenBundleRepository
import com.google.android.horologist.auth.data.tokenshare.TokenBundleRepositoryImpl
import com.google.android.horologist.auth.sample.SampleApplication
import com.google.android.horologist.auth.sample.shared.TokenSerializer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn

class TokenShareListenerViewModel(
    tokenBundleRepository: TokenBundleRepository<String>
) : ViewModel() {

    public val uiState: StateFlow<List<String>> =
        tokenBundleRepository.flow
            .map { listOf(it) }
            .runningReduce { accumulator, value -> accumulator + value }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        public val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY]!!

                TokenShareListenerViewModel(
                    TokenBundleRepositoryImpl.create(
                        serializer = TokenSerializer,
                        applicationContext = application,
                        coroutineScope = (application as SampleApplication).servicesCoroutineScope
                    )
                )
            }
        }
    }
}
