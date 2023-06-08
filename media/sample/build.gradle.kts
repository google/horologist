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

@file:Suppress("UnstableApiUsage")

import java.util.Properties
import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("com.google.protobuf")
    kotlin("android")
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

            buildConfigField("boolean", "BENCHMARK", "false")
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

            buildConfigField("boolean", "BENCHMARK", "false")
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

            buildConfigField("boolean", "BENCHMARK", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "11"
        // Allow for widescale experimental APIs in Alpha libraries we build upon
        freeCompilerArgs = freeCompilerArgs + """
            androidx.compose.foundation.ExperimentalFoundationApi
            androidx.compose.ui.ExperimentalComposeUiApi
            androidx.wear.compose.material.ExperimentalWearMaterialApi
            com.google.android.horologist.annotations.ExperimentalHorologistApi
            kotlin.RequiresOptIn
            kotlinx.coroutines.ExperimentalCoroutinesApi
            """.trim().split("\\s+".toRegex()).map { "-opt-in=$it" }
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
    api(projects.annotations)

    implementation(projects.baseUi)
    implementation(projects.media.audio)
    implementation(projects.media.audioUi)
    implementation(projects.composables)
    implementation(projects.composeLayout)
    implementation(projects.composeMaterial)
    implementation(projects.media.core)
    implementation(projects.media.backendMedia3)
    implementation(projects.media.data)
    implementation(projects.media.sync)
    implementation(projects.media.ui)
    implementation(projects.networkAwareness.core)
    implementation(projects.networkAwareness.ui)
    implementation(projects.networkAwareness.okhttp)
    implementation(projects.networkAwareness.db)
    implementation(projects.tiles)
    implementation(projects.logo)

    implementation(
        project.findProject(":media-lib-datasource-okhttp") ?: libs.androidx.media3.datasourceokhttp
    )

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
    implementation(libs.androidx.wear.protolayout.material)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlin.lite)

    add("benchmarkImplementation", libs.androidx.tracing.perfetto)
    add("benchmarkImplementation", libs.androidx.tracing.perfetto.binary)

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

    implementation(projects.auth.composables)
    implementation(projects.auth.data)
    implementation(projects.auth.ui)
    implementation(libs.playservices.auth)
    implementation(libs.kotlinx.coroutines.playservices)

    add("benchmarkImplementation", libs.androidx.runtime.tracing)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(projects.composeTools)
    releaseCompileOnly(projects.composeTools)
    add("benchmarkCompileOnly", projects.composeTools)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.test.ext.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)

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

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10") {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10") {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }
}

val device: String? = localProperties.getProperty("DEVICE")
if (device != null) {
    task<Exec>("appLaunch") {
        group = "Media"
        description = "Run on device $device"
        dependsOn(":media:media-sample:installRelease")
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

tasks.maybeCreate("prepareKotlinIdeaImport")
    .dependsOn("generateDebugProto")
