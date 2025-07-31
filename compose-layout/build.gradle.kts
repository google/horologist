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
    id("com.android.library")
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalavaGradle)
    kotlin("android")
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = false
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
        // Allow for widescale experimental APIs in Alpha libraries we build upon
        freeCompilerArgs = freeCompilerArgs +
            """
            kotlin.RequiresOptIn
            com.google.android.horologist.annotations.ExperimentalHorologistApi
            """.trim().split("\\s+".toRegex()).map {
                "-opt-in=$it"
            }
    }

    packaging {
        resources {
            excludes +=
                listOf(
                    "/META-INF/AL2.0",
                    "/META-INF/LGPL2.1",
                )
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }

    lint {
        checkReleaseBuilds = false
        textReport = true
        // https://buganizer.corp.google.com/issues/328279054
        disable.add("UnsafeOptInUsageError")
    }
    namespace = "com.google.android.horologist.compose.layout"
}

metalava {
    excludedSourceSets.setFrom("src/debug/java")
    filename.set("api/current.api")
}

dependencies {
    api(projects.annotations)

    implementation(platform(libs.compose.bom))
    api(libs.wearcompose.material)
    api(libs.wearcompose.foundation)

    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.paging)
    api(libs.androidx.wear)
    api(libs.androidx.navigation.runtime)
    api(libs.wearcompose.navigation)

    implementation(libs.compose.ui.util)

    implementation(libs.compose.ui.tooling)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.wearcompose.tooling)

    debugImplementation(libs.compose.ui.toolingpreview)
    debugImplementation(projects.composeTools)
    debugImplementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.compose.material.iconscore)
    testImplementation(libs.compose.material.iconsext)
    testImplementation(libs.androidx.wear.compose.material3)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.androidx.test.espressocore)
    testImplementation(libs.robolectric)
    testImplementation(projects.composeTools)
    testImplementation(projects.roboscreenshots)
}

apply(plugin = "com.vanniktech.maven.publish")
