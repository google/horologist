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

package com.google.android.horologist.mediasample.di

import android.app.Service
import com.google.android.horologist.media3.complication.MediaComplicationService
import com.google.android.horologist.mediasample.components.MediaApplication

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class ServiceContainer(
    private val mediaApplicationContainer: MediaApplicationContainer,
    private val service: Service,
) : AutoCloseable {
    override fun close() {
    }

    fun inject(service: MediaComplicationService<*>) {
        service.intentBuilder = mediaApplicationContainer.intentBuilder
    }

    companion object {
        internal val MediaComplicationService<*>.container: ServiceContainer
            get() = (application as MediaApplication).container.serviceContainer(this)

        fun inject(complicationService: MediaComplicationService<*>) {
            val serviceContainer = complicationService.container

            serviceContainer.inject(complicationService)
        }
    }
}
