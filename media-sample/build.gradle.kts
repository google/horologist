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
import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("com.google.protobuf")
}

val localProperties = Properties()
val localFile = project.rootProject.file("local.properties")
if (localFile.exists()) {
    localProperties.load(localFile.reader())
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.google.android.horologist.mediasample"
        // Min because of Tiles
        minSdk = 26
        targetSdk = 30

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "com.google.android.horologist.mediasample.runner.MediaAppRunner"
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
        create("benchmark") {
            initWith(buildTypes.getByName("release"))

            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-benchmark.pro"
            )

            matchingFallbacks.add("release")
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
        jvmTarget = "1.8"
        // Allow for widescale experimental APIs in Alpha libraries we build upon
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=androidx.wear.compose.material.ExperimentalWearMaterialApi",
            "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-opt-in=com.google.android.horologist.audio.ExperimentalHorologistAudioApi",
            "-opt-in=com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi",
            "-opt-in=com.google.android.horologist.base.ui.ExperimentalHorologistBaseUiApi",
            "-opt-in=com.google.android.horologist.composables.ExperimentalHorologistComposablesApi",
            "-opt-in=com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi",
            "-opt-in=com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi",
            "-opt-in=com.google.android.horologist.media.ExperimentalHorologistMediaApi",
            "-opt-in=com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi",
            "-opt-in=com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi",
            "-opt-in=com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi",
            "-opt-in=com.google.android.horologist.networks.ExperimentalHorologistNetworksApi",
            "-opt-in=com.google.android.horologist.tiles.ExperimentalHorologistTilesApi",
            "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    namespace = "com.google.android.horologist.mediasample"
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

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(projects.baseUi)
    implementation(projects.audio)
    implementation(projects.audioUi)
    implementation(projects.composables)
    implementation(projects.composeLayout)
    implementation(projects.composeLayout)
    implementation(projects.composeTools)
    implementation(projects.media)
    implementation(projects.media3Backend)
    implementation(projects.mediaData)
    implementation(projects.mediaSync)
    implementation(projects.mediaUi)
    implementation(projects.networkAwareness)
    implementation(projects.tiles)

    implementation(
        project.findProject(":media-lib-datasource-okhttp") ?: libs.androidx.media3.datasourceokhttp
    )

    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.util)

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
    implementation(libs.androidx.wear.tiles.material)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlin.lite)

    implementation(libs.androidx.complications.datasource.ktx)

    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.retrofit2.convertermoshi)
    implementation(libs.retrofit2.retrofit)
    implementation(libs.com.squareup.okhttp3.okhttp)
    implementation(libs.com.squareup.okhttp3.logging.interceptor)

    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.androidx.palette.ktx)

    implementation(libs.lottie.compose)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.guava)

    debugImplementation(projects.composeTools)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.complications.rendering)

    implementation(libs.dagger.hiltandroid)
    kapt(libs.dagger.hiltandroidcompiler)
    implementation(libs.hilt.navigationcompose)

    implementation(libs.androidx.metrics.performance)

    implementation(
        project.findProject(":media-lib-exoplayer-workmanager")
            ?: libs.androidx.media3.exoplayerworkmanager
    )

    implementation(libs.room.common)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.androidx.tracing.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.test.ext.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.ext.ktx)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.androidx.complications.rendering)
    androidTestImplementation(libs.dagger.hiltandroidtesting)
    kaptAndroidTest(libs.dagger.hiltandroidcompiler)
}

val device: String? = localProperties.getProperty("DEVICE")
if (device != null) {
    task<Exec>("appLaunch") {
        group = "Media"
        description = "Run on device $device"
        dependsOn(":media-sample:installRelease")
        description = "Launch App"
        commandLine =
            "adb -s $device shell am start -n com.google.android.horologist.mediasample/com.google.android.horologist.mediasample.ui.app.MediaActivity"
                .split(" ")
    }

    task<Exec>("offloadStatus") {
        group = "Media"
        description = "Offload Status for $device"
        description = "Offload Status"
        commandLine = "adb -s $device shell dumpsys media.audio_flinger".split(" ")
    }
}
