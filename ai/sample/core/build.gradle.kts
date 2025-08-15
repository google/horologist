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
    id("com.google.protobuf")
    kotlin("android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 23

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
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi",
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
    }

    lint {
        checkReleaseBuilds = false
        textReport = true
    }

    namespace = "com.google.android.horologist.ai.sample.core"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.stnd.get().toString()
    }
    plugins {
        create("javalite") {
            artifact = libs.protobuf.protoc.gen.javalite.get().toString()
        }
        create("grpc") {
            artifact = libs.protobuf.protoc.gen.grpc.java.get().toString()
        }
        create("grpckt") {
            artifact = libs.protobuf.protoc.gen.grpc.kotlin.get().toString()
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
            task.plugins {
                create("grpc") {
                    option("lite")
                }
                create("grpckt") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    api(projects.annotations)

    implementation(libs.dagger.hiltandroid)
    implementation(projects.datalayer.core)
    implementation(projects.datalayer.grpc)
    ksp(libs.dagger.hiltandroidcompiler)

    implementation(libs.grpc.stub)

    implementation(platform(libs.compose.bom))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)

    api(libs.playservices.wearable)
    implementation(libs.kotlinx.coroutines.playservices)
    api(libs.androidx.datastore.preferences)
    api(libs.androidx.datastore)
    api(libs.protobuf.kotlin.lite)
    api(libs.androidx.lifecycle.runtime)
    api(libs.androidx.wear.remote.interactions)
    api(libs.androidx.lifecycle.service)
    api(projects.datalayer.grpc)
    api(libs.io.grpc.grpc.android)
    api(libs.io.grpc.grpc.binder)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.test.ext.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espressocore)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.truth)
}

// tasks.maybeCreate("prepareKotlinIdeaImport")
//    .dependsOn("generateDebugProto")
