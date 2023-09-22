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

package com.google.android.horologist.screensizes

import androidx.core.os.LocaleListCompat
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import com.google.android.horologist.screenshots.ScreenshotTestRule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import java.util.Locale

@RunWith(ParameterizedRobolectricTestRunner::class)
class LocaleTileTest(val locale: Locale) : ScreenshotBaseTest(
    ScreenshotTestRule.screenshotTestRuleParams {
        record = ScreenshotTestRule.RecordMode.Repair
        testLabel = locale.toLanguageTag()
    },
) {

    @Test
    fun screenshot() {
        screenshotTestRule.setContent(takeScreenshot = true) {
            TestHarness(locales = LocaleListCompat.create(locale)) {
                SampleTilePreview()
            }
        }
    }


    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun locales() = listOf(
            "ar",
            "as",
            "b+es+419",
            "bg",
            "bn",
            "ca",
            "cs",
            "da",
            "de",
            "el",
            "en-rGB",
            "en-rIE",
            "en",
            "es-rUS",
            "es",
            "et",
            "eu",
            "fa",
            "fi",
            "fr-rCA",
            "fr",
            "gl",
            "gu",
            "hi",
            "hr",
            "hu",
            "hy",
            "in",
            "is",
            "it",
            "iw",
            "ja",
            "ka",
            "kk",
            "km",
            "kn",
            "ko",
            "ky",
            "lt",
            "lv",
            "mk",
            "ml",
            "mn",
            "mr",
            "ms",
            "my",
            "nb",
            "ne",
            "nl",
            "or",
            "pa",
            "pl",
            "pt-rBR",
            "pt-rPT",
            "ro",
            "ru",
            "si",
            "sk",
            "sl",
            "sq",
            "sr",
            "sv",
            "ta",
            "te",
            "th",
            "tr",
            "uk",
            "ur",
            "uz",
            "vi",
            "zh-rCN",
            "zh-rHK",
            "zh-rTW",
        ).map { Locale.forLanguageTag(it) }
    }

}
