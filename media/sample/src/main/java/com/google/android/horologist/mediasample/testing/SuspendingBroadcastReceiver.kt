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

@file:OptIn(DelicateCoroutinesApi::class)

package com.google.android.horologist.mediasample.testing

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

abstract class SuspendingBroadcastReceiver : BroadcastReceiver() {
    abstract val action: String
    open val coroutineScope: CoroutineScope = GlobalScope

    override fun onReceive(context: Context, intent: Intent) {
        println("Received $intent")

        if (intent.action != action)
            return

        val intentExtras = intent.extras

        val pendingResult = goAsync()
        coroutineScope.launch {
            try {
                val result = withTimeout(9.seconds) {
                    execute(context, intentExtras ?: Bundle.EMPTY)
                }
                pendingResult.setResult(result.code, result.data, result.extras)
            } catch (e: TimeoutCancellationException) {
                pendingResult.setResult(Activity.RESULT_CANCELED, e.toString(), Bundle.EMPTY)
            } catch (e: Exception) {
                pendingResult.setResult(Activity.RESULT_CANCELED, e.toString(), Bundle.EMPTY)
            } finally {
                pendingResult.finish()
            }
        }
    }

    abstract suspend fun execute(context: Context, intentExtras: Bundle): BroadcastResult
}

data class BroadcastResult(
    val data: String,
    val code: Int = Activity.RESULT_OK,
    val extras: Bundle = Bundle.EMPTY
)
