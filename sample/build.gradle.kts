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

import com.google.protobuf.gradle.id

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.protobuf")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.google.android.horologist.sample"
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
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
            "-opt-in=androidx.wear.compose.material.ExperimentalWearMaterialApi",
            "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-opt-in=com.google.android.horologist.audio.ExperimentalHorologistAudioApi",
            "-opt-in=com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi",
            "-opt-in=com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi",
            "-opt-in=com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi",
            "-opt-in=com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi",
            "-opt-in=com.google.android.horologist.base.ui.ExperimentalHorologistBaseUiApi",
            "-opt-in=com.google.android.horologist.composables.ExperimentalHorologistComposablesApi",
            "-opt-in=com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi",
            "-opt-in=com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi",
            "-opt-in=com.google.android.horologist.data.ExperimentalHorologistDataLayerApi",
            "-opt-in=com.google.android.horologist.media.ExperimentalHorologistMediaApi",
            "-opt-in=com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi",
            "-opt-in=com.google.android.horologist.networks.ExperimentalHorologistNetworksApi",
            "-opt-in=com.google.android.horologist.tiles.ExperimentalHorologistTilesApi",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    namespace = "com.google.android.horologist.sample"
}

sourceSets {
    create("main") {
        java {
            srcDirs(
                "build/generated/source/proto/debug/java",
                "build/generated/source/proto/debug/grpc",
                "build/generated/source/proto/debug/kotlin",
                "build/generated/source/proto/debug/grpckt"
            )
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.4"
    }
    plugins {
        id("javalite") {
            artifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(projects.baseUi)
    implementation(projects.composeLayout)
    implementation(projects.audio)
    implementation(projects.audioUi)
    implementation(projects.authComposables)
    implementation(projects.authData)
    implementation(projects.authUi)
    implementation(projects.tiles)
    implementation(projects.composables)
    implementation(projects.media)
    implementation(projects.mediaUi)
    implementation(projects.networkAwareness)
    implementation(projects.tiles)
    implementation(projects.datalayer)

    implementation(libs.compose.ui.util)

    implementation(libs.compose.foundation.foundation)
    implementation(libs.compose.material.iconsext)

    implementation(libs.wearcompose.material)
    implementation(libs.wearcompose.foundation)
    implementation(libs.wearcompose.navigation)

    implementation(libs.androidx.corektx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.wear.tiles)
    implementation(libs.androidx.wear.tiles.material)

    implementation(libs.kotlinx.coroutines.playservices)
    implementation(libs.kotlin.stdlib)

    implementation(libs.coil)

    implementation(libs.lottie.compose)

    implementation(libs.protobuf.kotlin.lite)

    implementation(libs.com.squareup.okhttp3.logging.interceptor)

    implementation(libs.compose.ui.toolingpreview)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(projects.composeTools)
    releaseCompileOnly(projects.composeTools)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.ext.ktx)
}
