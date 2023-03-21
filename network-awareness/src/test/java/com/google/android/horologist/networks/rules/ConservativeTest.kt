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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.networks.rules

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.networks.data.RequestType.ImageRequest
import com.google.android.horologist.networks.data.RequestType.MediaRequest
import com.google.android.horologist.networks.data.RequestType.MediaRequest.MediaRequestType.Download
import com.google.android.horologist.networks.data.RequestType.MediaRequest.MediaRequestType.Stream
import com.google.android.horologist.networks.rules.NetworkingRules.Conservative
import com.google.android.horologist.networks.rules.helpers.Fixtures
import com.google.android.horologist.networks.rules.helpers.Fixtures.bt
import com.google.android.horologist.networks.rules.helpers.Fixtures.cell
import com.google.android.horologist.networks.rules.helpers.Fixtures.wifi
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ConservativeTest {
    val wifiFirst = Fixtures.networks(wifi, bt)
    val btFirst = Fixtures.networks(bt, wifi)
    val btOnly = Fixtures.networks(bt)
    val cellOnly = Fixtures.networks(cell)

    @Test
    fun getPreferredNetworkForImages() {
        assertThat(Conservative.getPreferredNetwork(wifiFirst, ImageRequest)).isEqualTo(wifi)
        assertThat(Conservative.getPreferredNetwork(btFirst, ImageRequest)).isEqualTo(wifi)
        assertThat(Conservative.getPreferredNetwork(btOnly, ImageRequest)).isEqualTo(bt)
        assertThat(Conservative.getPreferredNetwork(cellOnly, ImageRequest)).isEqualTo(cell)
    }

    @Test
    fun getPreferredNetworkForMediaDownloads() {
        assertThat(Conservative.getPreferredNetwork(wifiFirst, MediaRequest(Download))).isEqualTo(wifi)
        assertThat(Conservative.getPreferredNetwork(btFirst, MediaRequest(Download))).isEqualTo(wifi)
        assertThat(Conservative.getPreferredNetwork(btOnly, MediaRequest(Download))).isEqualTo(null)
        assertThat(Conservative.getPreferredNetwork(cellOnly, MediaRequest(Download))).isEqualTo(null)
    }

    @Test
    fun getPreferredNetworkForMediaStreams() {
        assertThat(Conservative.getPreferredNetwork(wifiFirst, MediaRequest(Stream))).isEqualTo(wifi)
        assertThat(Conservative.getPreferredNetwork(btFirst, MediaRequest(Stream))).isEqualTo(wifi)
        assertThat(Conservative.getPreferredNetwork(btOnly, MediaRequest(Stream))).isEqualTo(bt)
        assertThat(Conservative.getPreferredNetwork(cellOnly, MediaRequest(Stream))).isEqualTo(null)
    }

    @Test
    fun checkValidRequestForImages() {
        assertThat(Conservative.checkValidRequest(ImageRequest, wifi.networkInfo)).isInstanceOf(Allow::class.java)
        assertThat(Conservative.checkValidRequest(ImageRequest, cell.networkInfo)).isInstanceOf(Allow::class.java)
        assertThat(Conservative.checkValidRequest(ImageRequest, bt.networkInfo)).isInstanceOf(Allow::class.java)
    }

    @Test
    fun checkValidRequestForMediaDownloads() {
        assertThat(Conservative.checkValidRequest(MediaRequest(Download), wifi.networkInfo)).isInstanceOf(Allow::class.java)
        assertThat(Conservative.checkValidRequest(MediaRequest(Download), cell.networkInfo)).isInstanceOf(Fail::class.java)
        assertThat(Conservative.checkValidRequest(MediaRequest(Download), bt.networkInfo)).isInstanceOf(Fail::class.java)
    }

    @Test
    fun checkValidRequestForMediaStream() {
        assertThat(Conservative.checkValidRequest(MediaRequest(Stream), wifi.networkInfo)).isInstanceOf(Allow::class.java)
        assertThat(Conservative.checkValidRequest(MediaRequest(Stream), cell.networkInfo)).isInstanceOf(Fail::class.java)
        assertThat(Conservative.checkValidRequest(MediaRequest(Stream), bt.networkInfo)).isInstanceOf(Allow::class.java)
    }
}
