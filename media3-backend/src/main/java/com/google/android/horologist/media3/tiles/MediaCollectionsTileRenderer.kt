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

@file:OptIn(ExperimentalHorologistComposeToolsApi::class, ExperimentalHorologistTilesApi::class)

package com.google.android.horologist.media3.tiles

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ActionBuilders.LaunchAction
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.LayoutElementBuilders.Image
import androidx.wear.tiles.LayoutElementBuilders.Spacer
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ResourceBuilders.AndroidImageResourceByResId
import androidx.wear.tiles.ResourceBuilders.ImageResource
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.material.Chip
import androidx.wear.tiles.material.ChipColors
import androidx.wear.tiles.material.Colors
import androidx.wear.tiles.material.CompactChip
import androidx.wear.tiles.material.layouts.PrimaryLayout
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.compose.tools.UampColors
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.media3.R
import com.google.android.horologist.media3.tiles.MediaCollectionsTileRenderer.MediaCollectionsState
import com.google.android.horologist.media3.tiles.MediaCollectionsTileRenderer.ResourceState
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.components.NoOpClickable
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import com.google.android.horologist.tiles.theme.toTileColors

class MediaCollectionsTileRenderer(
    context: Context,
    private val materialTheme: Colors,
) :
    SingleTileLayoutRenderer<MediaCollectionsState, ResourceState>(context) {

    override fun createTheme(): Colors = materialTheme

    override fun renderTile(
        state: MediaCollectionsState,
        deviceParameters: DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        val expandedDimensionProp = ExpandedDimensionProp.Builder().build()
        return PrimaryLayout.Builder(deviceParameters)
            .setPrimaryLabelTextContent(
                Image.Builder()
                    .setResourceId("note")
                    .build()
            )
            .setContent(
                Column.Builder()
                    .setWidth(expandedDimensionProp)
                    .setHeight(expandedDimensionProp)
                    .addContent(
                        Image.Builder()
                            .setResourceId("note")
                            .build()
                    )
                    .apply {
                        listOf(state.collection1, state.collection2).forEach {
                            val clickable = Clickable.Builder()
                                .setOnClick(
                                    it.action
                                )
                                .build()
                            addContent(
                                Chip.Builder(context, clickable, deviceParameters)
                                    .setChipColors(ChipColors.secondaryChipColors(theme))
                                    .setPrimaryTextIconContent(it.name, "collection-${it.icon}")
                                    .setWidth(expandedDimensionProp)
                                    .build()
                            )
                            addContent(
                                Spacer.Builder()
                                    .setHeight(
                                        DimensionBuilders.DpProp.Builder()
                                            .setValue(4f)
                                            .build()
                                    )
                                    .build()
                            )
                        }
                    }
                    .build()
            )
            .setPrimaryChipContent(
                CompactChip.Builder(context, "Playlists", NoOpClickable, deviceParameters)
                    .setChipColors(ChipColors.primaryChipColors(theme))
                    .build()
            )
            .build()
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceResults: ResourceState,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        addIdToImageMapping(
            "note",
            ImageResource.Builder()
                .setAndroidResourceByResId(
                    AndroidImageResourceByResId.Builder()
                        .setResourceId(R.drawable.ic_baseline_music_note_24)
                        .build()
                )
                .build()
        )
        resourceResults.images.forEach { i, imageResource ->
            addIdToImageMapping(
                "collection-$i",
                imageResource
            )
        }
    }

    data class MediaCollection(
        val name: String,
        val id: Int,
        @DrawableRes val icon: Int,
        val action: ActionBuilders.Action
    )

    data class MediaCollectionsState(
        val collection1: MediaCollection,
        val collection2: MediaCollection,
    )

    data class ResourceState(
        val images: Map<Int, ImageResource>
    )
}

@OptIn(ExperimentalHorologistComposeToolsApi::class, ExperimentalHorologistTilesApi::class)
@WearPreviewDevices
@Composable
fun SampleTilePreview() {
    val context = LocalContext.current

    val launchAction = LaunchAction.Builder()
        .build()

    val tileState = remember {
        MediaCollectionsState(
            MediaCollectionsTileRenderer.MediaCollection(
                name = "Liked Songs",
                id = 1,
                icon = R.drawable.ic_baseline_queue_music_24,
                action = launchAction
            ),
            MediaCollectionsTileRenderer.MediaCollection(
                name = "Podcasts",
                id = 2,
                icon = R.drawable.ic_baseline_podcasts_24,
                action = launchAction
            ),
        )
    }

    val resourceState = remember {
        ResourceState(
            mapOf(
                1 to drawableResToImageResource(R.drawable.ic_baseline_queue_music_24),
                2 to drawableResToImageResource(R.drawable.ic_baseline_podcasts_24)
            )
        )
    }

    val renderer = remember {
        MediaCollectionsTileRenderer(context, UampColors.toTileColors())
    }

    TileLayoutPreview(
        tileState,
        resourceState,
        renderer
    )
}
