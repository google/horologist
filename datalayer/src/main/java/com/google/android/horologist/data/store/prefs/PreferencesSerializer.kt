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

package com.google.android.horologist.data.store.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.PreferencesProto.PreferenceMap
import androidx.datastore.preferences.PreferencesProto.StringSet
import androidx.datastore.preferences.PreferencesProto.Value
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Proto based serializer for Preferences.
 *
 * TODO check if we can open up and reuse https://b.corp.google.com/issues/241749603
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:datastore/datastore-preferences-core/src/jvmMain/kotlin/androidx/datastore/preferences/core/PreferencesSerializer.kt;l=34?q=PreferencesSerializer&sq=
 */
internal object PreferencesSerializer : Serializer<Preferences> {
    override val defaultValue: Preferences
        get() {
            return emptyPreferences()
        }

    @Throws(IOException::class, CorruptionException::class)
    override suspend fun readFrom(input: InputStream): Preferences {
        val preferencesProto = try {
            PreferenceMap.parseFrom(input)
        } catch (ipbe: InvalidProtocolBufferException) {
            throw CorruptionException("Unable to parse preferences proto.", ipbe)
        }

        val mutablePreferences = mutablePreferencesOf()

        for ((name, value) in preferencesProto.preferencesMap) {
            addProtoEntryToPreferences(name, value, mutablePreferences)
        }

        return mutablePreferences.toPreferences()
    }

    @Throws(IOException::class, CorruptionException::class)
    override suspend fun writeTo(t: Preferences, output: OutputStream) {
        val preferences = t.asMap()
        val protoBuilder = PreferenceMap.newBuilder()

        for ((key, value) in preferences) {
            protoBuilder.putPreferences(key.name, getValueProto(value))
        }

        protoBuilder.build().writeTo(output)
    }

    private fun getValueProto(value: Any): Value {
        @Suppress("UNCHECKED_CAST")
        return when (value) {
            is Boolean -> Value.newBuilder().setBoolean(value).build()
            is Float -> Value.newBuilder().setFloat(value).build()
            is Double -> Value.newBuilder().setDouble(value).build()
            is Int -> Value.newBuilder().setInteger(value).build()
            is Long -> Value.newBuilder().setLong(value).build()
            is String -> Value.newBuilder().setString(value).build()
            is Set<*> -> Value.newBuilder().setStringSet(
                StringSet.newBuilder().addAllStrings(value as Set<String>)
            ).build()

            else -> throw IllegalStateException(
                "PreferencesSerializer does not support type: ${value.javaClass.name}"
            )
        }
    }

    private fun addProtoEntryToPreferences(
        name: String,
        value: Value,
        mutablePreferences: MutablePreferences
    ) {
        return when (value.valueCase) {
            Value.ValueCase.BOOLEAN ->
                mutablePreferences[booleanPreferencesKey(name)] =
                    value.boolean

            Value.ValueCase.FLOAT -> mutablePreferences[floatPreferencesKey(name)] = value.float
            Value.ValueCase.DOUBLE -> mutablePreferences[doublePreferencesKey(name)] = value.double
            Value.ValueCase.INTEGER -> mutablePreferences[intPreferencesKey(name)] = value.integer
            Value.ValueCase.LONG -> mutablePreferences[longPreferencesKey(name)] = value.long
            Value.ValueCase.STRING -> mutablePreferences[stringPreferencesKey(name)] = value.string
            Value.ValueCase.STRING_SET ->
                mutablePreferences[stringSetPreferencesKey(name)] =
                    value.stringSet.stringsList.toSet()

            Value.ValueCase.VALUE_NOT_SET ->
                throw CorruptionException("Value not set.")

            null -> throw CorruptionException("Value case is null.")
            else -> throw CorruptionException("Value case is not supported.")
        }
    }
}
