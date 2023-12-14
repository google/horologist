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

package com.google.android.horologist.datalayer.sample.di

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.sample.MainActivity
import com.google.android.horologist.datalayer.sample.screens.counter.CounterScreenViewModel
import com.google.android.horologist.datalayer.sample.screens.listnodes.ListNodesViewModel
import com.google.android.horologist.datalayer.sample.shared.CounterValueSerializer
import kotlinx.coroutines.CoroutineScope

object SampleAppDI {

    fun inject(mainActivity: MainActivity) {
        mainActivity.listNodesViewModel = listNodesViewModel(
            applicationContext = mainActivity.applicationContext,
            coroutineScope = mainActivity.lifecycleScope,
        )

        mainActivity.counterScreenViewModel = counterScreenViewModel(
            applicationContext = mainActivity.applicationContext,
            coroutineScope = mainActivity.lifecycleScope,
        )
    }

    private fun listNodesViewModel(
        applicationContext: Context,
        coroutineScope: CoroutineScope,
    ): ListNodesViewModel = ListNodesViewModel(
        phoneDataLayerAppHelper = phoneDataLayerAppHelper(
            applicationContext = applicationContext,
            coroutineScope = coroutineScope,
        ),
    )

    private fun phoneDataLayerAppHelper(
        applicationContext: Context,
        coroutineScope: CoroutineScope,
    ): PhoneDataLayerAppHelper = PhoneDataLayerAppHelper(
        context = applicationContext,
        registry = wearDataLayerRegistry(
            applicationContext = applicationContext,
            coroutineScope = coroutineScope,
        ),
    )

    private fun wearDataLayerRegistry(
        applicationContext: Context,
        coroutineScope: CoroutineScope,
    ): WearDataLayerRegistry = WearDataLayerRegistry.fromContext(
        application = applicationContext,
        coroutineScope = coroutineScope,
    ).apply {
        registerSerializer(CounterValueSerializer)
    }

    private fun counterScreenViewModel(
        applicationContext: Context,
        coroutineScope: CoroutineScope,
    ): CounterScreenViewModel = CounterScreenViewModel(
        phoneDataLayerAppHelper = phoneDataLayerAppHelper(
            applicationContext = applicationContext,
            coroutineScope = coroutineScope,
        ),
        registry = wearDataLayerRegistry(
            applicationContext = applicationContext,
            coroutineScope = coroutineScope,
        ),
    )
}
