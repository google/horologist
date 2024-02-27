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

package com.google.android.horologist.datalayer.sample.di

import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.prompt.installapp.InstallAppPrompt
import com.google.android.horologist.datalayer.phone.ui.prompt.reengage.ReEngagePrompt
import com.google.android.horologist.datalayer.phone.ui.prompt.signin.SignInPrompt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PromptModule {
    @Singleton
    @Provides
    fun installAppPrompt(phoneDataLayerAppHelper: PhoneDataLayerAppHelper): InstallAppPrompt = InstallAppPrompt(
        phoneDataLayerAppHelper = phoneDataLayerAppHelper,
    )

    @Singleton
    @Provides
    fun reEngagePrompt(
        coroutineScope: CoroutineScope,
        phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
    ): ReEngagePrompt = ReEngagePrompt(
        coroutineScope = coroutineScope,
        phoneDataLayerAppHelper = phoneDataLayerAppHelper,
    )

    @Singleton
    @Provides
    fun signInPrompt(
        coroutineScope: CoroutineScope,
        phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
    ): SignInPrompt = SignInPrompt(
        coroutineScope = coroutineScope,
        phoneDataLayerAppHelper = phoneDataLayerAppHelper,
    )
}
