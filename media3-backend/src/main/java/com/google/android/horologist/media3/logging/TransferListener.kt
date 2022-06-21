/*
 * Copyright 2021 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalHorologistMedia3BackendApi::class)

package com.google.android.horologist.media3.logging

import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import androidx.media3.datasource.cache.CacheDataSource
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi

/**
 * Simple implementation of TransferListener and EventListener for networking activity.
 *
 * Default implementation is a noop currently, but can be edited to allow logging when
 * investigating a playback issue.
 */
@ExperimentalHorologistMedia3BackendApi
class TransferListener(
    val appEventLogger: ErrorReporter
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
        appEventLogger.logMessage("bytes transfer $isNetwork $bytesTransferred")
    }

    override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
        appEventLogger.logMessage("end $isNetwork", category = ErrorReporter.Category.Network)
    }
}
