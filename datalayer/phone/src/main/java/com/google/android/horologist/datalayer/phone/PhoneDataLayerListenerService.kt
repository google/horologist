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

package com.google.android.horologist.datalayer.phone

import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.apphelper.DataLayerAppHelper
import com.google.android.horologist.data.apphelper.DataLayerAppHelperService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

public class PhoneDataLayerListenerService : DataLayerAppHelperService() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    public override val appHelper: DataLayerAppHelper by lazy {
        val registry = WearDataLayerRegistry.fromContext(this, serviceScope)
        PhoneDataLayerAppHelper(this, registry)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
