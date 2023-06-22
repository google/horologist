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

plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("org.jetbrains.dokka")
    id("me.tylerbwong.gradle.metalava")
    kotlin("android")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = false
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }

    packaging {
        resources {
            excludes += listOf(
                "/META-INF/AL2.0",
                "/META-INF/LGPL2.1"
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
    }

    namespace = "com.google.android.horologist.media.data"
}

metalava {
    sourcePaths.setFrom("src/main")
    filename.set("api/current.api")
    reportLintsAsErrors.set(true)
}

dependencies {
    api(projects.annotations)

    implementation(projects.media.core)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.corektx)
    implementation(libs.androidx.wear)
    implementation(project.findProject(":media-lib-common") ?: libs.androidx.media3.common)
    implementation(project.findProject(":media-lib-exoplayer") ?: libs.androidx.media3.exoplayer)
    implementation(
        project.findProject(":media-lib-exoplayer-workmanager")
            ?: libs.androidx.media3.exoplayerworkmanager
    )
    implementation(libs.room.common)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.androidx.lifecycle.service)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.test.ext.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(
        project.findProject(":media-lib-exoplayer") ?: libs.androidx.media3.exoplayer
    )
    testImplementation(project.findProject(":media-test-utils") ?: libs.androidx.media3.testutils)
    testImplementation(
        project.findProject(":media-test-utils-robolectric")
            ?: libs.androidx.media3.testutils.robolectric
    )
}

apply(plugin = "com.vanniktech.maven.publish")
