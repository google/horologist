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

package com.google.android.horologist.auth.data.phone.tokenshare.impl

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.horologist.auth.data.phone.ExperimentalHorologistAuthDataPhoneApi
import com.google.android.horologist.auth.data.phone.common.TAG
import com.google.android.horologist.auth.data.phone.tokenshare.TokenRepository
import com.google.android.horologist.auth.data.phone.tokenshare.TokenRepository.Companion.KEY_TOKEN
import com.google.android.horologist.auth.data.phone.tokenshare.TokenRepository.Companion.SERVICE_PATH_PREFIX
import kotlinx.coroutines.tasks.await

/**
 * Default implementation for [TokenRepository].
 */
@ExperimentalHorologistAuthDataPhoneApi
public class TokenRepositoryImpl(private val dataClient: DataClient) : TokenRepository {

    @SuppressLint("VisibleForTests") // https://issuetracker.google.com/issues/239451111
    override suspend fun updateToken(token: String) {
        val putDataReq: PutDataRequest = PutDataMapRequest.create(SERVICE_PATH_PREFIX).apply {
            dataMap.putString(KEY_TOKEN, token)
        }.asPutDataRequest()

        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)

        putDataTask.await()

        if (putDataTask.isSuccessful) {
            Log.d(TAG, "DataItem updated successfully")
        } else {
            putDataTask.exception?.let { exception ->
                Log.e(TAG, "DataItem update failed with: ${exception.message}", exception)
            } ?: run {
                Log.e(
                    TAG,
                    "DataItem update failed. [isCancelled: ${putDataTask.isCanceled}] [isComplete: ${putDataTask.isComplete}]"
                )
            }
        }
    }
}
