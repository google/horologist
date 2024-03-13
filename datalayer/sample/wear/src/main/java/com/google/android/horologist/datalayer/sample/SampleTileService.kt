/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.datalayer.sample

import android.content.Context
import androidx.wear.protolayout.ActionBuilders.AndroidActivity
import androidx.wear.protolayout.ActionBuilders.LaunchAction
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.protolayout.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.AndroidImageResourceByResId
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.EventBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SampleTileService : SuspendingTileService() {
    @Inject
    lateinit var tileSync: TileSync

    val renderer = SampleTileRenderer(this)

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile {
        return renderer.renderTimeline(Unit, requestParams)
    }

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources {
        return renderer.produceRequestedResources(Unit, requestParams)
    }

    override fun onTileAddEvent(requestParams: EventBuilders.TileAddEvent) {
        updateTiles()
    }

    override fun onTileRemoveEvent(requestParams: EventBuilders.TileRemoveEvent) {
        updateTiles()
    }

    private fun updateTiles() {
        runBlocking {
            tileSync.trackInstalledTiles(this@SampleTileService)
        }
    }
}

class SampleTileRenderer(context: Context) : SingleTileLayoutRenderer<Unit, Unit>(context) {
    override fun renderTile(
        state: Unit,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
    ): LayoutElementBuilders.LayoutElement {
        val openClickable = Clickable.Builder()
            .setOnClick(
                LaunchAction.Builder()
                    .setAndroidActivity(
                        AndroidActivity.Builder()
                            .setClassName("com.google.android.horologist.datalayer.sample.MainActivity")
                            .setPackageName("com.google.android.horologist.datalayer.sample")
                            .build(),
                    )
                    .build(),
            )
            .build()

        return PrimaryLayout.Builder(deviceParameters)
            .setContent(
                Box.Builder()
                    .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
                    .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
                    .addContent(
                        Text.Builder(context, "This is a sample Tile.")
                            .setTypography(Typography.TYPOGRAPHY_TITLE1)
                            .setColor(argb(theme.primary))
                            .setMaxLines(2)
                            .build(),
                    )
                    .build(),
            )
            .setPrimaryChipContent(
                CompactChip.Builder(context, "Sample", openClickable, deviceParameters)
                    .setIconContent("appIcon")
                    .build(),
            )
            .build()
    }

    override fun ResourceBuilders.Resources.Builder.produceRequestedResources(
        resourceState: Unit,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: List<String>,
    ) {
        this.addIdToImageMapping(
            "appIcon",
            ImageResource.Builder()
                .setAndroidResourceByResId(
                    AndroidImageResourceByResId.Builder()
                        .setResourceId(com.google.android.horologist.tiles.R.drawable.ic_nordic)
                        .build(),
                )
                .build(),
        )
    }
}
