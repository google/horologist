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

package com.google.android.horologist.mediasample.runner;

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import kotlin.reflect.jvm.jvmName

class MediaAppRunner : AndroidJUnitRunner() {
    override fun callApplicationOnCreate(app: Application?) {
        println("callApplicationOnCreate")
        super.callApplicationOnCreate(app)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        println("finish")
        super.finish(resultCode, results)
    }

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        println("newApplication")
        return super.newApplication(cl, TestMediaApplication::class.jvmName, context)
    }
}