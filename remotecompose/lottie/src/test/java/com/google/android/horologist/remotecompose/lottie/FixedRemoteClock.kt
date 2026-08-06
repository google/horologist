/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.remote.core.RemoteClock
import androidx.compose.remote.core.SystemClock
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

/**
 * A [RemoteClock] frozen at a single instant.
 *
 * The player derives `ANIMATION_TIME` from `nanoTime() - startNanoTime`, so a constant [nanoTime]
 * pins playback to frame 0 and makes screenshots of animated documents reproducible.
 */
internal class FixedRemoteClock(
  instant: Instant = Instant.parse("2026-01-01T10:10:30Z"),
  zone: ZoneId = ZoneId.of("UTC"),
) : RemoteClock {
  private val delegate = SystemClock(Clock.fixed(instant, zone))

  override fun millis(): Long = delegate.millis()

  override fun nanoTime(): Long = 0L

  override fun getZoneId(): String = delegate.zoneId

  override fun snapshot(millis: Long?): RemoteClock.TimeSnapshot = delegate.snapshot(millis)
}
