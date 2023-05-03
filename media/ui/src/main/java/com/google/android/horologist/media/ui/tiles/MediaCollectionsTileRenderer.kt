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

package com.google.android.horologist.media.ui.tiles

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.protolayout.DimensionBuilders.WrappedDimensionProp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.Spacer
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.material.Chip
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

/**
 * A tile template showing two media collections and a primary chip that links to playlists.
 */
@ExperimentalHorologistApi
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
        DimensionBuilders.DpProp.Builder(size).build()
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
        for ((image, imageResource) in resourceState.images) {
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
