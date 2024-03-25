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

package com.google.android.horologist.datalayer.watch

import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WearDataLayerAppHelperTest {

    @Test
    fun testCreate() = runTest {
        val scope = CoroutineScope(coroutineContext + Job())

        val context = InstrumentationRegistry.getInstrumentation().context
        val registry = WearDataLayerRegistry.fromContext(context, scope)
        val helper = WearDataLayerAppHelper(context, registry)

        assertThat(helper.isAvailable()).isFalse()

        scope.cancel()
    }
}
