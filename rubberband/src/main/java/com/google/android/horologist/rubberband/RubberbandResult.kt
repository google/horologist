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

package com.google.android.horologist.rubberband

public enum class RubberbandResult {
    SUCCESS,
    ERROR_UNKNOWN_REQUEST,
    ERROR_ACTIVITY_NOT_FOUND,
    ERROR_INVALID_COMPONENT,
    ERROR_NO_COMPANION_FOUND;

    public val byteArray: ByteArray = byteArrayOf(this.ordinal.toByte())
}

public fun rubberBandResultFromByteArray(byteArray: ByteArray): RubberbandResult {
    val index = byteArray[0].toInt()
    return RubberbandResult.values()[index]
}
