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

@file:OptIn(ExperimentalHorologistMedia3BackendApi::class)

package com.google.android.horologist.media3.service

import android.net.Uri
import androidx.annotation.GuardedBy
import androidx.media3.common.Player
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import com.google.android.horologist.media3.logging.ErrorReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InterruptedIOException

class PlaylistPrefetcher(
    private val coroutineScope: CoroutineScope,
    private val cacheDataSourceFactory: CacheDataSource.Factory,
    private val eventLogger: ErrorReporter
) : Player.Listener {
    @GuardedBy("this")
    private var cached: MutableSet<Uri> =
        mutableSetOf()

    @GuardedBy("this")
    private var prefetching: MutableMap<Uri, CacheWriter> = mutableMapOf()

    override fun onEvents(player: Player, events: Player.Events) {
        // Wait for loading of initial track and we are playing music
        if (events.containsAny(
                Player.EVENT_IS_LOADING_CHANGED,
                Player.EVENT_IS_PLAYING_CHANGED,
                Player.EVENT_MEDIA_ITEM_TRANSITION,
                Player.EVENT_TIMELINE_CHANGED,
            )) {
            val mediaItems = if (player.isPlaying && !player.isLoading) {
                val position = player.currentMediaItemIndex
                (position  until player.mediaItemCount).map {
                    player.getMediaItemAt(it).localConfiguration!!.uri
                }.distinct().take(3)
            } else {
                listOf()
            }

            prefetch(mediaItems)
        }
    }

    fun prefetch(mediaItems: List<Uri>) {
        synchronized(this) {
            val currentlyCaching = prefetching.keys

            currentlyCaching.subtract(mediaItems).forEach {
                prefetching[it]?.cancel()
            }

            val toCache = mediaItems.subtract(cached).take(2).subtract(currentlyCaching)

            toCache.forEach { uri ->
                val dataSpec = DataSpec(uri)
                val dataSource = cacheDataSourceFactory.createDataSource()

                var totalBytesCached = 0L

                val cacheWriter =
                    CacheWriter(
                        dataSource,
                        dataSpec,
                        null
                    ) { requestLength, bytesCached, newBytesCached ->
                        totalBytesCached += newBytesCached
                    }

                prefetching[uri] = cacheWriter

                coroutineScope.launch(Dispatchers.IO) {
                    eventLogger.logMessage(
                        "Caching $uri",
                        category = ErrorReporter.Category.Downloads
                    )

                    try {
                        dataSource.open(dataSpec)
                        cacheWriter.cache()
                        eventLogger.logMessage(
                            "Cached $uri $totalBytesCached",
                            category = ErrorReporter.Category.Downloads
                        )
                        synchronized(this@PlaylistPrefetcher) {
                            cached.add(uri)
                        }
                    } catch (iioe: InterruptedIOException) {
                        eventLogger.logMessage(
                            "Cache cancelled $uri $totalBytesCached",
                            category = ErrorReporter.Category.Downloads
                        )
                    } catch (ioe: IOException) {
                        eventLogger.logMessage(
                            "Cache failed $uri $totalBytesCached $ioe",
                            category = ErrorReporter.Category.Downloads
                        )
                    } finally {
                        dataSource.close()
                    }
                }
            }
        }
    }
}