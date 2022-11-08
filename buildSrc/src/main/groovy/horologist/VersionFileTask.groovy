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

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class VersionFileTask extends DefaultTask {
    @OutputDirectory
    abstract DirectoryProperty getOutputDir()

    @TaskAction
    void writeVersionFile() {
        def directory = outputDirectory.getAsFile().get()
        directory.mkdirs()

        def group = project.group
        def artifact = project.name
        def versionName = project.version as String
        new File(directory, "${group}_$artifact.version").write(versionName)
    }
}