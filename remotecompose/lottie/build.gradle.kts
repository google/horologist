/*
 * Copyright 2026 The Android Open Source Project
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
  kotlin("multiplatform")
  id("com.android.kotlin.multiplatform.library")
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.metalavaGradle)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.composeAiPreview)
}

composePreview { kmpAndroidRobolectric.set(true) }

kotlin {
  compilerOptions {
    languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
    freeCompilerArgs.addAll(
      listOf("-opt-in=androidx.compose.remote.player.compose.ExperimentalRemotePlayerApi")
    )
  }

  jvm()

  android {
    namespace = "com.google.android.horologist.remotecompose.lottie"
    compileSdk = 37
    minSdk = 29

    withHostTestBuilder {}.configure { isIncludeAndroidResources = true }
  }

  sourceSets {
    applyDefaultHierarchyTemplate()

    commonMain.dependencies {
      api(libs.compose.remote.creation)
      api(libs.compose.remote.creation.compose)
      implementation(libs.kotlinx.serialization.json)
      implementation(project.dependencies.platform(libs.compose.bom))
      implementation(libs.compose.runtime)
    }

    commonTest.dependencies { implementation(kotlin("test")) }

    val jvmAndAndroidMain by creating { dependsOn(commonMain.get()) }

    jvmMain { dependsOn(jvmAndAndroidMain) }

    androidMain {
      dependsOn(jvmAndAndroidMain)
      dependencies {
        implementation(libs.androidx.annotation)
        implementation(libs.compose.ui)
      }
    }

    getByName("androidHostTest").dependencies {
      implementation(libs.androidx.activity.compose)
      implementation(libs.compose.animation.core)
      implementation(libs.compose.foundation.foundation)
      implementation(libs.compose.foundation.foundation.layout)
      implementation(libs.compose.ui.toolingpreview)
      implementation(project.dependencies.platform(libs.compose.preview.daemon.bom))
      implementation(libs.compose.preview.annotations)
      implementation(libs.junit)
      implementation(libs.truth)
      implementation(libs.robolectric)
      implementation(libs.androidx.test.ext.ktx)
      implementation(libs.compose.ui.test.junit4)
      implementation(libs.compose.ui.test.manifest)
      implementation(libs.lottie.compose)
      implementation(projects.roboscreenshots)
      implementation(libs.androidx.compose.remote.player.core)
      implementation(libs.androidx.compose.remote.player.compose)
      implementation(libs.androidx.compose.remote.player.view)
    }
  }
}

tasks.withType<Test>().configureEach {
  listOf(
      "lottieCliRun",
      "lottieUrl",
      "lottieUrlsFile",
      "lottieOutput",
      "lottieFrames",
      "lottieSizeDp",
    )
    .forEach { prop -> project.findProperty(prop)?.let { systemProperty(prop, it.toString()) } }
}

metalava { filename.set("api/current.api") }
