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

@file:Suppress("RestrictedApi", "INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.google.android.horologist.remotecompose.lottie.renderer.properties

import android.annotation.SuppressLint
import androidx.compose.remote.core.Limits
import androidx.compose.remote.core.operations.Utils
import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemoteFloatExpression
import androidx.compose.remote.creation.compose.state.RemoteOperationCacheKey
import androidx.compose.remote.creation.compose.state.RemoteStateInstanceKey

private enum class FunctionOperation {
  Call,
  Snapshot,
  Output,
}

/**
 * Snapshots live [RemoteFloat] values into per-call `FloatFunctionDefine` parameter slots.
 *
 * Embedded `RcPlayer` (`GraphContext`) indexes `FloatExpression` ops in `computedOpIndex` and
 * clears `memoPass` whenever `depth` returns to `0`, causing chained combinators (such as contour
 * pruning and circular scans) to re-evaluate ancestor expression DAGs exponentially per frame.
 * Function parameter slots have `isComputed(id) == false`, severing the ancestor DAG at O(1) state
 * ID overhead per batch while preserving constant values unchanged.
 */
internal fun snapshotRemoteFloats(inputs: List<RemoteFloat>): List<RemoteFloat> {
  val liveIndices = inputs.indices.filter { inputs[it].constantValueOrNull == null }
  if (liveIndices.isEmpty()) return inputs
  val outputs = inputs.toMutableList()
  for (chunkIndices in liveIndices.chunked(Limits.MAX_FUNCTION_ARGUMENTS)) {
    val chunkInputs = chunkIndices.map { inputs[it] }
    val batch =
      RemoteFloatExpression(
        null,
        RemoteOperationCacheKey.create(FunctionOperation.Snapshot, *chunkInputs.toTypedArray()),
      ) { state ->
        val args =
          chunkInputs.map { it.rawReference().getFloatIdForCreationState(state) }.toFloatArray()
        val snapshotOutputs = FloatArray(chunkInputs.size)
        val snapshotFn = state.document.createFloatFunction(snapshotOutputs)
        state.document.endFloatFunction()
        state.document.callFloatFunction(snapshotFn, *args)
        snapshotOutputs.forEach { reference ->
          check(Utils.idFromNan(reference) < Limits.MAX_STATE_DATA) {
            "Lottie geometry exceeds RC's ${Limits.MAX_STATE_DATA}-entry float-state limit"
          }
        }
        snapshotOutputs
      }
    for (localIndex in chunkIndices.indices) {
      outputs[chunkIndices[localIndex]] =
        RemoteFloatExpression(
          null,
          RemoteOperationCacheKey.create(FunctionOperation.Output, batch, localIndex),
        ) { state ->
          floatArrayOf(batch.arrayForCreationState(state)[localIndex])
        }
    }
  }
  return outputs
}

/**
 * A recording-local function with reusable scratch state and distinct result snapshots per call.
 * The body emits RC operations, not host-side playback code. Its argument/output IDs must not
 * escape except through the returned snapshots: subsequent calls overwrite the function state.
 */
internal class RemoteFloatFunction(
  private val argumentCount: Int,
  private val resultCount: Int,
  @SuppressLint("RestrictedApi") body: (RemoteComposeWriter, FloatArray) -> FloatArray,
) {
  // This holder is accessed only as a cached array, never evaluated as a float expression.
  private val definition =
    RemoteFloatExpression(null, RemoteStateInstanceKey()) { state ->
      val args = FloatArray(argumentCount)
      val id = state.document.createFloatFunction(args)
      val results = body(state.document, args)
      require(results.size == resultCount)
      state.document.endFloatFunction()
      floatArrayOf(Utils.asNan(id), *results)
    }

  operator fun invoke(inputs: List<RemoteFloat>): List<RemoteFloat> {
    require(inputs.size == argumentCount)
    val invocation =
      RemoteFloatExpression(
        null,
        RemoteOperationCacheKey.create(FunctionOperation.Call, definition, *inputs.toTypedArray()),
      ) { state ->
        // Resolve dependencies outside the definition and before the call. Never emit an input
        // expression into the reusable function body, where it could bind to a previous caller.
        val args = inputs.map { it.rawReference().getFloatIdForCreationState(state) }.toFloatArray()
        val program = definition.arrayForCreationState(state)
        state.document.callFloatFunction(Utils.idFromNan(program[0]), *args)
        if (resultCount == 0) {
          FloatArray(0)
        } else {
          require(resultCount <= Limits.MAX_FUNCTION_ARGUMENTS)
          // Snapshot outputs through a per-call FloatFunctionCall parameter block rather than
          // top-level FloatExpressions. Embedded RcPlayer indexes top-level FloatExpressions in
          // GraphContext.computedOpIndex and lazily re-evaluates them against the shared scratch
          // slots after later calls have overwritten them, whereas function parameter slots are
          // excluded from computedOpIndex and retain each call's written values in store.
          val snapshotOutputs = FloatArray(resultCount)
          val snapshotFn = state.document.createFloatFunction(snapshotOutputs)
          state.document.endFloatFunction()
          state.document.callFloatFunction(
            snapshotFn,
            *FloatArray(resultCount) { index -> program[index + 1] },
          )
          snapshotOutputs.forEach { reference ->
            check(Utils.idFromNan(reference) < Limits.MAX_STATE_DATA) {
              "Lottie geometry exceeds RC's ${Limits.MAX_STATE_DATA}-entry float-state limit"
            }
          }
          snapshotOutputs
        }
      }
    return List(resultCount) { index ->
      RemoteFloatExpression(
        null,
        RemoteOperationCacheKey.create(FunctionOperation.Output, invocation, index),
      ) { state ->
        floatArrayOf(invocation.arrayForCreationState(state)[index])
      }
    }
  }
}
