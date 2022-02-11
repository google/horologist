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

package com.google.android.horologist.system

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import com.google.android.horologist.system.BluetoothSettings.launchBluetoothSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Audio Output Repository for identifying available audio devices in a simple manner
 * such as []
 */
class AudioOutputRepository(
    private val audioManager: AudioManager,
    private val application: Context
) : AutoCloseable {
    private val _available = MutableStateFlow(listOf<AudioOutput>())
    private val _output = MutableStateFlow<AudioOutput>(AudioOutput.None)

    val audioOutput: StateFlow<AudioOutput>
        get() = _output

    val available: StateFlow<List<AudioOutput>>
        get() = _available

    private val callback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
            addDevices(addedDevices.toList())
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
            removeDevices(removedDevices)
        }
    }

    init {
        addDevices(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toList())

        val handler = Handler(Looper.getMainLooper())
        audioManager.registerAudioDeviceCallback(callback, handler)
    }

    private fun removeDevices(removedDevices: Array<out AudioDeviceInfo>) {
        var changed = false

        val currentAvailable = _available.value.toMutableList()

        removedDevices.forEach { audioDevice ->
            changed = changed or currentAvailable.removeIf { it.id == audioDevice.id }
        }

        if (changed) {
            _available.value = currentAvailable
            _output.value = currentAvailable.findBluetooth()
        }
    }

    private fun addDevices(addedDevices: List<AudioDeviceInfo>) {
        var added: AudioOutput? = null

        val currentAvailable = _available.value.toMutableList()

        val audioOutputs = addedDevices.mapNotNull { AudioOutput.fromDevice(it) }

        audioOutputs.forEach { output ->
            if (currentAvailable.find { it.id == output.id } == null) {
                currentAvailable.add(output)
                added = output
            }
        }

        if (added != null) {
            _available.value = currentAvailable
            _output.value = currentAvailable.findBluetooth()
        }
    }

    override fun close() {
        audioManager.unregisterAudioDeviceCallback(callback)
        _output.value = AudioOutput.None
        _available.value = listOf()
    }

    fun launchOutputSelection(closeOnConnect: Boolean) {
        application.launchBluetoothSettings(closeOnConnect)
    }

    companion object {
        fun fromContext(application: Context): AudioOutputRepository {
            val audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            return AudioOutputRepository(audioManager, application)
        }

        fun List<AudioOutput>.findBluetooth(): AudioOutput =
            find { it is AudioOutput.BluetoothHeadset } ?: firstOrNull() ?: AudioOutput.None
    }
}
