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
    id("dev.chrisbanes.paparazzi")
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
        jvmTarget = "1.8"
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
    sourcePaths = mutableSetOf("src/main")
    filename = "api/current.api"
    reportLintsAsErrors = true
}

dependencies {
    api(projects.audio)
    api(libs.kotlin.stdlib)
    implementation(projects.composeLayout)

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

    testImplementation(libs.junit)
    testImplementation(projects.paparazzi)
    testImplementation(libs.paparazzi)

    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.truth)
}

apply(plugin = "com.vanniktech.maven.publish")
