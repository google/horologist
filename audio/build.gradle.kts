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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = false
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packagingOptions {
        resources {
            excludes.addAll(listOf("/META-INF/AL2.0", "/META-INF/LGPL2.1"))
        }
    }


    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }

    sourceSets.getByName("test") {
        java.srcDir("src/sharedTest/kotlin")
        res.srcDir("src/sharedTest/res")
    }
    sourceSets.getByName("androidTest") {
        java.srcDir("src/sharedTest/kotlin")
        res.srcDir("src/sharedTest/res")
    }
    lint {
        checkReleaseBuilds = false
        textReport = true
    }
    namespace = "com.google.android.horologist.audio"
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
    implementation(libs.kotlin.stdlib)

    implementation(libs.androidx.mediarouter)
    implementation(libs.androidx.wear)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.kotlinx.coroutines.android)

    androidTestImplementation(libs.androidx.lifecycle.testing)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit)
}

apply(plugin = "com.vanniktech.maven.publish")
