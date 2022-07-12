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

package com.google.android.horologist.tiles.complication

import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService

/**
 * A complication service based on a [ComplicationTemplate].
 * The implementation is effectively two parts, first creating some simple data model
 * using a suspending [data] function. Then a render phase.
 */
public abstract class DataComplicationService<D, R : ComplicationTemplate<D>> :
    SuspendingComplicationDataSourceService() {
    public abstract val renderer: R

    public abstract suspend fun data(request: ComplicationRequest): D

    public abstract fun previewData(type: ComplicationType): D

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val data: D = data(request)
        return render(request.complicationType, data)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        val data: D = previewData(type)
        return render(type, data)
    }

    public fun render(type: ComplicationType, data: D): ComplicationData {
        return renderer.render(type, data)
    }
}
