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
    compileSdk = 33

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
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi"
        )
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
    namespace = "com.google.android.horologist.media3"
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
    api(projects.annotations)

    implementation(projects.audio)
    implementation(projects.media)
    implementation(projects.networkAwareness)
    implementation(libs.kotlinx.coroutines.core)
    api(project.findProject(":media-lib-common") ?: libs.androidx.media3.common)
    api(libs.androidx.annotation)
    api(project.findProject(":media-lib-exoplayer") ?: libs.androidx.media3.exoplayer)
    api(project.findProject(":media-lib-exoplayer-dash") ?: libs.androidx.media3.exoplayerdash)
    api(project.findProject(":media-lib-exoplayer-hls") ?: libs.androidx.media3.exoplayerhls)
    api(project.findProject(":media-lib-exoplayer-rtsp") ?: libs.androidx.media3.exoplayerrtsp)
    api(project.findProject(":media-lib-session") ?: libs.androidx.media3.session)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.androidx.corektx)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.tracing.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.test.ext.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(project.findProject(":media-test-utils") ?: libs.androidx.media3.testutils)
    testImplementation(
        project.findProject(":media-test-utils-robolectric")
            ?: libs.androidx.media3.testutils.robolectric
    )
}

apply(plugin = "com.vanniktech.maven.publish")
