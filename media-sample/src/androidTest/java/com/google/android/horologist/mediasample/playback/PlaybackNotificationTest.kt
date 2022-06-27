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

package com.google.android.horologist.mediasample.playback

import android.app.Notification
import androidx.core.app.NotificationCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.media.data.Media3MediaItemMapper
import com.google.android.horologist.media3.flows.waitForPlaying
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class PlaybackNotificationTest : BasePlaybackTest() {
    @Test
    fun testCausesNotification() = runTest {
        withContext(Dispatchers.Main) {
            val browser = browser()

            browser.setMediaItem(
                Media3MediaItemMapper.map(TestMedia.songMp3),
            )
            browser.prepare()
            browser.play()

            withTimeout(10.seconds) {
                browser.waitForPlaying()
            }

            val activeNotifications = notificationManager.activeNotifications.toList()

            assertThat(activeNotifications).isNotEmpty()

            val mediaNotifications =
                activeNotifications.filter { it.packageName == application.packageName }

            assertThat(activeNotifications).hasSize(1)
            val playbackNotification = mediaNotifications.first()
            val notification = playbackNotification.notification

            assertThat(playbackNotification.isOngoing).isTrue()
            assertThat(notification.category).isEqualTo(NotificationCompat.CATEGORY_TRANSPORT)
            // Media3 MediaNotificationHandler
            assertThat(notification.channelId).isEqualTo("default_channel_id")
            assertThat(notification.visibility).isEqualTo(Notification.VISIBILITY_PUBLIC)
        }
    }
}
