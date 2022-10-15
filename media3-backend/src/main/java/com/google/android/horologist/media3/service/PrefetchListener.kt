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

package com.google.android.horologist.media3.service

import android.net.Uri
import androidx.media3.common.Player
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class PrefetchListener(
    private val coroutineScope: CoroutineScope,
    private val cache: CacheDataSource.Factory
) : Player.Listener {
    private var prefetchJob: Job? = null
    private var prefetching: MutableList<Uri> = mutableListOf()

    override fun onEvents(player: Player, events: Player.Events) {
        // Cancel all prefetching on any timeline change
        if (events.containsAny(Player.EVENT_TIMELINE_CHANGED)) {
            cancelPrefetch()
        }

        // Wait for loading of initial track and we are playing music
        if (events.containsAny(
                Player.EVENT_IS_LOADING_CHANGED,
                Player.EVENT_IS_PLAYING_CHANGED,
                Player.EVENT_MEDIA_ITEM_TRANSITION,
            )) {
            if (player.isPlaying && !player.isLoading) {
                val position = player.currentMediaItemIndex
                val mediaItems = (position + 1 until player.mediaItemCount).map {
                    player.getMediaItemAt(it).localConfiguration!!.uri
                }.distinct().take(3)

                prefetch(mediaItems)
            }
        }
    }

    private fun cancelPrefetch() {
        prefetchJob?.cancel()
        prefetchJob = null
        prefetching.clear()
    }

    fun prefetch(mediaItems: List<Uri>) {
        if (mediaItems == prefetching) {
            println("Prefetching already")
            return
        }

        cancelPrefetch()

        coroutineScope.launch {
            mediaItems.forEach { uri ->
                val dataSpec = DataSpec(uri)
                val dataSource = cache.createDataSource()
                println("Caching $uri")
                try {
                    dataSource.open(dataSpec)
                    val cacheWriter =
                        CacheWriter(
                            dataSource,
                            dataSpec,
                            null
                        ) { requestLength, bytesCached, newBytesCached ->
                            println("ProgressListener ${dataSpec.uri} $requestLength $bytesCached $newBytesCached")
                        }
                    cacheWriter.cache()
                    println("Cached $uri")
                } catch (ioe: IOException) {
                    println("Cache failed $uri $ioe")
                } finally {
                    dataSource.close()
                }
            }
        }
    }
}