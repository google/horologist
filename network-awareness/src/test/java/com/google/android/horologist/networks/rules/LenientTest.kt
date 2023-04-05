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

package com.google.android.horologist.networks.rules

import com.google.android.horologist.networks.data.RequestType.ImageRequest
import com.google.android.horologist.networks.rules.NetworkingRules.Lenient
import com.google.android.horologist.networks.rules.helpers.Fixtures
import com.google.android.horologist.networks.rules.helpers.Fixtures.bt
import com.google.android.horologist.networks.rules.helpers.Fixtures.cell
import com.google.android.horologist.networks.rules.helpers.Fixtures.wifi
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LenientTest {
    @Test
    fun getPreferredNetwork() {
        val wifiFirst = Fixtures.networks(wifi, bt)
        assertThat(Lenient.getPreferredNetwork(wifiFirst, ImageRequest)).isEqualTo(wifi)

        val btFirst = Fixtures.networks(bt, wifi)
        assertThat(Lenient.getPreferredNetwork(btFirst, ImageRequest)).isEqualTo(wifi)

        val btOnly = Fixtures.networks(bt)
        assertThat(Lenient.getPreferredNetwork(btOnly, ImageRequest)).isEqualTo(bt)
    }

    @Test
    fun checkValidRequest() {
        assertThat(Lenient.checkValidRequest(ImageRequest, wifi.networkInfo)).isInstanceOf(Allow::class.java)

        assertThat(Lenient.checkValidRequest(ImageRequest, cell.networkInfo)).isInstanceOf(Allow::class.java)

        assertThat(Lenient.checkValidRequest(ImageRequest, bt.networkInfo)).isInstanceOf(Allow::class.java)
    }
}
