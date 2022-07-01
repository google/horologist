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

import android.app.Activity
import com.google.android.horologist.mediasample.components.MediaActivity
import com.google.android.horologist.mediasample.components.MediaApplication

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class MediaActivityContainer(
    internal val mediaApplicationContainer: MediaApplicationContainer,
) : AutoCloseable {
    override fun close() {
    }

    fun inject(activity: MediaActivity) {
        activity.mediaActivityContainer = this
        activity.viewModelModule = mediaApplicationContainer.viewModelModule
    }

    companion object {
        private val Activity.container: MediaApplicationContainer
            get() = (application as MediaApplication).container

        fun inject(mediaActivity: MediaActivity) {
            val activityContainer =
                mediaActivity.container.activityContainer(mediaActivity)

            activityContainer.inject(mediaActivity)
        }
    }
}
