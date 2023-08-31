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

package com.google.android.horologist.mediasample.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.data.googlesignin.GoogleSignInEventListener
import com.google.android.horologist.mediasample.data.auth.GoogleSignInAuthUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun googleSignIn(
        @ApplicationContext application: Context,
    ): GoogleSignInClient = GoogleSignIn.getClient(
        application,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestProfile().build(),
    )

    @Singleton
    @Provides
    fun googleSignInAuthUserRepository(
        @ApplicationContext application: Context,
        googleSignInClient: GoogleSignInClient,
    ): GoogleSignInAuthUserRepository = GoogleSignInAuthUserRepository(
        application,
        googleSignInClient,
    )

    @Singleton
    @Provides
    fun authUserRepository(
        googleSignInAuthUserRepository: GoogleSignInAuthUserRepository,
    ): AuthUserRepository = googleSignInAuthUserRepository

    @Singleton
    @Provides
    fun googleSignInEventListener(
        statefulAuthUserRepository: GoogleSignInAuthUserRepository,
    ): GoogleSignInEventListener = statefulAuthUserRepository
}
