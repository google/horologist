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

package com.google.android.horologist.datalayer.sample.screens.nodes

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.android.horologist.data.proto.SampleProto.Data
import com.google.android.horologist.data.proto.data
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SampleDataSerializer : Serializer<Data> {
    override val defaultValue: Data = data {
        this.value = 0
        this.name = ""
    }

    override suspend fun readFrom(input: InputStream): Data {
        try {
            return Data.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Data, output: OutputStream) = t.writeTo(output)
}
