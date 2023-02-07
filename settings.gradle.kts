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

import java.util.Properties

plugins {
    id("com.gradle.enterprise") version "3.5"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "horologist"

include(":sample")
include(":compose-layout")
include(":composables")
include(":audio")
include(":audio-ui")
include(":auth-composables")
include(":auth-data")
include(":auth-data-phone")
include(":auth-sample-phone")
include(":auth-sample-wear")
include(":auth-ui")
include(":base-ui")
include(":tiles")
include(":media")
include(":media3-backend")
include(":media-ui")
include(":media-benchmark")
include(":media-data")
include(":media-sample")
include(":media-sample-benchmark")
include(":media-sync")
include(":compose-tools")
include(":paparazzi")
include(":network-awareness")
include(":datalayer")
include("health-composables")

// Enable Gradle's version catalog support
// https://docs.gradle.org/current/userguide/platforms.html
enableFeaturePreview("VERSION_CATALOGS")
// https://docs.gradle.org/7.4/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val localProperties = Properties()
val localFile = file("local.properties")
if (localFile.exists()) {
    localProperties.load(localFile.reader())
}
if (localProperties.getProperty("media3.checkout", "false").toBoolean()) {
    val extension = gradle as ExtensionAware
    extension.extra["androidxMediaModulePrefix"] = "media-"
    apply(from = file("../media/core_settings.gradle"))
}
