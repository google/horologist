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
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlin.kapt")
    id("me.tylerbwong.gradle.metalava")
    alias(libs.plugins.dependencyAnalysis)
    kotlin("android")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 25
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = false
        compose = true
    }

    kotlinOptions {
        jvmTarget = "11"
        // Allow for widescale experimental APIs in Alpha libraries we build upon
        freeCompilerArgs = freeCompilerArgs + """
            kotlin.RequiresOptIn
            com.google.android.horologist.annotations.ExperimentalHorologistApi
            """.trim().split("\\s+".toRegex()).map { "-opt-in=$it" }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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
    namespace = "com.google.android.horologist.compose.layout"
}

kapt {
    correctErrorTypes = true
}

metalava {
    sourcePaths.setFrom("src/main")
    filename.set("api/current.api")
    reportLintsAsErrors.set(true)
}

dependencies {
    api(projects.annotations)

    api(libs.androidx.lifecycle.common)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.viewmodel.savedstate)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.androidx.paging)
    api(libs.compose.foundation.foundation)
    api(libs.compose.foundation.foundation.layout)
    api(libs.compose.runtime)
    api(libs.compose.ui)
    api(libs.wearcompose.material)
    api(libs.wearcompose.foundation)
    api(libs.wearcompose.navigation)

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.compose.animation)
    implementation(libs.compose.runtime.saveable)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.text)
    implementation(libs.compose.ui.unit)
    implementation(libs.compose.ui.util)
    implementation(libs.kotlin.stdlib)

    debugApi(libs.kotlinx.coroutines.core)
    debugApi(libs.wearcompose.tooling)

    debugRuntimeOnly(libs.compose.ui.test.manifest)

    releaseApi(libs.kotlinx.coroutines.core)

    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.compose.ui.geometry)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.robolectric.annotations)
    testImplementation(libs.truth)
}

dependencyAnalysis {
    issues {
        onAny {
            severity("fail")
            exclude(":annotations") // bug: reported as unused
        }
    }
}

apply(plugin = "com.vanniktech.maven.publish")
