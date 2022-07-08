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

package com.google.android.horologist

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class TestImagesInCi(
    val imageFile: File
) {
    @Test
    fun testImage() {
        assertThat(imageFile.exists()).isTrue()

        val imageIO = Class.forName("javax.imageio.ImageIO")
        val readMethod = imageIO.getMethod("read", File::class.java)

        val image = readMethod.invoke(null, imageFile)

        assertWithMessage("image $imageFile ${imageFile.length()} wasn't read").that(image)
            .isNotNull()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun images() = File("src/test/snapshots/images").listFiles()
    }
}
