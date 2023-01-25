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

plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("android")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.google.android.horologist.auth.sample"
        // Min because of Tiles
        minSdk = 26
        targetSdk = 30

        versionCode = 1
        versionName = "1.0"

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
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "11"

        // Allow for widescale experimental APIs in Alpha libraries we build upon
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi",
            "-opt-in=com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi",
            "-opt-in=com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi",
            "-opt-in=com.google.android.horologist.base.ui.ExperimentalHorologistBaseUiApi",
            "-opt-in=com.google.android.horologist.composables.ExperimentalHorologistComposablesApi",
            "-opt-in=com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi",
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    namespace = "com.google.android.horologist.auth.sample"
}

dependencies {
    implementation(projects.authComposables)
    implementation(projects.authData)
    implementation(projects.authUi)
    implementation(projects.baseUi)
    implementation(projects.composables)
    implementation(projects.composeLayout)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.wear)
    implementation(libs.compose.foundation.foundation)
    implementation(libs.compose.ui.toolingpreview)
    implementation(libs.kotlin.stdlib)
    implementation(libs.wearcompose.material)
    implementation(libs.wearcompose.foundation)
    implementation(libs.wearcompose.navigation)

    implementation(libs.com.squareup.okhttp3.logging.interceptor)
    implementation(libs.com.squareup.okhttp3.okhttp)
    implementation(libs.kotlinx.coroutines.playservices)
    implementation(libs.moshi.kotlin)
    implementation(libs.playservices.auth)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(projects.composeTools)
    releaseCompileOnly(projects.composeTools)
}

secrets {
    propertiesFileName = "$projectDir/secrets.properties"
    defaultPropertiesFileName = "$projectDir/secrets.defaults.properties"
}
