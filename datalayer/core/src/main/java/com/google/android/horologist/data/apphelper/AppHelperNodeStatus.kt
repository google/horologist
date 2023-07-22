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

package com.google.android.horologist.data.apphelper

import com.google.android.horologist.data.SurfacesInfo

/**
 * Represents a node on the network, and the installation status of the app. The node can be a
 * watch or different device like a phone.
 */
public data class AppHelperNodeStatus(
    val id: String,
    val displayName: String,
    val isAppInstalled: Boolean,
    val nodeType: AppHelperNodeType,
    val surfacesInfo: SurfacesInfo = SurfacesInfo.getDefaultInstance()
)

public enum class AppHelperNodeType {
    UNKNOWN,
    WATCH,
    PHONE
}
