import com.android.build.gradle.LibraryExtension
import com.vanniktech.maven.publish.SonatypeHost
import java.net.URL
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
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)

        classpath(libs.gradleMavenPublishPlugin)

        classpath(libs.dokka)

        classpath(libs.metalavaGradle)

        classpath(libs.affectedmoduledetector)

        classpath(libs.paparazziPlugin)

        classpath(libs.dagger.hiltandroidplugin)

        classpath(libs.googleSecretsGradlePlugin)
    }
}

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlinGradle) apply false
    alias(libs.plugins.benManes)
    alias(libs.plugins.versionCatalogUpdate)
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.affectedModulesDetector)
    alias(libs.plugins.gradleMavenPublishPlugin)
}

apply(plugin = "org.jetbrains.dokka")
apply(plugin = "com.dropbox.affectedmoduledetector")
apply(plugin = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>().configureEach {
    outputDirectory.set(rootProject.file("docs/api"))
    failOnWarning.set(true)
}

affectedModuleDetector {
    baseDir = "${project.rootDir}"
    pathsAffectingAllModules = setOf(
        "gradle/libs.versions.toml",
    )
    excludedModules = setOf(
        "sample",
        "paparazzi",
    )

    logFilename = "output.log"
    logFolder = "${rootProject.buildDir}/reports/affectedModuleDetector"

    val baseRef: String? = findProperty("affected_base_ref") as? String
    // If we have a base ref to diff against, extract the branch name and use it
    if (!baseRef.isNullOrEmpty()) {
        // Remove the prefix from the head.
        // TODO: need to support other types of git refs
        specifiedBranch = baseRef.replace("refs/heads/", "")
        compareFrom = "SpecifiedBranchCommit"
    } else {
        // Otherwise we use the previous commit. This is mostly used for commits to main.
        compareFrom = "PreviousCommit"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        val composeSnapshot = rootProject.libs.versions.composesnapshot.get()
        if (composeSnapshot.length > 1) {
            maven(url = uri("https://androidx.dev/snapshots/builds/$composeSnapshot/artifacts/repository/"))
        }

        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroup("com.github.QuickBirdEng.kotlin-snapshot-testing")
            }
        }
    }

    plugins.withId("com.vanniktech.maven.publish") {
        mavenPublishing {
            publishToMavenCentral(SonatypeHost("https://google.oss.sonatype.org"))
        }
    }
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlin {
            target("**/*.kt")
            ktlint(libs.versions.ktlint.get())
            licenseHeaderFile(rootProject.file("spotless/copyright.txt"))
        }

        groovyGradle {
            target("**/*.gradle")
            greclipse().configFile(rootProject.file("spotless/greclipse.properties"))
            licenseHeaderFile(
                rootProject.file("spotless/copyright.txt"),
                "(buildscript|apply|import|plugins)"
            )
        }
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            // Make sure that we're using the Android version of Guava
            if (this@configureEach.name.contains("android", ignoreCase = true)
                && this@eachDependency.requested.group == "com.google.guava"
                && this@eachDependency.requested.module.name == "guava"
                && this@eachDependency.requested.version?.contains("jre") == true) {
                this@eachDependency.requested.version?.replace(
                    "jre",
                    "android"
                )?.let { this@eachDependency.useVersion(it) }
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            if (System.getenv("CI") == "true") {
                // Treat all Kotlin warnings as errors
                allWarningsAsErrors = true
            }
            // Set JVM target to 1.8
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + listOf(
                // Allow use of @OptIn
                "-opt-in=kotlin.RequiresOptIn",
                // Enable default methods in interfaces
                "-Xjvm-default=all"
            )

            // Workaround for https://youtrack.jetbrains.com/issue/KT-37652
            if (!this@configureEach.name.endsWith("TestKotlin") && !this@configureEach.name.startsWith(
                    "compileDebug"
                ) && !project.name.contains(
                    "sample"
                ) && !project.name.contains("datalayer")) {
                // Enable explicit API mode
                this@configureEach.kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
            }
        }
    }

    // Read in the signing.properties file if it is exists
    val signingPropsFile = rootProject.file("release/signing.properties")
    if (signingPropsFile.exists()) {
        val localProperties = Properties()
        signingPropsFile.inputStream().use { istream -> localProperties.load(istream) }
        localProperties.forEach { prop ->
            project.extra[prop.key as String] = prop.value
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
            dokkaSourceSets.configureEach {
                reportUndocumented.set(true)
                skipEmptyPackages.set(true)
                skipDeprecated.set(true)
                jdkVersion.set(8)

                // Add Android SDK packages
                noAndroidSdkLink.set(false)

                // Add samples from :sample module
                samples.from(
                    rootProject.file("auth-sample-phone/src/main/java/"),
                    rootProject.file("auth-sample-wear/src/main/java/"),
                    rootProject.file("media-sample/src/main/java/"),
                    rootProject.file("sample/src/main/java/"),
                )

                // AndroidX + Compose docs
                externalDocumentationLink {
                    url.set(URL("https://developer.android.com/reference/"))
                    packageListUrl.set(URL("https://developer.android.com/reference/androidx/package-list"))
                }
                externalDocumentationLink {
                    url.set(URL("https://developer.android.com/reference/kotlin/"))
                    packageListUrl.set(URL("https://developer.android.com/reference/kotlin/androidx/package-list"))
                }

                sourceLink {
                    localDirectory.set(project.file("src/main/java"))
                    // URL showing where the source code can be accessed through the web browser
                    remoteUrl.set(URL("https://github.com/google/horologist/blob/main/${project.name}/src/main/java"))
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
        if (plugins.hasPlugin("com.android.library")) {
            configure<com.android.build.gradle.LibraryExtension> {
                lint {
                    // Remove once fixed: https://issuetracker.google.com/196420849
                    disable.add("ExpiringTargetSdkVersion")
                }
            }
        }

        val generateVersionFile = tasks.register("generateVersionFile") {
            val outputDirectory =
                project.file("${project.buildDir}/generated/sources/generateVersionFile")

            doLast {
                val versionName = project.properties.get("VERSION_NAME") as String

                val manifestDir = File(outputDirectory, "META-INF")
                manifestDir.mkdirs()
                File(
                    manifestDir,
                    "com.google.android.horologist_${project.name}.version"
                ).writeText("${versionName}\n")
            }
        }

        afterEvaluate {
            val outputDirectory =
                project.file("${project.buildDir}/generated/sources/generateVersionFile")

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

    afterEvaluate {
        var version: String? = null
        val composeSnapshot = libs.versions.composesnapshot.get()
        if (composeSnapshot.length > 1) {
            // We're depending on a Jetpack Compose snapshot, update the library version name
            // to indicate it's from a Compose snapshot
            val versionName = project.properties.get("VERSION_NAME") as String
            if (versionName.contains("SNAPSHOT")) {
                version = versionName.replace("-SNAPSHOT", ".compose-${composeSnapshot}-SNAPSHOT")
            }
        }

        if (version?.endsWith("SNAPSHOT") == false) {
            // If we're not a SNAPSHOT library version, we fail the build if we're relying on
            // any snapshot dependencies
            configurations.configureEach {
                dependencies.configureEach {
                    if (this is ProjectDependency) {
                        // We don't care about internal project dependencies
                        return@configureEach
                    }

//                    val depVersion = dependency.version
                    // TODO reenable https://github.com/google/horologist/issues/34
//                    if (depVersion != null && depVersion.endsWith("SNAPSHOT")) {
//                        throw IllegalArgumentException(
//                                "Using SNAPSHOT dependency with non-SNAPSHOT library version: $dependency"
//                        )
//                    }
                }
            }
        }
    }
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        (candidate.version.matches(".*-alpha.*".toRegex()) && !(currentVersion.matches(".*-alpha.*".toRegex()))) ||
            (candidate.version.matches(".*-beta.*".toRegex()) && !(currentVersion.matches(".*-(beta|alpha).*".toRegex())))
    }
}

apply(plugin = "nl.littlerobots.version-catalog-update")

versionCatalogUpdate {
    keep {
        keepUnusedVersions.set(true)
    }
}
