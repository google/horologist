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

package com.google.android.horologist.auth.oauth.devicegrant.signin

import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.data.oauth.devicegrant.AuthDeviceGrantTokenPayloadListener
import com.google.android.horologist.auth.oauth.devicegrant.data.DeviceGrantAuthUserRepositorySample

object DeviceGrantTokenPayloadListenerSample : AuthDeviceGrantTokenPayloadListener<String> {

    override suspend fun onPayloadReceived(payload: String) {
        // For this sample, this function sets a user with no information as authenticated, given
        // the payload received only contains the token.
        DeviceGrantAuthUserRepositorySample.setAuthenticated(AuthUser())
    }
}
