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

package com.google.android.horologist.auth.data.googlesignin

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A listener of events of the Google Sign-In authentication method.
 *
 * @sample com.google.android.horologist.auth.sample.screens.googlesignin.signin.GoogleSignInEventListenerSample
 */
public interface GoogleSignInEventListener {

    /**
     * Called when signed in.
     *
     * @param account account that signed in.
     */
    public suspend fun onSignedIn(account: GoogleSignInAccount): Unit
}

/**
 * A no-op implementation of [GoogleSignInEventListener].
 */
public object GoogleSignInEventListenerNoOpImpl : GoogleSignInEventListener {

    override suspend fun onSignedIn(account: GoogleSignInAccount): Unit = Unit
}
