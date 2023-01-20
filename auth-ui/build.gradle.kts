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
    id("app.cash.paparazzi")
    id("me.tylerbwong.gradle.metalava")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 26
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
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
            "-opt-in=com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi",
            "-opt-in=com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi",
            "-opt-in=com.google.android.horologist.base.ui.ExperimentalHorologistBaseUiApi",
            "-opt-in=com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi",
            "-opt-in=kotlin.RequiresOptIn"
        )
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
        disable += listOf("MissingTranslation", "ExtraTranslation")
    }

    resourcePrefix = "horologist_"

    namespace = "com.google.android.horologist.auth.ui"
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
    sourcePaths = mutableSetOf("src/main")
    filename = "api/current.api"
    reportLintsAsErrors = true
}

dependencies {

    implementation(projects.authComposables)
    implementation(projects.authData)
    implementation(projects.baseUi)
    implementation(projects.composeLayout)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.wear)
    implementation(libs.androidx.wear.phone.interactions)
    implementation(libs.compose.foundation.foundation)
    implementation(libs.kotlin.stdlib)
    implementation(libs.playservices.auth)
    implementation(libs.wearcompose.material)
    implementation(libs.wearcompose.foundation)

    debugImplementation(projects.composeTools)
    implementation(libs.compose.ui.toolingpreview)

    testImplementation(projects.composeTools)
    testImplementation(projects.paparazzi)
    testImplementation(libs.androidx.test.ext.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.paparazzi)
    testImplementation(libs.robolectric)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.ext.ktx)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.truth)
}

apply(plugin = "com.vanniktech.maven.publish")