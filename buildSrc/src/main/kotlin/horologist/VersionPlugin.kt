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

package horologist

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
import java.io.File

class VersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val outputDir = File(project.buildDir, "generated/sources/generateVersionFile/META-INF")

        val generateVersionFile = project.tasks.register("generateVersionFile") {
            outputDir.mkdirs()
            outputs.dir(outputDir)

            val sourceSets = project.extensions.getByType<SourceSetContainer>()
            val resources = sourceSets.getByName("main").resources
            resources.srcDir(outputDir)

            val group = project.group
            val artifact = project.name
            val versionName = project.version as String
            File(outputDir, "${group}_$artifact.version").writeText(versionName)
        }

//        val generateReleaseResources = project.tasks.findByName("generateReleaseResources")
//        generateReleaseResources?.dependsOn(generateVersionFile)

        val processResources = project.tasks.findByName("processResources")
        processResources?.dependsOn(generateVersionFile)

//        val extension = project.extensions.getByName("androidComponents") as ApplicationAndroidComponentsExtension
//        project.extensions.configure<LibraryAndroidComponentsExtension> {
//
//        }

//        if (processResources == null && generateReleaseResources == null) {
//            throw GradleException("project ${project.name} does not have tasks")
//        }
    }
}
