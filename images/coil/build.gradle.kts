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

    sourceSets.getByName("main") {
        assets.srcDir("src/main/assets")
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
    namespace = "com.google.android.horologist.images.coil"
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
    filename.set("api/current.api")
}

dependencies {
    api(projects.annotations)
    api(projects.images.base)

    implementation(libs.coil)
    implementation(libs.coil.base)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.androidx.corektx)
    implementation(libs.wearcompose.material)

    testImplementation(projects.composeTools)
    testImplementation(projects.composeMaterial)
    testImplementation(projects.roboscreenshots)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.corektx)
    testImplementation(libs.compose.material.iconsext)
    testImplementation(libs.compose.ui.test)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.wearcompose.material)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testRuntimeOnly(libs.compose.ui.test.manifest)
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            moduleName.set("images-coil")
        }
    }
}

apply(plugin = "com.vanniktech.maven.publish")
