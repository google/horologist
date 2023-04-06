/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.media3.logging

import android.annotation.SuppressLint
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import androidx.media3.datasource.cache.CacheDataSource
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * Simple implementation of TransferListener and EventListener for networking activity.
 *
 * Default implementation is a noop currently, but can be edited to allow logging when
 * investigating a playback issue.
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalHorologistApi
public class TransferListener(
    private val appEventLogger: ErrorReporter
) : CacheDataSource.EventListener,
    TransferListener {
    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
    }

    override fun onCacheIgnored(reason: Int) {
        appEventLogger.logMessage(
            "cache ignored $reason",
            category = ErrorReporter.Category.Network
        )
    }

    override fun onTransferInitializing(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
        appEventLogger.logMessage("init $isNetwork", category = ErrorReporter.Category.Network)
    }

    override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
        appEventLogger.logMessage("start $isNetwork", category = ErrorReporter.Category.Network)
    }

    override fun onBytesTransferred(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean,
        bytesTransferred: Int
    ) {
    }

    override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
        appEventLogger.logMessage("end $isNetwork", category = ErrorReporter.Category.Network)
    }
}
