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

plugins {
    id("com.gradle.develocity") version "4.1"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
    }
}

rootProject.name = "horologist"

include(":ai:sample:core")
include(":ai:sample:phone")
include(":ai:sample:wear-core")
include(":ai:sample:wear-prompt-app")
include(":ai:sample:wear-gemini")
include(":ai:sample:wear-gemini-lib")
include(":ai:ui")
include(":annotations")
include(":auth:composables")
include(":auth:data")
include(":auth:data-phone")
include(":auth:sample:phone")
include(":auth:sample:shared")
include(":auth:sample:wear")
include(":auth:ui")
include(":compose-layout")
include(":compose-material")
include(":compose-tools")
include(":composables")
include(":datalayer:core")
include(":datalayer:grpc")
include(":datalayer:watch")
include(":datalayer:phone")
include(":datalayer:phone-ui")
include(":datalayer:sample:phone")
include(":datalayer:sample:shared")
include(":datalayer:sample:wear")
include(":health:composables")
include(":health:service")
include(":images:base")
include(":images:coil")
include(":logo")
include(":media:audio")
include(":media:audio-ui")
include(":media:audio-ui-material3")
include(":media:audio-ui-model")
include(":media:core")
include(":media:backend-media3")
include(":media:media3-logging")
include(":media:media3-outputswitcher")
include(":media:ui")
include(":media:ui-material3")
include(":media:ui-model")
include(":media:benchmark")
include(":media:data")
include(":media:sample")
include(":media:sample-benchmark")
include(":media:sync")
include(":network-awareness:core")
include(":network-awareness:db")
include(":network-awareness:okhttp")
include(":network-awareness:ui")
include(":roboscreenshots")
include(":sample")
include(":tiles")

// https://docs.gradle.org/7.4/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val media3Checkout: String by settings

if (media3Checkout.isNotBlank()) {
    gradle.extra.set("androidxMediaModulePrefix", "media3-")
    apply(from = file("$media3Checkout/core_settings.gradle"))
}
