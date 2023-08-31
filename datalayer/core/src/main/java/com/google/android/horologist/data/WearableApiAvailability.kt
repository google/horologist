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

package com.google.android.horologist.data

import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.AvailabilityException
import com.google.android.gms.common.api.GoogleApi
import kotlinx.coroutines.tasks.await

/**
 * Checks whether a given Wearable Data Layer API is available on this device.
 */
object WearableApiAvailability {
    suspend fun isAvailable(api: GoogleApi<*>): Boolean {
        return try {
            GoogleApiAvailability.getInstance()
                .checkApiAvailability(api)
                .await()

            true
        } catch (e: AvailabilityException) {
            Log.d(
                TAG,
                "${api.javaClass.simpleName} API is not available in this device.",
            )
            false
        }
    }

    val TAG = "WearableApiAvailability"
}
