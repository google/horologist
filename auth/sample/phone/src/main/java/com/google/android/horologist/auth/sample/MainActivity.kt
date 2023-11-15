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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.android.horologist.auth.data.phone.tokenshare.TokenBundleRepository
import com.google.android.horologist.auth.data.phone.tokenshare.impl.TokenBundleRepositoryImpl
import com.google.android.horologist.auth.sample.shared.TOKEN_BUNDLE_CUSTOM_KEY
import com.google.android.horologist.auth.sample.shared.TokenBundleSerializer
import com.google.android.horologist.auth.sample.shared.model.TokenBundleProto.TokenBundle
import com.google.android.horologist.auth.sample.ui.theme.HorologistTheme
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var tokenBundleRepositoryDefaultKey: TokenBundleRepository<TokenBundle?>
    private lateinit var tokenBundleRepositoryCustomKey: TokenBundleRepository<TokenBundle?>

    private lateinit var phoneDataLayerAppHelper: PhoneDataLayerAppHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val registry = WearDataLayerRegistry.fromContext(
            application = this@MainActivity.applicationContext,
            coroutineScope = lifecycleScope,
        )

        tokenBundleRepositoryDefaultKey = TokenBundleRepositoryImpl(
            registry = registry,
            coroutineScope = lifecycleScope,
            serializer = TokenBundleSerializer,
        )

        tokenBundleRepositoryCustomKey = TokenBundleRepositoryImpl(
            registry = registry,
            coroutineScope = lifecycleScope,
            serializer = TokenBundleSerializer,
            key = TOKEN_BUNDLE_CUSTOM_KEY,
        )

        phoneDataLayerAppHelper = PhoneDataLayerAppHelper(
            context = this,
            registry = registry,
        )

        setContent {
            HorologistTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    var apiAvailable by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            apiAvailable = tokenBundleRepositoryDefaultKey.isAvailable()
                        }
                    }

                    MainScreen(
                        apiAvailable = apiAvailable,
                        onUpdateTokenDefault = ::onUpdateTokenDefault,
                        onUpdateTokenCustom = ::onUpdateTokenCustom,
                    )
                }
            }
        }
    }

    private fun onUpdateTokenDefault() {
        lifecycleScope.launch {
            tokenBundleRepositoryDefaultKey.update(
                TokenBundle.newBuilder()
                    .setAccessToken("${System.currentTimeMillis()}")
                    .build(),
            )
        }
    }

    private fun onUpdateTokenCustom() {
        lifecycleScope.launch {
            tokenBundleRepositoryCustomKey.update(
                TokenBundle.newBuilder()
                    .setAccessToken("${System.currentTimeMillis()}")
                    .build(),
            )
        }
    }
}

@Composable
fun MainScreen(
    apiAvailable: Boolean,
    onUpdateTokenDefault: () -> Unit,
    onUpdateTokenCustom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                onUpdateTokenDefault()
            },
            modifier = Modifier.wrapContentHeight(),
            enabled = apiAvailable,
        ) { Text(stringResource(R.string.token_share_button_update_token_default)) }

        Button(
            onClick = {
                onUpdateTokenCustom()
            },
            modifier = Modifier.wrapContentHeight(),
            enabled = apiAvailable,
        ) { Text(stringResource(R.string.token_share_button_update_token_custom)) }

        if (!apiAvailable) {
            Text(
                text = stringResource(R.string.token_share_message_api_unavailable),
                modifier.fillMaxWidth(),
                color = Color.Red,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    HorologistTheme {
        MainScreen(
            apiAvailable = true,
            onUpdateTokenDefault = { },
            onUpdateTokenCustom = { },
        )
    }
}
