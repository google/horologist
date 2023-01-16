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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.PlaceholderDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.placeholder
import androidx.wear.compose.material.placeholderShimmer
import androidx.wear.compose.material.rememberPlaceholderState
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.paging.items
import com.google.android.horologist.compose.tools.WearSquareDevicePreview
import com.google.android.horologist.sample.R
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

@Composable
fun PagingScreen(
    navController: NavController,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier
) {
    val myBackend = remember { MyBackend() }

    val pager: Pager<Int, PagingItem> = remember {
        Pager(
            PagingConfig(
                pageSize = myBackend.dataBatchSize,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { myBackend.getAllData() }
    }

    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier.fillMaxSize()
    ) {
        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
            items(10) {
                PagingItemCard(item = null)
            }
        } else {
            items(lazyPagingItems) { item ->
                PagingItemCard(item) {
                    if (item != null) {
                        navController.navigate("pagingItem?id=${item.item}")
                    }
                }
            }
        }
    }
}

@Composable
private fun PagingItemCard(
    item: PagingItem?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Workaround for https://issuetracker.google.com/issues/260343754
    val chipPlaceholderState =
        if (item != null) rememberPlaceholderState { true } else rememberPlaceholderState { false }

    TitleCard(
        modifier = modifier
            .fillMaxWidth()
            .placeholderShimmer(chipPlaceholderState),
        enabled = item != null,
        onClick = onClick,
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(if (chipPlaceholderState.isShowContent) 1f else 0.5f)
                    .placeholder(chipPlaceholderState),
                text = item?.toString().orEmpty()
            )
        },
        backgroundPainter = PlaceholderDefaults.painterWithPlaceholderOverlayBackgroundBrush(
            placeholderState = chipPlaceholderState,
            painter = CardDefaults.cardBackgroundPainter()
        )
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(chipPlaceholderState),
            text = if (item != null) {
                stringResource(R.string.lorem_ipsum)
            } else {
                "\n\n"
            },
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (!chipPlaceholderState.isShowContent) {
        LaunchedEffect(chipPlaceholderState) {
            chipPlaceholderState.startPlaceholderAnimation()
        }
    }
}

private class MyBackend {
    private val backendDataList = (0..65).toList()
    val dataBatchSize = 5

    class DesiredLoadResultPageResponse(
        val data: List<PagingItem>,
        val itemsAfter: Int = PagingSource.LoadResult.Page.COUNT_UNDEFINED
    )

    /**
     * Returns [dataBatchSize] items for a key
     */
    fun searchItemsByKey(key: Int): DesiredLoadResultPageResponse {
        val maxKey = ceil(backendDataList.size.toFloat() / dataBatchSize).toInt()

        if (key >= maxKey) {
            return DesiredLoadResultPageResponse(data = emptyList(), itemsAfter = 0)
        }

        val from = key * dataBatchSize
        val to = minOf((key + 1) * dataBatchSize, backendDataList.size)
        val currentSublist = backendDataList.subList(from, to).map {
            PagingItem(it)
        }

        val itemsAfter = backendDataList.size - to - 1
        return DesiredLoadResultPageResponse(data = currentSublist, itemsAfter = itemsAfter)
    }

    fun getAllData(): PagingSource<Int, PagingItem> {
        // Example from https://developer.android.com/reference/kotlin/androidx/paging/PagingSource
        return object : PagingSource<Int, PagingItem>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PagingItem> {
                // Simulate latency
                delay(1.5.seconds)

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
                    nextKey = nextKey,
                    itemsAfter = response.itemsAfter
                )
            }

            override fun getRefreshKey(state: PagingState<Int, PagingItem>): Int {
                return (
                    (state.anchorPosition ?: 0) -
                        state.config.initialLoadSize / 2.coerceAtLeast(0)
                    )
            }
        }
    }
}

data class PagingItem(
    val item: Int,
    val loadedAt: LocalTime = LocalTime.now()
) {
    override fun toString(): String {
        return "$item (${loadedAt.format(timeFormatter)})"
    }

    companion object {
        private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss")
    }
}

@WearSquareDevicePreview
@Composable
fun PagingItemCardPreviewWithDelayedContent() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        var item by remember { mutableStateOf<PagingItem?>(null) }
        LaunchedEffect(Unit) {
            delay(1000)
            item = PagingItem(10)
        }
        PagingItemCard(modifier = Modifier.fillMaxWidth(), item = item)
    }
}

@WearSquareDevicePreview
@Composable
fun PagingItemCardPreviewWithInitialContent() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val item = remember { PagingItem(10) }
        PagingItemCard(modifier = Modifier.fillMaxWidth(), item = item)
    }
}
