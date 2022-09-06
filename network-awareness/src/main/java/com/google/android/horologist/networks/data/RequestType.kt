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

package com.google.android.horologist.networks.data

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi

/**
 * An open domain model for RequestTypes in a typical Wear application.
 * Allows decisions such as suitable networks to be made based on the traffic.
 */
@ExperimentalHorologistNetworksApi
public interface RequestType {
    @ExperimentalHorologistNetworksApi
    public object ImageRequest : RequestType {
        override fun toString(): String {
            return "image"
        }
    }

    @ExperimentalHorologistNetworksApi
    public data class MediaRequest(public val type: MediaRequestType) : RequestType {
        public val name: String = "media-${type.toString().lowercase()}"
        public enum class MediaRequestType {
            Stream, Download, Live
        }
        override fun toString(): String = name

        public companion object {
            public val DownloadRequest: MediaRequest = MediaRequest(MediaRequestType.Download)
            public val StreamRequest: MediaRequest = MediaRequest(MediaRequestType.Stream)
        }
    }

    @ExperimentalHorologistNetworksApi
    public object ApiRequest : RequestType {
        override fun toString(): String {
            return "api"
        }
    }

    @ExperimentalHorologistNetworksApi
    public object LogsRequest : RequestType {
        override fun toString(): String {
            return "logs"
        }
    }

    @ExperimentalHorologistNetworksApi
    public object UnknownRequest : RequestType {
        override fun toString(): String {
            return "unknown"
        }
    }
}
