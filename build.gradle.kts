import com.android.build.gradle.LibraryExtension
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI
import java.util.Properties

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

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.android.tools.build.gradle)
        classpath(libs.kotlin.gradlePlugin)

        classpath(libs.gradleMavenPublishPlugin)

        classpath(libs.dagger.hiltandroidplugin)
        classpath(libs.oss.licenses.plugin)
    }
}

plugins {
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradleMavenPublishPlugin)
    alias(libs.plugins.kotlinGradle) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.metalavaGradle) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.spotless)
}

apply(plugin = "org.jetbrains.dokka")

val media3Checkout = project.properties["media3Checkout"]?.toString() ?: ""

if (media3Checkout.isNotBlank()) {
    allprojects {
        configurations.all {
            resolutionStrategy {
                dependencySubstitution {
                    substitute(module("androidx.media3:media3-common")).using(project(":media3-lib-common"))
                    substitute(module("androidx.media3:media3-datasource-okhttp")).using(project(":media3-lib-datasource-okhttp"))
                    substitute(module("androidx.media3:media3-exoplayer")).using(project(":media3-lib-exoplayer"))
                    substitute(module("androidx.media3:media3-exoplayer-dash")).using(project(":media3-lib-exoplayer-dash"))
                    substitute(module("androidx.media3:media3-exoplayer-hls")).using(project(":media3-lib-exoplayer-hls"))
                    substitute(module("androidx.media3:media3-exoplayer-rtsp")).using(project(":media3-lib-exoplayer-rtsp"))
                    substitute(module("androidx.media3:media3-exoplayer-workmanager")).using(
                        project(
                            ":media3-lib-exoplayer-workmanager"
                        )
                    )
                    substitute(module("androidx.media3:media3-session")).using(project(":media3-lib-session"))
                    substitute(module("androidx.media3:media3-test-utils")).using(project(":media3-test-utils"))
                    substitute(module("androidx.media3:media3-test-utils-robolectric")).using(
                        project(":media3-test-utils-robolectric")
                    )
                    substitute(module("androidx.media3:media3-ui")).using(project(":media3-lib-ui"))
                }
            }
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>().configureEach {
    outputDirectory.set(rootProject.file("docs/api"))
    failOnWarning.set(true)
}

allprojects {
    repositories {
        google()
        mavenCentral()

        val composeSnapshot = rootProject.libs.versions.composesnapshot.get()
        if (composeSnapshot.length > 1) {
            maven(url = uri("https://androidx.dev/snapshots/builds/$composeSnapshot/artifacts/repository/"))
        }
    }

    plugins.withId("com.vanniktech.maven.publish") {
        mavenPublishing {
            publishToMavenCentral()
            signAllPublications()
            if (project.plugins.hasPlugin("com.android.library")) {
                configure(
                    AndroidSingleVariantLibrary(
                        variant = "release",
                        sourcesJar = true,
                        publishJavadocJar = false
                    )
                )
            } else if (project.plugins.hasPlugin("java-library")) {
                configure(JavaLibrary(javadocJar = JavadocJar.Empty(), sourcesJar = true))
            }
        }
    }
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    if (childProjects.isEmpty()) {
        spotless {
            kotlin {
                target("**/*.kt")
                ktlint(libs.versions.ktlint.get())
                    .setEditorConfigPath(rootProject.file("quality/ktlint/.editorconfig"))
                licenseHeaderFile(rootProject.file("spotless/copyright.txt"))
            }
            kotlinGradle {
                target("**/*.gradle.kts")
                ktlint(libs.versions.ktlint.get())
                    .setEditorConfigPath(rootProject.file("quality/ktlint/.editorconfig"))
                licenseHeaderFile(
                    rootProject.file("spotless/copyright.txt"),
                    "(buildscript|apply|import|plugins)"
                )
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            if (rootProject.property("strict.build") == true) {
                // Treat all Kotlin warnings as errors
                allWarningsAsErrors = true
            }
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                listOf(
                    // Allow use of @OptIn
                    "-opt-in=kotlin.RequiresOptIn",
                    // Enable default methods in interfaces
                    "-Xjvm-default=all"
                )
            )
        }
    }

    // Must be afterEvaluate or else com.vanniktech.maven.publish will overwrite our
    // dokka and version configuration.
    afterEvaluate {
        if (tasks.findByName("dokkaHtmlPartial") == null) {
            // If dokka isn't enabled on this module, skip
            return@afterEvaluate
        }

        tasks.named<org.jetbrains.dokka.gradle.DokkaTaskPartial>("dokkaHtmlPartial") {
            failOnWarning.set(false)
            dokkaSourceSets.configureEach {
                reportUndocumented.set(true)
                skipEmptyPackages.set(true)
                skipDeprecated.set(true)
                jdkVersion.set(8)

                // Add Android SDK packages
                noAndroidSdkLink.set(false)

                // Add samples from :sample module
                samples.from(
                    rootProject.file("auth/sample/src/main/java/"),
                    rootProject.file("auth/sample/wear/src/main/java/"),
                    rootProject.file("media/sample/src/main/java/"),
                    rootProject.file("sample/src/main/java/"),
                )

                // AndroidX + Compose docs
                externalDocumentationLink {
                    url.set(URI("https://developer.android.com/reference/").toURL())
                    packageListUrl.set(URI("https://developer.android.com/reference/androidx/package-list").toURL())
                }
                externalDocumentationLink {
                    url.set(URI("https://developer.android.com/reference/kotlin/").toURL())
                    packageListUrl.set(URI("https://developer.android.com/reference/kotlin/androidx/package-list").toURL())
                }

                sourceLink {
                    localDirectory.set(project.file("src/main/java"))
                    // URL showing where the source code can be accessed through the web browser
                    remoteUrl.set(URI("https://github.com/google/horologist/blob/main/${project.name}/src/main/java").toURL())
                    // Suffix which is used to append the line number to the URL. Use #L for GitHub
                    remoteLineSuffix.set("#L")
                }

                perPackageOption {
                    matchingRegex.set("com.google.android.horologist.auth.sample.shared.*")

                    suppress.set(true)
                }

                // Remove composable previews from docs
                suppressedFiles.from(file("src/debug/java"))
            }
        }

        val buildDir = project.layout.buildDirectory
        val outputDirectory =
            buildDir.dir("generated/sources/generateVersionFile")
        val generateVersionFile = tasks.register("generateVersionFile") {

            doLast {
                val versionName = project.properties["VERSION_NAME"] as String

                val manifestDir = outputDirectory.get().dir("META-INF")
                manifestDir.asFile.mkdirs()
                val name = if (project.parent?.name == "horologist")
                    project.name
                else
                    project.parent?.name + project.name
                manifestDir.file(
                    "com.google.android.horologist_$name.version"
                ).asFile.writeText("${versionName}\n")
            }
        }

        afterEvaluate {
            val processResources = tasks.findByName("processResources")
            if (processResources != null) {
                processResources.dependsOn(generateVersionFile)

                val sourceSets = extensions.getByType(SourceSetContainer::class)
                val resources = sourceSets.findByName("main")?.resources
                resources?.srcDir(outputDirectory)
            }

            val isLibrary = plugins.hasPlugin("com.android.library")
            if (isLibrary) {
                val library = extensions.getByType(LibraryExtension::class)

                val resources = library.sourceSets.findByName("main")?.resources!!
                resources.srcDir(outputDirectory)
                if (resources.includes.isNotEmpty()) {
                    resources.include("META-INF/*.version")
                }

                library.libraryVariants.all {
                    processJavaResourcesProvider.get().dependsOn(generateVersionFile)
                }
            }
        }
    }
}