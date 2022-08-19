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

import com.google.android.horologist.mediasample.data.api.UampService
import com.google.android.horologist.mediasample.data.api.model.CatalogApiModel
import com.google.android.horologist.mediasample.di.annotation.Dispatcher
import com.google.android.horologist.mediasample.di.annotation.UampDispatchers.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PlaylistRemoteDataSource(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val uampService: UampService
) {

    fun getPlaylists(): Flow<CatalogApiModel> = flow {
        emit(uampService.catalog())
    }.flowOn(ioDispatcher)
}
