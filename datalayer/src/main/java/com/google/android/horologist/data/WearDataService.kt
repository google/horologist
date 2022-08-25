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

package com.google.android.horologist.data

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService

/**
 * Base service class for applications wishing to be woken up to receive data layer events.
 *
 * Must include the appropriate registration in the manifest. Such as
 *
 * ```
 * <service
 *   android:name="com.google.android.horologist.datalayer.SampleDataService"
 *   android:exported="true">
 *   <intent-filter>
 *     <action android:name="com.google.android.gms.wearable.DATA_CHANGED" />
 *     <data android:scheme="wear" android:host="*" android:pathPrefix="/proto" />
 *   </intent-filter>
 * </service>
 * ```
 */
abstract class WearDataService : WearableListenerService() {
    abstract val registry: WearDataLayerRegistry

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        registry.onDataChanged(dataEventBuffer.toList())
    }
}
