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

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.android.horologist.data.SurfacesInfo
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

public object SurfaceInfoSerializer : Serializer<SurfacesInfo> {
    override val defaultValue: SurfacesInfo = SurfacesInfo.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SurfacesInfo =
        try {
            SurfacesInfo.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: SurfacesInfo, output: OutputStream) {
        t.writeTo(output)
    }
}
