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

package com.google.android.horologist.media.sync.api

import android.util.Log
import kotlin.coroutines.cancellation.CancellationException

/**
 * Interface marker for a class that manages synchronization between local data and a remote
 * source for a [Syncable].
 */
public interface Synchronizer {
    public suspend fun getChangeListVersions(model: String): Int

    public suspend fun updateChangeListVersions(model: String, version: Int): Unit

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
     */
    public suspend fun Syncable.sync(): Boolean = this@sync.syncWith(this@Synchronizer)
}

/**
 * Interface marker for a class that is synchronized with a remote source. Syncing must not be
 * performed concurrently and it is the [Synchronizer]'s responsibility to ensure this.
 */
public interface Syncable {
    /**
     * Synchronizes the local database backing the repository with the network.
     * Returns if the sync was successful or not.
     */
    public suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception
    )
    Result.failure(exception)
}

/**
 * Utility function for syncing a repository with the network.
 * [model] Model that needs to be synced
 * [changeListFetcher] Fetches the change list for the model
 * [modelDeleter] Deletes models by consuming the ids of the models that have been deleted.
 * [modelUpdater] Updates models by consuming the ids of the models that have changed.
 *
 * Note that the blocks defined above are never run concurrently, and the [Synchronizer]
 * implementation must guarantee this.
 */
public suspend fun Synchronizer.changeListSync(
    model: String,
    changeListFetcher: suspend (currentVersion: Int) -> List<NetworkChangeList>,
    modelDeleter: suspend (ids: List<String>) -> Unit,
    modelUpdater: suspend (ids: List<String>) -> Unit
): Boolean = changeListSync(
    models = listOf(model),
    changeListFetcher = { _: String, currentVersion: Int -> changeListFetcher(currentVersion) },
    modelDeleter = { _: String, ids: List<String> -> modelDeleter(ids) },
    modelUpdater = { _: String, ids: List<String> -> modelUpdater(ids) }
)

/**
 * Utility function for syncing a repository with the network.
 * [models] List of models that needs to be synced
 * [changeListFetcher] Fetches the change list for the model
 * [modelDeleter] Deletes models by consuming the ids of the models that have been deleted.
 * [modelUpdater] Updates models by consuming the ids of the models that have changed.
 *
 * Note that the blocks defined above are never run concurrently, and the [Synchronizer]
 * implementation must guarantee this.
 */
public suspend fun Synchronizer.changeListSync(
    models: List<String>,
    changeListFetcher: suspend (model: String, currentVersion: Int) -> List<NetworkChangeList>,
    modelDeleter: suspend (model: String, ids: List<String>) -> Unit,
    modelUpdater: suspend (model: String, ids: List<String>) -> Unit
): Boolean = suspendRunCatching {
    models.forEach { model ->
        // Fetch the change list since last sync (akin to a git fetch)
        val currentVersion = getChangeListVersions(model)
        val changeList = changeListFetcher(model, currentVersion)
        if (changeList.isEmpty()) return@suspendRunCatching true

        val (deleted, updated) = changeList.partition(NetworkChangeList::isDelete)

        // Delete models that have been deleted server-side
        modelDeleter(model, deleted.map(NetworkChangeList::id))

        // Using the change list, pull down and save the changes (akin to a git pull)
        modelUpdater(model, updated.map(NetworkChangeList::id))

        // Update the last synced version (akin to updating local git HEAD)
        val latestVersion = changeList.last().changeListVersion
        updateChangeListVersions(model, latestVersion)
    }
}.isSuccess
