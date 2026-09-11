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

package com.google.android.horologist.screenshots.rng

import org.junit.Assume
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Skips a test class unless it is assigned to the shard currently being run.
 *
 * Shards are selected by the `test.shardIndex` and `test.totalShards` system properties, which the
 * root build script populates from the `shardIndex` and `totalShards` Gradle properties. When
 * either is absent, or there is only one shard, every test runs.
 *
 * Assignment is by test class rather than by test method, so that all of a class's screenshots are
 * captured in the same JVM.
 *
 * This must be declared by *every* screenshot base class. It is a JUnit rule rather than a runner
 * or a Gradle-level filter, so it only applies to classes that actually declare it - a base class
 * that omits it will silently run in every shard.
 */
public class ShardRule : TestRule {
  override fun apply(base: Statement, description: Description): Statement =
    object : Statement() {
      override fun evaluate() {
        val shardIndex = System.getProperty("test.shardIndex")?.toIntOrNull()
        val totalShards = System.getProperty("test.totalShards")?.toIntOrNull()
        if (shardIndex != null && totalShards != null && totalShards > 1) {
          val className = description.className ?: description.displayName
          val assignedShard = Math.floorMod(className.hashCode(), totalShards)
          Assume.assumeTrue(
            "Skipping $className for shard $shardIndex of $totalShards (assigned to $assignedShard)",
            assignedShard == shardIndex,
          )
        }
        base.evaluate()
      }
    }
}
