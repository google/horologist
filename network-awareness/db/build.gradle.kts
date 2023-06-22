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
    id("com.google.devtools.ksp")
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
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi"
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

    namespace = "com.google.android.horologist.network.awareness.db"
}

project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    // Workaround for https://youtrack.jetbrains.com/issue/KT-37652
    if (!this.name.endsWith("TestKotlin") && !this.name.startsWith("compileDebug")) {
        this.kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xexplicit-api=strict"
        }
    }
}

metalava {
    sourcePaths.setFrom("src/main")
    filename.set("api/current.api")
    reportLintsAsErrors.set(true)
}

dependencies {
    api(projects.annotations)
    api(projects.networkAwareness.core)

    implementation(libs.kotlin.stdlib)
    implementation(libs.com.squareup.okhttp3.okhttp)

    implementation(libs.room.common)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.androidx.tracing.ktx)

    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.test.ext.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.ext.ktx)
    androidTestImplementation(libs.truth)
}

apply(plugin = "com.vanniktech.maven.publish")
