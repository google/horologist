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

package com.google.android.horologist.spec

import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.tooling.preview.devices.WearDevices

@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Fonts - Small",
    fontScale = 0.94f,
)
@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Fonts - Normal",
    fontScale = 1f,
)
@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Fonts - Medium",
    fontScale = 1.06f,
)
@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Fonts - Large",
    fontScale = 1.12f,
)
@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Fonts - Larger",
    fontScale = 1.18f,
)
@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Fonts - Largest",
    fontScale = 1.24f,
)
@Preview(
    device = WearDevices.LARGE_ROUND,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Large Round",
    showSystemUi = true,
)
@Preview(
    device = WearDevices.SMALL_ROUND,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Small Round",
    showSystemUi = true,
)
public annotation class WearCustomPreviews
