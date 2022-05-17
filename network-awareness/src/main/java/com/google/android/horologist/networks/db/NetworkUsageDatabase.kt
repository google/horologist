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

package com.google.android.horologist.networks.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Instant

@Database(
    entities = [DataUsage::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NetworkUsageDatabase.Converters::class)
public abstract class NetworkUsageDatabase : RoomDatabase() {
    public abstract fun networkUsageDao(): NetworkUsageDao

    public class Converters {
        @TypeConverter
        public fun fromTimestamp(value: Long?): Instant? {
            return value?.let { Instant.ofEpochMilli(it) }
        }

        @TypeConverter
        public fun dateToTimestamp(date: Instant?): Long? {
            return date?.toEpochMilli()
        }
    }

    public companion object {
        private lateinit var INSTANCE: NetworkUsageDatabase

        public fun getDatabase(context: Context, multiprocess: Boolean = false): NetworkUsageDatabase {
            return synchronized(this) {
                if (!Companion::INSTANCE.isInitialized) {
                    val instance = Room.databaseBuilder(
                        context,
                        NetworkUsageDatabase::class.java,
                        "networkUsage"
                    )
                        // TODO support migrations
                        .fallbackToDestructiveMigration()
                        .apply {
                            if (multiprocess) {
                                // enabled to support Flow updates between processes
                                enableMultiInstanceInvalidation()
                            }
                        }
                        // enabled to support Flow updates between processes
                        .enableMultiInstanceInvalidation()
                        .build()
                    INSTANCE = instance
                }

                INSTANCE
            }
        }
    }
}
