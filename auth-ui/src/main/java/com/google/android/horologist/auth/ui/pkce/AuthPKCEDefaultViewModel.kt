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

package com.google.android.horologist.auth.ui.pkce

import android.app.Application
import com.google.android.horologist.auth.data.pkce.AuthPKCEConfigRepository
import com.google.android.horologist.auth.data.pkce.AuthPKCEOAuthCodeRepository
import com.google.android.horologist.auth.data.pkce.AuthPKCETokenPayloadListener
import com.google.android.horologist.auth.data.pkce.AuthPKCETokenPayloadListenerNoOpImpl
import com.google.android.horologist.auth.data.pkce.AuthPKCETokenRepository
import com.google.android.horologist.auth.data.pkce.impl.AuthPKCEDefaultConfig
import com.google.android.horologist.auth.data.pkce.impl.AuthPKCEOAuthCodeDefaultPayload
import com.google.android.horologist.auth.data.pkce.impl.google.api.TokenResponse
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi

/**
 * Default implementation of [AuthPKCEViewModel], using [AuthPKCEDefaultConfig] as config and
 * [AuthPKCEOAuthCodeDefaultPayload] as payload.
 */
@ExperimentalHorologistAuthUiApi
public abstract class AuthPKCEDefaultViewModel(
    protected val authPKCEConfigRepository: AuthPKCEConfigRepository<AuthPKCEDefaultConfig>,
    protected val authPKCEOAuthCodeRepository: AuthPKCEOAuthCodeRepository<AuthPKCEDefaultConfig, AuthPKCEOAuthCodeDefaultPayload>,
    protected val authPKCETokenRepository: AuthPKCETokenRepository<AuthPKCEDefaultConfig, AuthPKCEOAuthCodeDefaultPayload, TokenResponse>,
    protected val authPKCETokenPayloadListener: AuthPKCETokenPayloadListener<TokenResponse> = AuthPKCETokenPayloadListenerNoOpImpl(),
    application: Application
) : AuthPKCEViewModel<AuthPKCEDefaultConfig, AuthPKCEOAuthCodeDefaultPayload, TokenResponse>(
    authPKCEConfigRepository = authPKCEConfigRepository,
    authPKCEOAuthCodeRepository = authPKCEOAuthCodeRepository,
    authPKCETokenRepository = authPKCETokenRepository,
    authPKCETokenPayloadListener = authPKCETokenPayloadListener,
    application = application
)
