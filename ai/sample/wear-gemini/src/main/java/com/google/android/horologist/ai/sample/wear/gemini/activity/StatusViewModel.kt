/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.ai.sample.wear.gemini.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import com.google.protobuf.empty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatusViewModel
    @Inject
    constructor(
        service: InferenceServiceGrpcKt.InferenceServiceCoroutineStub,
    ) : ViewModel() {
        val uiState = flow {
            emit(StatusUiState(serviceName = service.serviceInfo(empty { }).name))
        }.stateIn(viewModelScope, SharingStarted.Lazily, StatusUiState())
    }
