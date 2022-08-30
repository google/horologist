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

@file:OptIn(ExperimentalHorologistTilesApi::class)

package com.google.android.horologist.media.ui.tiles

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.tiles.DimensionBuilders.WrappedDimensionProp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.LayoutElementBuilders.Spacer
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ResourceBuilders.ImageResource
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.material.Chip
import androidx.wear.tiles.material.ChipColors
import androidx.wear.tiles.material.Colors
import androidx.wear.tiles.material.CompactChip
import androidx.wear.tiles.material.layouts.PrimaryLayout
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

/**
 * A tile template showing two media collections and a primary chip that links to playlists.
 */
@ExperimentalHorologistMediaUiApi
public class MediaCollectionsTileRenderer(
    context: Context,
    private val materialTheme: Colors,
    debugResourceMode: Boolean
) : SingleTileLayoutRenderer<MediaCollectionsTileRenderer.MediaCollectionsState, MediaCollectionsTileRenderer.ResourceState>(
    context,
    debugResourceMode
) {
    override fun createTheme(): Colors = materialTheme

    private val expandedDimensionProp = ExpandedDimensionProp.Builder().build()

    private val wrapDimensionProp = WrappedDimensionProp.Builder().build()

    override fun renderTile(
        state: MediaCollectionsState,
        deviceParameters: DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        return PrimaryLayout.Builder(deviceParameters)
            .setContent(
                Column.Builder()
                    .setWidth(expandedDimensionProp)
                    .setHeight(wrapDimensionProp)
                    .addContent(
                        collectionChip(
                            state.collection1,
                            deviceParameters
                        )
                    )
                    .addContent(spacer(4f))
                    .addContent(
                        collectionChip(
                            state.collection2,
                            deviceParameters
                        )
                    ).build()
            ).setPrimaryChipContent(
                CompactChip.Builder(
                    context,
                    context.getString(state.chipName),
                    Clickable.Builder().setOnClick(
                        state.chipAction
                    ).build(),
                    deviceParameters
                ).setChipColors(ChipColors.primaryChipColors(theme)).build()
            ).build()
    }

    private fun spacer(size: Float) = Spacer.Builder().setHeight(
        DimensionBuilders.DpProp.Builder().setValue(size).build()
    ).build()

    private fun collectionChip(
        collection: MediaCollection,
        deviceParameters: DeviceParameters
    ) = Chip.Builder(
        context,
        Clickable.Builder().setOnClick(collection.action).build(),
        deviceParameters
    )
        .setChipColors(ChipColors.secondaryChipColors(theme))
        .setPrimaryLabelContent(collection.name)
        .setIconContent(collection.artworkId)
        .setWidth(expandedDimensionProp)
        .build()

    override fun Resources.Builder.produceRequestedResources(
        resourceState: ResourceState,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        resourceState.images.forEach { (image, imageResource) ->
            if (imageResource != null) {
                addIdToImageMapping(image, imageResource)
            }
        }
    }

    public data class MediaCollection(
        public val name: String,
        public val artworkId: String,
        public val action: ActionBuilders.Action
    )

    public data class MediaCollectionsState(
        @StringRes public val chipName: Int,
        public val chipAction: ActionBuilders.Action,
        public val collection1: MediaCollection,
        public val collection2: MediaCollection
    )

    public data class ResourceState(
        @DrawableRes public val appIcon: Int,
        public val images: Map<String, ImageResource?>
    )
}
