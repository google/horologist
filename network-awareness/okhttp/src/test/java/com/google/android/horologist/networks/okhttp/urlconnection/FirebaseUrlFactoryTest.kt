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

package com.google.android.horologist.networks.okhttp.urlconnection

import com.google.common.truth.Truth.assertThat
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test

class FirebaseUrlFactoryTest {
    val server = MockWebServer()

    val client = OkHttpClient()
    val urlFactory = FirebaseUrlFactory(client)

    @Test
    fun getRequest() {
        server.enqueue(MockResponse().setBody("hello, world!"))

        val conn = urlFactory.open(server.url("/").toUrl())

        val text = conn.inputStream.bufferedReader().use {
            it.readText()
        }

        assertThat(text).isEqualTo("hello, world!")
        assertThat(conn.responseCode).isEqualTo(200)

        val recordedRequest = server.takeRequest()
        val headers = recordedRequest.headers

        assertThat(headers["Accept-Encoding"]).isEqualTo("gzip")
        assertThat(headers["Connection"]).isEqualTo("Keep-Alive")
    }

    @Test
    fun postRequest() {
        server.enqueue(MockResponse().setBody("hello, world!"))

        val conn = urlFactory.open(server.url("/").toUrl())

        conn.requestMethod = "POST"

        conn.outputStream.bufferedWriter().use {
            it.write("Hello from here")
        }

        val text = conn.inputStream.bufferedReader().use {
            it.readText()
        }

        assertThat(text).isEqualTo("hello, world!")
        assertThat(conn.responseCode).isEqualTo(200)

        val recordedRequest = server.takeRequest()
        val headers = recordedRequest.headers

        assertThat(recordedRequest.body.readUtf8()).isEqualTo("Hello from here")

        assertThat(headers["Accept-Encoding"]).isEqualTo("gzip")
        assertThat(headers["Connection"]).isEqualTo("Keep-Alive")
        assertThat(headers["Content-Type"]).isEqualTo("application/x-www-form-urlencoded")
        // TODO consider delaying to send with known size
        assertThat(headers["Transfer-Encoding"]).isEqualTo("chunked")
    }

    @Test
    fun getRequestWithCache() {
        server.enqueue(MockResponse().setResponseCode(304))

        val conn = urlFactory.open(server.url("/").toUrl())

        conn.ifModifiedSince = 1681890254000

        val text = conn.inputStream.bufferedReader().use {
            it.readText()
        }

        assertThat(text).isEqualTo("")
        assertThat(conn.responseCode).isEqualTo(304)

        val recordedRequest = server.takeRequest()
        val headers = recordedRequest.headers

        assertThat(headers["If-Modified-Since"]).isEqualTo("Wed, 19 Apr 2023 07:44:14 GMT")
    }

    @Test
    fun postRequestWithConnect() {
        server.enqueue(MockResponse().setBody("hello, world!"))

        val conn = urlFactory.open(server.url("/").toUrl())

        conn.requestMethod = "POST"
        conn.doInput = true
        conn.connect()

        conn.outputStream.bufferedWriter().use {
            it.write("Hello from here")
        }

        val text = conn.inputStream.bufferedReader().use {
            it.readText()
        }

        assertThat(text).isEqualTo("hello, world!")
        assertThat(conn.responseCode).isEqualTo(200)

        val recordedRequest = server.takeRequest()
        val headers = recordedRequest.headers

        assertThat(recordedRequest.body.readUtf8()).isEqualTo("Hello from here")
        assertThat(headers["Content-Type"]).isEqualTo("application/x-www-form-urlencoded")
        assertThat(headers["Transfer-Encoding"]).isEqualTo("chunked")
    }

    @Test
    fun clearProperty() {
        server.enqueue(MockResponse().setBody("hello, world!"))

        val conn = urlFactory.open(server.url("/").toUrl())

        conn.setRequestProperty("a", "b")
        conn.setRequestProperty("c", "d")
        conn.setRequestProperty("a", null)

        val text = conn.inputStream.bufferedReader().use {
            it.readText()
        }

        assertThat(text).isEqualTo("hello, world!")
        assertThat(conn.responseCode).isEqualTo(200)

        val recordedRequest = server.takeRequest()
        val headers = recordedRequest.headers

        assertThat(headers["a"]).isNull()
        assertThat(headers["c"]).isEqualTo("d")
        assertThat(headers["Connection"]).isEqualTo("Keep-Alive")
    }
}
