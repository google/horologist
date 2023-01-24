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
    id("kotlin-android")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlin.kapt")
    id("me.tylerbwong.gradle.metalava")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 25
        targetSdk = 30
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packagingOptions {
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
    sourcePaths = mutableSetOf("src/main")
    filename = "api/current.api"
    reportLintsAsErrors = true
}

dependencies {

    api(libs.wearcompose.material)
    api(libs.wearcompose.foundation)
    api(libs.wearcompose.navigation)

    api(libs.accompanist.pager)

    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.paging)

    implementation(libs.kotlin.stdlib)
    implementation(libs.compose.ui.util)
    implementation(libs.androidx.wear)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.compose.ui.toolingpreview)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.ui.ktx)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(projects.composeTools)
    debugImplementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.truth)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.junit)
}

apply(plugin = "com.vanniktech.maven.publish")
