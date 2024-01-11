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

package com.google.android.horologist.ai.core.dummy

import com.google.android.horologist.ai.core.InferenceServiceGrpcKt
import com.google.android.horologist.ai.core.PromptRequest
import com.google.android.horologist.ai.core.Response
import com.google.android.horologist.ai.core.ServiceInfo
import com.google.android.horologist.ai.core.failure
import com.google.android.horologist.ai.core.modelId
import com.google.android.horologist.ai.core.modelInfo
import com.google.android.horologist.ai.core.response
import com.google.android.horologist.ai.core.serviceInfo
import com.google.android.horologist.ai.core.textResponse
import com.google.protobuf.Empty

class DummyInferenceServiceImpl(val thisId: String) : InferenceServiceGrpcKt.InferenceServiceCoroutineImplBase() {
    override suspend fun answerPrompt(request: PromptRequest): Response {
        if (request.model.id != thisId) {
            return response {
                failure = failure {
                    message = "Unknown model ${request.model.id}"
                }
            }
        } else if (request.prompt.hasText()) {
            val query = request.prompt.text.text
            return response {
                text = textResponse {
                    this.response =
                        "I didn't understand '$query'.\nPlease try again with a different question. $thisId"
                }
            }
        } else {
            return response {
                failure = failure {
                    message = "Unhandled request type $request"
                }
            }
        }
    }

    override suspend fun serviceInfo(request: Empty): ServiceInfo {
        return serviceInfo {
            name = "Dummy $thisId"
            models += modelInfo {
                id = modelId { id = thisId }
                name = "Dummy $thisId"
            }
        }
    }
}
