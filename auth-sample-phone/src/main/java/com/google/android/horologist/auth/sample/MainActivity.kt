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

package com.google.android.horologist.auth.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.android.horologist.auth.data.phone.tokenshare.TokenBundleRepository
import com.google.android.horologist.auth.data.phone.tokenshare.impl.TokenBundleRepositoryImpl
import com.google.android.horologist.auth.sample.shared.TokenSerializer
import com.google.android.horologist.auth.sample.ui.theme.HorologistTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var tokenBundleRepository: TokenBundleRepository<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenBundleRepository = TokenBundleRepositoryImpl.create(
            applicationContext = this@MainActivity.applicationContext,
            coroutineScope = lifecycleScope,
            serializer = TokenSerializer
        )

        setContent {
            HorologistTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(onSendData = ::onSendData)
                }
            }
        }
    }

    private fun onSendData() {
        lifecycleScope.launch {
            tokenBundleRepository.update("${System.currentTimeMillis()}")
        }
    }
}

@Composable
fun MainScreen(
    onSendData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onSendData()
        },
        modifier = modifier.wrapContentHeight()
    ) { Text("Update token!") }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HorologistTheme {
        MainScreen(onSendData = { })
    }
}
