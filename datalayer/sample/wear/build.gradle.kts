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

import com.google.protobuf.gradle.id

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("com.google.protobuf")
    id("dagger.hilt.android.plugin")
    kotlin("android")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.google.android.horologist.datalayer.sample"
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
                "proguard-rules.pro",
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
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "11"

        // Allow for widescale experimental APIs in Alpha libraries we build upon
        freeCompilerArgs = freeCompilerArgs +
            listOf(
                "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi",
            )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }

    namespace = "com.google.android.horologist.datalayer.sample"
}

sourceSets {
    create("main") {
        java {
            srcDirs(
                "build/generated/source/proto/debug/java",
                "build/generated/source/proto/debug/grpc",
                "build/generated/source/proto/debug/kotlin",
                "build/generated/source/proto/debug/grpckt",
            )
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.26.1"
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
    api(projects.annotations)

    implementation(projects.composables)
    implementation(projects.composeLayout)
    implementation(projects.composeMaterial)
    implementation(projects.datalayer.core)
    implementation(projects.datalayer.grpc)
    implementation(projects.datalayer.sample.shared)
    implementation(projects.datalayer.watch)
    implementation(projects.tiles)
    implementation(libs.androidx.wear.protolayout.material)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.complications.data)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.wear)
    implementation(libs.compose.foundation.foundation)
    implementation(libs.compose.material.iconscore)
    implementation(libs.compose.material.iconsext)
    implementation(libs.compose.ui.toolingpreview)
    implementation(libs.kotlin.stdlib)
    implementation(libs.wearcompose.material)
    implementation(libs.wearcompose.foundation)
    implementation(libs.wearcompose.navigation)

    implementation(libs.kotlinx.coroutines.playservices)
    implementation(libs.protobuf.kotlin.lite)

    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.wear.tooling.preview)
    debugImplementation(projects.composeTools)
    releaseCompileOnly(projects.composeTools)
    debugImplementation(libs.androidx.wear.tiles.tooling.preview)
    implementation(libs.androidx.wear.tiles.tooling)

    implementation(libs.dagger.hiltandroid)
    ksp(libs.dagger.hiltandroidcompiler)
    implementation(libs.hilt.navigationcompose)

    testImplementation(libs.androidx.navigation.testing)
    testImplementation(libs.androidx.test.espressocore)
    testImplementation(libs.compose.ui.test)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.androidx.test.runner)
}

tasks.maybeCreate("prepareKotlinIdeaImport")
    .dependsOn("generateDebugProto")
