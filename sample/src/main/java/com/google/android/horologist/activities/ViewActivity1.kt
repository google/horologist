/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.widget.SwipeDismissFrameLayout
import com.google.android.horologist.sample.R

class ViewActivity1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        val callback: AmbientLifecycleObserver.AmbientLifecycleCallback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
            override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                println("enter")
            }

            override fun onExitAmbient() {
                println("exit")
            }

            override fun onUpdateAmbient() {
                println("update")
            }
        }
        val observer = AmbientLifecycleObserver(this, callback)

        lifecycle.addObserver(observer)

//        findViewById<SwipeDismissFrameLayout>(R.id.swipe_dismiss_root).apply {
//            addCallback(object : SwipeDismissFrameLayout.Callback() {
//
//                override fun onDismissed(layout: SwipeDismissFrameLayout) {
//                    finish()
//                }
//            })
//        }
        findViewById<Button>(R.id.button).apply {
            setOnClickListener {
                val intent = Intent(this@ViewActivity1, ComposeActivity1::class.java)
                startActivity(intent)
            }
            text = "ViewActivity1"
            viewTreeObserver.addOnDrawListener {
                println("ViewActivity1")
            }
        }
    }
}