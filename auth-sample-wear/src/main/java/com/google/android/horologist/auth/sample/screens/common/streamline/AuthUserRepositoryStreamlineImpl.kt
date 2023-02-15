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

package com.google.android.horologist.auth.sample.screens.common.streamline

import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import kotlinx.coroutines.delay
import kotlin.random.Random

object AuthUserRepositoryStreamlineImpl : AuthUserRepository {

    var mode: Mode = Mode.NO_ACCOUNTS_AVAILABLE

    override suspend fun getAuthenticated(): AuthUser? = null

    override suspend fun getAvailable(): List<AuthUser> {
        // simulate latency in fetching the accounts
        delay(Random.nextLong(500, 1000))

        return when (mode) {
            Mode.NO_ACCOUNTS_AVAILABLE -> emptyList()
            Mode.SINGLE_ACCOUNT_AVAILABLE -> {
                listOf(
                    AuthUser(
                        displayName = "Maggie",
                        email = "maggie@example.com"
                    )
                )
            }

            Mode.MULTIPLE_ACCOUNTS_AVAILABLE -> {
                listOf(
                    AuthUser(
                        displayName = "Maggie",
                        email = "maggie@example.com"
                    ),
                    AuthUser(
                        displayName = "John",
                        email = "john@example.com"
                    )
                )
            }
        }
    }

    enum class Mode {
        NO_ACCOUNTS_AVAILABLE,
        SINGLE_ACCOUNT_AVAILABLE,
        MULTIPLE_ACCOUNTS_AVAILABLE,
    }
}
