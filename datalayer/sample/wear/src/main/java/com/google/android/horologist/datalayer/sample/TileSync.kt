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
import androidx.concurrent.futures.await
import androidx.wear.tiles.TileService
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

class TileSync(
    val wearAppHelper: WearDataLayerAppHelper,
) {
    val executor = Dispatchers.Default.asExecutor()

    suspend fun trackInstalledTiles(context: Context) {
        val myTilesList = listOf(
            SampleTileService::class.java.name,
        )

        val activeTiles = TileService.getActiveTilesAsync(context, executor).await()

        for (tileName in myTilesList) {
            if (activeTiles.any { it.componentName.className == tileName }) {
                wearAppHelper.markTileAsInstalled(tileName)
            } else {
                wearAppHelper.markTileAsRemoved(tileName)
            }
        }
    }
}
