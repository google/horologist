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

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Locale

plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.roborazzi)
    kotlin("plugin.serialization")
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.google.android.horologist.sample"
        // Min because of Tiles
        minSdk = 26
        targetSdk = 34

        val date = LocalDate.now()

        versionCode = date.format(ofPattern("yyyyMMdd", Locale.ROOT)).toInt()
        versionName = date.toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["schemeSuffix"] = "-debug"
        }
        release {
            manifestPlaceholders["schemeSuffix"] = ""

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
        // Allow for widescale experimental APIs in Alpha libraries we build upon
        freeCompilerArgs = freeCompilerArgs +
            """
            androidx.compose.ui.ExperimentalComposeUiApi
            androidx.wear.compose.material.ExperimentalWearMaterialApi
            com.google.android.horologist.annotations.ExperimentalHorologistApi
            androidx.compose.foundation.ExperimentalFoundationApi
            kotlin.RequiresOptIn
            kotlinx.coroutines.ExperimentalCoroutinesApi
            """.trim().split("\\s+".toRegex()).map {
                "-opt-in=$it"
            }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.systemProperty("screenshot.record", findProperty("screenshot.record") ?: "false")
            }
        }
        animationsDisabled = true
    }

    lint {
        // https://buganizer.corp.google.com/issues/328279054
        disable.add("UnsafeOptInUsageError")
    }

    namespace = "com.google.android.horologist.sample"
}

dependencies {
    api(projects.annotations)

    implementation(projects.composeLayout)
    implementation(projects.media.audio)
    implementation(projects.media.audioUi)
    implementation(projects.auth.composables)
    implementation(projects.auth.data)
    implementation(projects.auth.ui)
    implementation(projects.composables)
    implementation(projects.composeMaterial)
    implementation(projects.networkAwareness.core)
    implementation(projects.networkAwareness.okhttp)
    implementation(projects.networkAwareness.ui)
    implementation(projects.media.core)
    implementation(projects.media.ui)
    implementation(projects.networkAwareness)
    implementation(projects.tiles)
    implementation(projects.logo)

    implementation(libs.compose.ui.util)

    implementation(libs.compose.animation.animationgraphics)
    implementation(libs.compose.foundation.foundation)
    implementation(libs.compose.material.iconsext)

    implementation(libs.wearcompose.material)
    implementation(libs.wearcompose.foundation)
    implementation(libs.wearcompose.navigation)

    implementation(libs.androidx.corektx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.wear.tiles)
    implementation(libs.androidx.wear.protolayout.material)

    implementation(libs.kotlinx.coroutines.playservices)
    implementation(libs.kotlin.stdlib)

    implementation(libs.lottie.compose)

    implementation(libs.com.squareup.okhttp3.logging.interceptor)

    implementation(libs.compose.ui.toolingpreview)
    implementation(libs.androidx.wear.tooling.preview)

    implementation(libs.kotlinx.serialization.core)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(projects.composeTools)
    debugImplementation(libs.androidx.wear.tiles.tooling.preview)
    debugImplementation(libs.androidx.wear.tiles.tooling)
    releaseCompileOnly(projects.composeTools)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(projects.composeTools)
    testImplementation(projects.composeMaterial)
    testImplementation(projects.roboscreenshots)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espressocore)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.ext.ktx)
}
