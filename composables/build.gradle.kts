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
    id("org.jetbrains.dokka")
    id("me.tylerbwong.gradle.metalava")
    kotlin("android")
    alias(libs.plugins.roborazzi)
}

android {
    compileSdk = 35

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
        compose = true
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.wear.compose.material.ExperimentalWearMaterialApi",
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes +=
                listOf(
                    "/META-INF/AL2.0",
                    "/META-INF/LGPL2.1",
                )
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.systemProperty("screenshot.record", findProperty("screenshot.record") ?: "false")
            }
        }
        animationsDisabled = true
    }
    lint {
        checkReleaseBuilds = false
        textReport = true
        disable += listOf("MissingTranslation", "ExtraTranslation")
    }
    namespace = "com.google.android.horologist.composables"
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
    api(projects.images.base)

    implementation(platform(libs.compose.bom))
    implementation(projects.composeLayout)
    implementation(libs.androidx.wear)
    api(libs.wearcompose.material)
    implementation(libs.wearcompose.foundation)
    implementation(libs.compose.material.iconscore)
    implementation(libs.compose.material.iconsext)
    implementation(libs.compose.material.ripple)
    implementation(libs.compose.ui.util)
    implementation(libs.androidx.corektx)

    implementation(libs.compose.ui.toolingpreview)
    implementation(libs.androidx.wear.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(projects.composeTools)
    debugImplementation(projects.composeMaterial)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(projects.composeMaterial)
    testImplementation(projects.roboscreenshots)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.runner)
    testImplementation(projects.images.coil)
}

apply(plugin = "com.vanniktech.maven.publish")
