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

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

class VersionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def outputDir = new File(project.buildDir, "generated/sources/generateVersionFile/META-INF")

        def generateVersionFile =
                project.tasks.register("generateVersionFile", VersionFileTask) {
                    outputDirectory = outputDir
                }

        project.afterEvaluate {
            def processResources = project.tasks.findByName("processResources")
            if (processResources != null) {
                processResources.dependsOn(generateVersionFile)

                def sourceSets = project.extensions.getByType(SourceSetContainer)
                def resources = sourceSets.findByName("main")?.resources
                resources?.srcDir(outputDir)
            }

            def isLibrary = project.plugins.hasPlugin("com.android.library")
            if (isLibrary) {
                def library = project.extensions.getByType(LibraryExtension)

                def resources = library.sourceSets.findByName("main")?.resources
                resources?.srcDir(outputDir)

                library.libraryVariants.all {
                    processJavaResourcesProvider.get().dependsOn(generateVersionFile)
                }
            }
        }
    }
}