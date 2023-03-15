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

import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityClient.FILTER_ALL
import com.google.android.gms.wearable.Wearable
import com.google.common.truth.Truth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CapabilitiesTest {
    @Test
    fun wearCapabilitiesArePresent() = runTest {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

//        Truth.assertThat(values).containsExactly("horologist_phone", "data_layer_app_helper_device_phone")

        val thisId = Wearable.getNodeClient(targetContext).localNode.await()

        val capabilities = Wearable.getCapabilityClient(targetContext)
            .getAllCapabilities(FILTER_ALL).await()

        Truth.assertThat(capabilities["horologist_phone"]?.nodes?.find { it.id == thisId.id }).isNotNull()
        Truth.assertThat(capabilities["data_layer_app_helper_device_phone"]?.nodes?.find { it.id == thisId.id }).isNotNull()
    }
}
