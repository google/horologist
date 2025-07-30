/*
 * Copyright 2025 The Android Open Source Project
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
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalavaGradle)
    kotlin("android")
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = false
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
        freeCompilerArgs = freeCompilerArgs + "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi"
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

    namespace = "com.google.android.horologist.audio.ui.material3"
}

project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    // Workaround for https://youtrack.jetbrains.com/issue/KT-37652
    if (!this.name.endsWith("TestKotlin") && !this.name.startsWith("compileDebug")) {
        compilerOptions {
            freeCompilerArgs.add("-Xexplicit-api=strict")
        }
    }
}

metalava {
    excludedSourceSets.setFrom("src/debug/java")
    filename.set("api/current.api")
}

dependencies {
    api(projects.media.audio)
    api(libs.kotlin.stdlib)
    api(projects.annotations)
    implementation(projects.composeLayout)
    implementation(project(":composables"))
    api(projects.media.audioUiModel)
    debugImplementation(projects.logo)

    implementation(platform(libs.compose.bom))
    api(libs.androidx.wear.compose.material3)
    api(libs.wearcompose.foundation)
    implementation(libs.androidx.corektx)
    implementation(libs.androidx.graphics.shapes)
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
    testImplementation(libs.androidx.test.espressocore)
    testImplementation(libs.truth)
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            moduleName.set("media-audio-ui-material3")
        }
    }
}

apply(plugin = "com.vanniktech.maven.publish")
