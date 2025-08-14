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
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalavaGradle)
    kotlin("android")
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 36

    defaultConfig {
        //        Tiles is API 26, but if we don't stop this here, then we can't run the sample app
        //        on older devices (25).  We also don't want this to bleed into other modules via
        //        compose-tools which is used just for testing.
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

    lint {
        checkReleaseBuilds = false
        textReport = true
    }
    namespace = "com.google.android.horologist.tiles"
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

    implementation(libs.kotlin.stdlib)

    implementation(libs.androidx.wear)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.concurrent.future)

    api(libs.androidx.wear.tiles)
    api(libs.androidx.wear.protolayout.material)
    api(libs.androidx.lifecycle.service)
    api(libs.androidx.concurrent.future.ktx)
    api(libs.androidx.lifecycle.process)

    implementation(libs.coil)
    implementation(libs.wearcompose.foundation)

    implementation(libs.androidx.complications.datasource.ktx)
    api(libs.wearcompose.material)

    testImplementation(projects.roboscreenshots)
    testImplementation(libs.androidx.wear.tiles.testing)
    testImplementation(libs.androidx.test.espressocore)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.concurrent.future.ktx)
    testImplementation(libs.androidx.lifecycle.testing)
    testImplementation(libs.androidx.wear.compose.material3)
    debugImplementation(libs.compose.ui.test.manifest)
}

apply(plugin = "com.vanniktech.maven.publish")
