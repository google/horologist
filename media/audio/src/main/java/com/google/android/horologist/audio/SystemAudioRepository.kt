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

import android.content.Context
import android.media.MediaRoute2Info
import android.media.MediaRouter2
import android.media.MediaRouter2.RoutingController
import android.media.RouteDiscoveryPreference
import android.media.RoutingSessionInfo
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import androidx.mediarouter.media.MediaRouter.RouteInfo
import com.google.android.horologist.audio.BluetoothSettings.launchBluetoothSettings
import com.google.android.horologist.audio.OutputSwitcher.launchSystemMediaOutputSwitcherUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executor

/**
 * Audio Repository for identifying and controlling available audio devices in a simple manner.
 */
public class SystemAudioRepository(
    private val application: Context,
    private val mediaRouter: MediaRouter,
    selector: MediaRouteSelector = MediaRouteSelector.Builder().build(),
) : AudioOutputRepository, VolumeRepository {
    private val _available = MutableStateFlow(mediaRouter.devices)
    private val _output = MutableStateFlow(mediaRouter.output)
    private val _volume = MutableStateFlow(mediaRouter.volume)
    private val watchSpeakerSuitabilityChecker: WatchSpeakerSuitabilityChecker?

    override val volumeState: StateFlow<VolumeState>
        get() = _volume

    override fun increaseVolume() {
        mediaRouter.selectedRoute.requestUpdateVolume(1)
    }

    override fun decreaseVolume() {
        mediaRouter.selectedRoute.requestUpdateVolume(-1)
    }

    override fun setVolume(volume: Int) {
        mediaRouter.selectedRoute.requestSetVolume(volume)
    }

    override val audioOutput: StateFlow<AudioOutput>
        get() = _output

    override val available: StateFlow<List<AudioOutput>>
        get() = _available

    private val callback = object : MediaRouter.Callback() {
        override fun onRouteAdded(router: MediaRouter, route: RouteInfo) {
            update()
        }

        override fun onRouteRemoved(router: MediaRouter, route: RouteInfo) {
            update()
        }

        override fun onRouteSelected(router: MediaRouter, route: RouteInfo, reason: Int) {
            update()
        }

        override fun onRouteChanged(router: MediaRouter, route: RouteInfo) {
            update()
        }

        override fun onRouteVolumeChanged(router: MediaRouter, route: RouteInfo) {
            mediaRouter.fixInconsistency()
            _volume.value = mediaRouter.volume
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.VANILLA_ICE_CREAM) {
            watchSpeakerSuitabilityChecker = WatchSpeakerSuitabilityChecker()
            watchSpeakerSuitabilityChecker.registerControllerCallback()
        } else {
            watchSpeakerSuitabilityChecker = null
        }
        mediaRouter.addCallback(
            MediaRouteSelector.Builder()
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
                .addSelector(selector)
                .build(),
            callback,
        )
        update()
    }

    private fun update() {
        mediaRouter.fixInconsistency()
        _available.value = mediaRouter.devices.map { setWatchSpeakerPlayability(it) }
        _output.value = setWatchSpeakerPlayability(mediaRouter.output)
    }

    private fun setWatchSpeakerPlayability(audioOutput: AudioOutput): AudioOutput {
        if (watchSpeakerSuitabilityChecker == null || audioOutput.type != AudioOutput.TYPE_WATCH) {
            return audioOutput
        }

        return when {
            Build.VERSION.SDK_INT < VERSION_CODES.VANILLA_ICE_CREAM -> audioOutput
            watchSpeakerSuitabilityChecker.isWatchSpeakerSelected() -> AudioOutput.WatchSpeaker(audioOutput.id, audioOutput.name, true)
            else -> AudioOutput.WatchSpeaker(audioOutput.id, audioOutput.name, false)
        }
    }

    override fun close() {
        mediaRouter.removeCallback(callback)
        _output.value = AudioOutput.None
        _available.value = listOf()
        if (Build.VERSION.SDK_INT >= VERSION_CODES.VANILLA_ICE_CREAM) {
            watchSpeakerSuitabilityChecker?.unRegisterControllerCallback()
        }
    }

    override fun launchOutputSelection(closeOnConnect: Boolean) {
        if (!application.launchSystemMediaOutputSwitcherUi()) {
            application.launchBluetoothSettings(closeOnConnect)
        }
    }

    public companion object {
        public fun fromContext(application: Context): SystemAudioRepository {
            return SystemAudioRepository(
                application,
                MediaRouter.getInstance(application),
            )
        }
    }

    @RequiresApi(VERSION_CODES.VANILLA_ICE_CREAM)
    private inner class WatchSpeakerSuitabilityChecker {
        private val executor = Executor { command -> command.run() }
        private val mediaRouter = MediaRouter2.getInstance(application)
        private var wasWatchSpeakerSelectedPreviously = isWatchSpeakerSelected()

        private val routeDiscoveryPreference = RouteDiscoveryPreference.Builder(emptyList(), false).build()
        private val routeCallback = object : MediaRouter2.RouteCallback() {}
        private val controllerCallback = object : MediaRouter2.ControllerCallback() {
            public override fun onControllerUpdated(controller: RoutingController) {
                val isWatchSpeakerSelectedCurrently = isWatchSpeakerSelected()
                if (wasWatchSpeakerSelectedPreviously != isWatchSpeakerSelectedCurrently) {
                    wasWatchSpeakerSelectedPreviously = isWatchSpeakerSelectedCurrently
                    update()
                }
            }
        }

        fun registerControllerCallback() {
            // It is important to register a RouteDiscoveryPreference before registering ControllerCallback.
            mediaRouter.registerRouteCallback(executor, routeCallback, routeDiscoveryPreference)
            mediaRouter.registerControllerCallback(executor, controllerCallback)
        }

        fun unRegisterControllerCallback() {
            mediaRouter.unregisterControllerCallback(controllerCallback)
            mediaRouter.unregisterRouteCallback(routeCallback)
        }

        fun isWatchSpeakerSelected(): Boolean {
            val isWatchSpeakerSelected = mediaRouter.systemController.selectedRoutes.firstOrNull {
                it.type == MediaRoute2Info.TYPE_BUILTIN_SPEAKER
            } != null
            val transferReason = mediaRouter.systemController.routingSessionInfo.transferReason
            val isWatchSpeakerSelectedManually = transferReason == RoutingSessionInfo.TRANSFER_REASON_SYSTEM_REQUEST ||
                transferReason == RoutingSessionInfo.TRANSFER_REASON_APP

            return isWatchSpeakerSelected && isWatchSpeakerSelectedManually && mediaRouter.systemController.wasTransferInitiatedBySelf()
        }
    }
}

private fun MediaRouter.fixInconsistency() {
    if (selectedRoute !in routes) {
        selectRoute(defaultRoute)
    }
}

private inline val MediaRouter.volume: VolumeState
    get() {
        return selectedRoute.volumeState
    }

private inline val MediaRouter.output: AudioOutput
    get() {
        return selectedRoute.device
    }

private inline val MediaRouter.devices: List<AudioOutput>
    get() {
        return routes.map { it.device }
    }

private inline val RouteInfo.volumeState: VolumeState
    get() {
        return VolumeState(current = volume, max = volumeMax)
    }

private inline val RouteInfo.device: AudioOutput
    get() {
        return when {
            isBluetooth -> {
                AudioOutput.BluetoothHeadset(id, name)
            }
            isDeviceSpeaker -> {
                AudioOutput.WatchSpeaker(id, name)
            }
            else -> {
                AudioOutput.Unknown(id, name)
            }
        }
    }
