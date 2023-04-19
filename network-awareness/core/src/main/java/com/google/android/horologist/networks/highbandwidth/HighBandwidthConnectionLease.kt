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

package com.google.android.horologist.networks.highbandwidth

import kotlin.time.Duration

/**
 * Cancellable network request token. Allows caller to release the connection or await it becoming
 * available.
 */
public interface HighBandwidthConnectionLease : AutoCloseable {
    /**
     * Await a connection being granted on this lease. The
     * connection could be immediately revoked, so apps
     * should check available networks after this call completes.
     * May return immediately if a network is unlikely to be
     * granted.
     *
     * @param timeout the length of time to wait for a likely network.
     * @return whether a network was found within the timeout.
     */
    public suspend fun awaitGranted(timeout: Duration): Boolean
}
