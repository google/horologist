/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.audio

/**
 * A device capable of playing audio.
 *
 * This interface is intentionally open to allow other targets to be defined in future.
 */
public interface AudioOutput {
    /**
     * A unique audio output id.
     */
    public val id: String

    /**
     * The user meaningful display name for the device.
     */
    public val name: String

    /**
     * Optional type of output which may be associated with an icon or displayed name.
     */
    public val type: String
        get() = ""

    /**
     * No current device.
     */
    public object None : AudioOutput {
        override val name: String = ""
        override val id: String = ""
        override val type: String = TYPE_NONE
    }

    /**
     * A bluetooth headset paired with the watch.
     */
    public data class BluetoothHeadset(
        override val id: String,
        override val name: String
    ) : AudioOutput {
        override val type: String = TYPE_HEADPHONES
    }

    /**
     * The one device watch speaker.
     */
    public data class WatchSpeaker(
        override val id: String,
        override val name: String
    ) : AudioOutput {
        override val type: String = TYPE_WATCH
    }

    /**
     * An unknown audio output device
     */
    public data class Unknown(
        override val id: String,
        override val name: String
    ) : AudioOutput

    public companion object {
        public const val TYPE_WATCH: String = "watch"
        public const val TYPE_HEADPHONES: String = "headphones"
        public const val TYPE_NONE: String = "none"
    }
}
