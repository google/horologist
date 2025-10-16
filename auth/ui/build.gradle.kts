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
    alias(libs.plugins.dependencyAnalysis)
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
        freeCompilerArgs = freeCompilerArgs +
            listOf(
                "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi",
                "-opt-in=kotlin.RequiresOptIn",
            )
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
        disable += listOf("MissingTranslation", "ExtraTranslation")
    }

    resourcePrefix = "horologist_"

    namespace = "com.google.android.horologist.auth.ui"
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

    api(projects.auth.composablesMaterial3)
    api(projects.auth.data)
    api(projects.composeLayout)

    api(libs.androidx.lifecycle.viewmodel)
    api(libs.compose.runtime)
    api(libs.compose.ui)
    api(libs.kotlinx.coroutines.core)
    api(libs.wearcompose.foundation)

    implementation(platform(libs.compose.bom))
    implementation(projects.composeMaterial)
    implementation(projects.images.coil)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodelktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.wear.phone.interactions)
    implementation(libs.compose.foundation.foundation.layout)
    implementation(libs.compose.ui.text)
    implementation(libs.compose.ui.unit)
    implementation(libs.kotlin.stdlib)
    implementation(libs.playservices.auth)
    implementation(libs.wearcompose.material)

    debugApi(libs.wearcompose.tooling)

    testImplementation(projects.composeTools)
    testImplementation(projects.roboscreenshots)
    testImplementation(libs.androidx.test.ext)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric.shadows)
    testRuntimeOnly(libs.robolectric)
}

dependencyAnalysis {
    issues {
        onAny {
            severity("fail")
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            moduleName.set("auth-ui")
        }
    }
}

apply(plugin = "com.vanniktech.maven.publish")
