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

package com.google.android.horologist.health.service

import android.app.Application
import android.content.ComponentName
import android.os.Looper.getMainLooper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.health.service.BinderConnection.Companion.bindService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class BinderConnectionTest {
    private lateinit var application: Application
    private lateinit var service: TestService

    @Before
    fun setup() {
        service = Robolectric.setupService(TestService::class.java)
        application = ApplicationProvider.getApplicationContext()

        shadowOf(application).setBindServiceCallsOnServiceConnectedDirectly(true)
        shadowOf(application).setComponentNameAndServiceForBindService(
            ComponentName(
                application,
                TestService::class.java
            ),
            service.localBinder
        )
    }

    @Test
    fun testScopeBinding() = runTest {
        assertThat(shadowOf(application).boundServiceConnections).hasSize(0)

        try {
            withContext(Dispatchers.Default) {
                val x = bindService<TestService.LocalBinder, TestService>(application)

                x.runWhenConnected {
                    assertThat(it.getService().doSomething()).isEqualTo("Something")

                    assertThat(shadowOf(application).boundServiceConnections).hasSize(1)
                }

                this.cancel()
            }
        } catch (e: CancellationException) {
            // expected
        }

        shadowOf(getMainLooper()).idle()

        assertThat(shadowOf(application).boundServiceConnections).hasSize(0)
    }
}
