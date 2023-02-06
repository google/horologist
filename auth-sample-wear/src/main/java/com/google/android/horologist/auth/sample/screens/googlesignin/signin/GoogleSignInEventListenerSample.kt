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

package com.google.android.horologist.auth.sample.screens.googlesignin.signin

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.horologist.auth.data.googlesignin.GoogleSignInEventListener

object GoogleSignInEventListenerSample : GoogleSignInEventListener {
    private val TAG = this::class.java.simpleName

    override suspend fun onSignedIn(account: GoogleSignInAccount?) {
        // This class does not do anything and is only here for sample purposes.
        Log.d(TAG, "Account received: ${account?.displayName}")
    }
}
