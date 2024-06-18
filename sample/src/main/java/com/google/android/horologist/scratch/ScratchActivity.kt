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

package com.google.android.horologist.scratch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.toRoute
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.PlaceholderState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.placeholder
import androidx.wear.compose.material.placeholderShimmer
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberActivePlaceholderState
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.compose.material.placeholderIf
import com.google.android.horologist.compose.material.placeholderShimmerIf
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.nav.SwipeDismissableNavHost
import com.google.android.horologist.compose.nav.composable
import com.google.android.horologist.images.base.paintable.Conversions.PlaceholderPaintable
import com.google.android.horologist.images.base.paintable.Conversions.orPlaceholder
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.paintable.Paintable
import com.google.android.horologist.sample.R
import com.google.android.horologist.scratch.Nav.ItemDetail
import com.google.android.horologist.scratch.Nav.ItemList
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds
import androidx.wear.compose.material.Chip as MaterialChip

class ScratchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val navController = rememberSwipeDismissableNavController()
    AppScaffold {
        SwipeDismissableNavHost(navController = navController, ItemList) {
            composable<ItemList> {
                val route = it.toRoute<ItemList>()

                ItemListScreen(
                    route,
                    onClick = { navController.navigate(Nav.ItemDetail(id = it.id)) })
            }

            composable<ItemDetail> {
                val route = it.toRoute<ItemDetail>()

                ItemDetailScreen(route)
            }
        }
    }
}

val items: List<Item> = List(10) {
    when (it) {
        1 -> Item(it, "Item $it", secondaryLabel = "Secondary", icon = null)
        2 -> Item(it, "Item $it", secondaryLabel = null, icon = null)
        else -> Item(
            it,
            "Item $it",
            secondaryLabel = "Secondary",
            icon = DrawableResPaintable(R.drawable.ic_person)
        )
    }
}

@Composable
fun ItemListScreen(route: ItemList, onClick: (Item) -> Unit) {
    var screenState by remember { mutableStateOf<ItemListScreenState>(ItemListScreenState.Loading) }

    LaunchedEffect(Unit) {
        delay(3.seconds)
        screenState = ItemListScreenState.Loaded(items)
    }

    ItemListScreen(screenState = screenState, onClick)
}

@Composable
fun ItemListScreen(screenState: ItemListScreenState, onClick: (Item) -> Unit) {
    val placeholderState = rememberActivePlaceholderState {
        screenState !is ItemListScreenState.Loading
    }

    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Chip
        )
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text("Items")
                }
            }

            val listItems = when (screenState) {
                is ItemListScreenState.Loaded -> screenState.items
                is ItemListScreenState.Loading -> List(5) { null }
            }

            itemsIndexed(listItems) { idx, it ->
                if (!placeholderState.isShowContent && idx == 0) {
                    // Code to use Horologist PlaceHolderChip
                    PlaceholderChip()
                } else if (idx % 2 == 0) {
                    HorologistMaterialChip(it, onClick, placeholderState)
                } else {
                    WearComposePlaceholderChip(it, onClick, placeholderState)
                }
            }
        }
    }
}

// Code to use Horologist Material Chip directly with Placeholder support
@Composable
private fun HorologistMaterialChip(
    it: Item?,
    onClick: (Item) -> Unit,
    placeholderState: PlaceholderState
) {
    Chip(
        label = it?.name.orEmpty(),
        secondaryLabel = if (it != null) it.secondaryLabel else "",
        icon = if (it != null) it.icon else PlaceholderPaintable,
        onClick = {
            if (it != null) {
                onClick(it)
            }
        },
        enabled = it != null,
        placeholderState = placeholderState
    )
}

// Code to use Wear Compose Chip directly with Placeholder support
@Composable
private fun WearComposePlaceholderChip(
    item: Item?,
    onClick: (Item) -> Unit,
    placeholderState: PlaceholderState
) {
    MaterialChip(
        modifier = Modifier
            .fillMaxWidth()
            .placeholderShimmer(placeholderState),
        label = {
            Text(
                text = if (placeholderState.isShowContent) item?.name.orEmpty() else "",
                modifier = Modifier
                    .run { if (placeholderState.isShowContent) this else padding(end = 10.dp) }
                    .fillMaxWidth()
                    .placeholder(placeholderState = placeholderState),
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        onClick = {
            if (item != null) {
                onClick(item)
            }
        },
        secondaryLabel = {
            Text(
                text = if (placeholderState.isShowContent) item?.secondaryLabel.orEmpty() else "",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .run {
                        if (placeholderState.isShowContent) this else padding(
                            end = 30.dp
                        )
                    }
                    .fillMaxWidth()
                    .placeholder(placeholderState = placeholderState),
            )
        },
        icon = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(ChipDefaults.IconSize)
                    .placeholder(
                        placeholderState
                    )
            ) {
                item?.icon?.let { paintable: Paintable ->
                    Image(
                        paintable.rememberPainter(),
                        contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                    )
                }
            }
        },
        enabled = item != null,
    )
}

@Composable
fun ItemDetailScreen(route: ItemDetail) {
    var screenState by remember { mutableStateOf<ItemDetailScreenState>(ItemDetailScreenState.Loading) }

    LaunchedEffect(Unit) {
        delay(1.seconds)
        val item = items.find { it.id < 8 && it.id == route.id }
        screenState = if (item != null) {
            ItemDetailScreenState.Loaded(item)
        } else {
            ItemDetailScreenState.Failed
        }
    }

    ItemDetailScreen(screenState = screenState)
}

@Composable
fun ItemDetailScreen(screenState: ItemDetailScreenState) {
    val placeholderState = rememberActivePlaceholderState {
        screenState !is ItemDetailScreenState.Loading
    }

    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Text
        )
    )

    val item = when (screenState) {
        is ItemDetailScreenState.Loaded -> screenState.item
        else -> null
    }

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(
                    contentPadding = firstItemPadding(),
                    modifier = Modifier.placeholderShimmer(placeholderState, shape = RectangleShape)
                ) {
                    Text(
                        item?.name.orEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .placeholder(placeholderState, shape = RectangleShape)
                    )
                }
            }

            item {
                Text("id: ${item?.id}", modifier = Modifier.placeholder(placeholderState))
            }
        }
    }
}

sealed interface ItemListScreenState {
    data object Loading : ItemListScreenState
    data class Loaded(val items: List<Item>) : ItemListScreenState
}

sealed interface ItemDetailScreenState {
    data object Loading : ItemDetailScreenState
    data class Loaded(val item: Item) : ItemDetailScreenState
    data object Failed : ItemDetailScreenState
}

data class Item(
    val id: Int,
    val name: String,
    val secondaryLabel: String? = null,
    val icon: Paintable?,
)

sealed interface Nav {
    @Serializable
    data object ItemList

    @Serializable
    data class ItemDetail(val id: Int)
}