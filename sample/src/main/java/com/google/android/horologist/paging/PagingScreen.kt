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

package com.google.android.horologist.paging

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.compose.paging.items
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

@Composable
fun PagingScreen() {
    val myBackend = remember { MyBackend() }

    val pager = remember {
        Pager(
            PagingConfig(
                pageSize = myBackend.DataBatchSize,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { myBackend.getAllData() }
    }

    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

    ScalingLazyColumn(state = rememberScalingLazyListState(initialCenterItemIndex = 0), autoCentering = AutoCenteringParams(itemIndex = 0)) {
        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
            item {
                Text(
                    text = "Waiting for items to load from the backend",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        items(lazyPagingItems) { item ->
            TitleCard(onClick = { /*TODO*/ }, title = {
                Text("$item")
            }) {
                Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit...")
            }
        }

        if (lazyPagingItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

class MyBackend {
    private val backendDataList = (0..60).toList()
    val DataBatchSize = 5

    class DesiredLoadResultPageResponse(
        val data: List<String>
    )

    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)

    /**
     * Returns [DataBatchSize] items for a key
     */
    fun searchItemsByKey(key: Int): DesiredLoadResultPageResponse {
        val maxKey = ceil(backendDataList.size.toFloat() / DataBatchSize).toInt()

        if (key >= maxKey) {
            return DesiredLoadResultPageResponse(emptyList())
        }

        val from = key * DataBatchSize
        val to = minOf((key + 1) * DataBatchSize, backendDataList.size)
        val currentSublist = backendDataList.subList(from, to).map {
            "Item $it fetched at ${LocalTime.now().format(timeFormatter)}"
        }

        return DesiredLoadResultPageResponse(currentSublist)
    }

    fun getAllData(): PagingSource<Int, String> {
        // Example from https://developer.android.com/reference/kotlin/androidx/paging/PagingSource
        return object : PagingSource<Int, String>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
                // Simulate latency
                delay(1.seconds)

                val pageNumber = params.key ?: 0

                val response = searchItemsByKey(pageNumber)

                // Since 0 is the lowest page number, return null to signify no more pages should
                // be loaded before it.
                val prevKey = if (pageNumber > 0) pageNumber - 1 else null

                // This API defines that it's out of data when a page returns empty. When out of
                // data, we return `null` to signify no more pages should be loaded
                val nextKey = if (response.data.isNotEmpty()) pageNumber + 1 else null

                return LoadResult.Page(
                    data = response.data,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            override fun getRefreshKey(state: PagingState<Int, String>): Int? {
                return (
                    (
                        state.anchorPosition
                            ?: 0
                        ) - state.config.initialLoadSize / 2
                    ).coerceAtLeast(0)
            }
        }
    }
}
