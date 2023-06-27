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
        compose = true
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi"
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

    sourceSets.getByName("main") {
        assets.srcDir("src/main/assets")
    }

    lint {
        disable += listOf("MissingTranslation", "ExtraTranslation")
        checkReleaseBuilds = false
        textReport = true
    }

    namespace = "com.google.android.horologist.audio.ui"
}

kapt {
    correctErrorTypes = true
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
    api(projects.media.audio)
    api(libs.kotlin.stdlib)
    api(projects.annotations)
    implementation(projects.composeLayout)
    implementation(projects.composeMaterial)
    implementation(projects.baseUi)
    debugImplementation(projects.logo)

    api(libs.wearcompose.material)
    api(libs.wearcompose.foundation)
    implementation(libs.androidx.corektx)

    implementation(libs.compose.material.iconscore)
    implementation(libs.compose.material.iconsext)

    implementation(libs.androidx.wear)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.lottie.compose)

    implementation(libs.compose.ui.toolingpreview)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(projects.composeTools)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(projects.roboscreenshots)
    testImplementation(libs.robolectric)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.espresso.core)
    testImplementation(libs.truth)
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            moduleName.set("media-audio-ui")
        }
    }
}

apply(plugin = "com.vanniktech.maven.publish")
