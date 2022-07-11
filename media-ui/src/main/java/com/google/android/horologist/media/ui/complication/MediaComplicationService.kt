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

package com.google.android.horologist.media.ui.complication

import androidx.wear.watchface.complications.data.ComplicationType
import com.google.android.horologist.tiles.complication.ComplicationRenderer
import com.google.android.horologist.tiles.complication.DataComplicationService

/**
 * A complication provider that support small images. Upon tapping on the app icon,
 * the complication will launch the controls screen.
 */
abstract class MediaComplicationService<D> : DataComplicationService<D, ComplicationRenderer<D>>() {
    override fun previewData(type: ComplicationType): D = renderer.previewData()
}
