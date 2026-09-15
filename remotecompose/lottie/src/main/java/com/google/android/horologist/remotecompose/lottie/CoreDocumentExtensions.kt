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

package com.google.android.horologist.remotecompose.lottie

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.PathIterator
import android.os.Build
import androidx.compose.remote.core.CoreDocument
import androidx.compose.remote.core.Operation
import androidx.compose.remote.core.PaintOperation
import androidx.compose.remote.core.RemoteComposeState
import androidx.compose.remote.core.RemoteContext
import androidx.compose.remote.core.VariableSupport
import androidx.compose.remote.core.operations.ComponentValue
import androidx.compose.remote.core.operations.ConditionalOperations
import androidx.compose.remote.core.operations.FloatExpression
import androidx.compose.remote.core.operations.FloatFunctionCall
import androidx.compose.remote.core.operations.FloatFunctionDefine
import androidx.compose.remote.core.operations.NamedVariable
import androidx.compose.remote.core.operations.PathAppend
import androidx.compose.remote.core.operations.PathCombine
import androidx.compose.remote.core.operations.PathCreate
import androidx.compose.remote.core.operations.PathData
import androidx.compose.remote.core.operations.layout.Component
import androidx.compose.remote.core.operations.layout.Container
import androidx.compose.remote.core.operations.layout.LayoutComponent
import androidx.compose.remote.core.operations.layout.LoopOperation
import androidx.compose.remote.core.operations.layout.modifiers.ModifierOperation
import androidx.compose.remote.player.core.platform.AndroidRemoteContext
import androidx.compose.remote.player.core.platform.FloatsToPath
import java.util.WeakHashMap

private val layoutComponentContentField by lazy {
  LayoutComponent::class.java.getDeclaredField("mContent").apply { isAccessible = true }
}

/** Finds the integer ID of a named variable within the [CoreDocument] operation hierarchy. */
@SuppressLint("RestrictedApi")
internal fun CoreDocument.findNamedVariableId(name: String): Int? {
  fun findIn(operations: List<Operation>): Int? {
    for (op in operations) {
      if (op is NamedVariable && (op.mVarName == name || op.mVarName == "USER:$name")) {
        return op.mVarId
      } else if (op is LayoutComponent) {
        val content = layoutComponentContentField.get(op) as? Container
        val canvasOps = op.canvasOperations
        if (content != null || canvasOps != null) {
          if (content != null) {
            val found = findIn(content.list)
            if (found != null) return found
          }
          if (canvasOps != null) {
            val found = findIn(canvasOps.list)
            if (found != null) return found
          }
        } else {
          val found = findIn(op.list)
          if (found != null) return found
        }
      } else if (op is Container) {
        val found = findIn(op.list)
        if (found != null) return found
      }
    }
    return null
  }
  return findIn(operations)
}

private val remoteContextField by lazy {
  RemoteComposeState::class.java.getDeclaredField("mRemoteContext").apply { isAccessible = true }
}

private val floatOverrideField by lazy {
  RemoteComposeState::class.java.getDeclaredField("mFloatOverride").apply { isAccessible = true }
}

private val pathDataChangedField by lazy {
  PathData::class.java.getDeclaredField("mPathChanged").apply { isAccessible = true }
}

private val pathCombineOutIdField by lazy {
  PathCombine::class.java.getDeclaredField("mOutId").apply { isAccessible = true }
}

private val pathCombineId1Field by lazy {
  PathCombine::class.java.getDeclaredField("mPathId1").apply { isAccessible = true }
}

private val pathCombineId2Field by lazy {
  PathCombine::class.java.getDeclaredField("mPathId2").apply { isAccessible = true }
}

private val pathCombineOpField by lazy {
  PathCombine::class.java.getDeclaredField("mOperation").apply { isAccessible = true }
}

private val floatFunctionCallOutArgsField by lazy {
  FloatFunctionCall::class.java.getDeclaredField("mOutArgs").apply { isAccessible = true }
}

private val floatFunctionCallFunctionField by lazy {
  FloatFunctionCall::class.java.getDeclaredField("mFunction").apply { isAccessible = true }
}

private val loopIndexVarIdField by lazy {
  LoopOperation::class.java.getDeclaredField("mIndexVariableId").apply { isAccessible = true }
}

private val loopFromOutField by lazy {
  LoopOperation::class.java.getDeclaredField("mFromOut").apply { isAccessible = true }
}

private val loopStepOutField by lazy {
  LoopOperation::class.java.getDeclaredField("mStepOut").apply { isAccessible = true }
}

private val loopUntilOutField by lazy {
  LoopOperation::class.java.getDeclaredField("mUntilOut").apply { isAccessible = true }
}

private val condTypeField by lazy {
  ConditionalOperations::class.java.getDeclaredField("mType").apply { isAccessible = true }
}

private val condVarAOutField by lazy {
  ConditionalOperations::class.java.getDeclaredField("mVarAOut").apply { isAccessible = true }
}

private val condVarBOutField by lazy {
  ConditionalOperations::class.java.getDeclaredField("mVarBOut").apply { isAccessible = true }
}

private val condDirtyField by lazy {
  ConditionalOperations::class.java.getDeclaredField("mDirty").apply { isAccessible = true }
}

private val lastAppliedValues = WeakHashMap<RemoteComposeState, MutableMap<Int, Float>>()

/** Sets an override for a named float on the document's [RemoteComposeState]. */
@SuppressLint("RestrictedApi")
internal fun CoreDocument.setNamedFloat(name: String, value: Float) {
  val id = findNamedVariableId(name) ?: return
  val state = remoteComposeState ?: return
  synchronized(lastAppliedValues) {
    val appliedForState = lastAppliedValues.getOrPut(state) { HashMap() }
    val previous = appliedForState[id]
    if (previous != null && previous == value && state.getFloat(id) == value) {
      return
    }
    appliedForState[id] = value
  }
  val floatOverrideArray = floatOverrideField.get(state) as? BooleanArray
  floatOverrideArray?.fill(false)
  state.overrideFloat(id, value)
  val context = remoteContextField.get(state) as? RemoteContext ?: return
  if (context is AndroidRemoteContext && context.paintContext == null) {
    context.useCanvas(Canvas())
  }
  context.mode = RemoteContext.ContextMode.PAINT
  val layoutDynamicIds = HashSet<Int>()
  val visitedContainers = HashSet<Container>()
  fun markTreeDirty(ops: List<Operation>) {
    for (op in ops) {
      op.markDirty()
      if (op is Container) {
        markTreeDirty(op.list)
      }
    }
  }
  fun evaluateStateOperations(ops: List<Operation>, inLayoutCanvasOps: Boolean = false) {
    for (op in ops) {
      op.markDirty()
      if (op is ComponentValue) {
        layoutDynamicIds.add(op.valueId)
      } else if (inLayoutCanvasOps && op is FloatExpression) {
        layoutDynamicIds.add(op.mId)
      }
      when {
        op is FloatFunctionDefine -> {
          op.registerListening(context)
          markTreeDirty(op.list)
        }
        op is FloatFunctionCall -> {
          op.registerListening(context)
          op.updateVariables(context)
          val fn = floatFunctionCallFunctionField.get(op) as? FloatFunctionDefine
          val outArgs = floatFunctionCallOutArgsField.get(op) as? FloatArray
          if (fn != null && outArgs != null) {
            val argIds = fn.args
            for (i in outArgs.indices) {
              context.loadFloat(argIds[i], outArgs[i])
            }
            evaluateStateOperations(fn.list, inLayoutCanvasOps)
          }
        }
        op is LoopOperation -> {
          op.updateVariables(context)
          val indexVarId = loopIndexVarIdField.getInt(op)
          val from = loopFromOutField.getFloat(op)
          val step = loopStepOutField.getFloat(op)
          val until = loopUntilOutField.getFloat(op)
          var i = from
          while (i < until) {
            if (indexVarId != 0) {
              context.loadFloat(indexVarId, i)
            }
            evaluateStateOperations(op.list, inLayoutCanvasOps)
            i += step
          }
        }
        op is ConditionalOperations -> {
          op.updateVariables(context)
          val type = condTypeField.getByte(op).toInt()
          val a = condVarAOutField.getFloat(op)
          val b = condVarBOutField.getFloat(op)
          val conditionMet =
            when (type) {
              0 -> a == b
              1 -> a != b
              2 -> a < b
              3 -> a <= b
              4 -> a > b
              5 -> a >= b
              6 -> condDirtyField.getBoolean(op)
              else -> false
            }
          condDirtyField.setBoolean(op, false)
          if (conditionMet) {
            evaluateStateOperations(op.list, inLayoutCanvasOps)
          }
        }
        op is PathData -> {
          op.updateVariables(context)
          pathDataChangedField.setBoolean(op, true)
          op.apply(context)
          state.getPathData(op.id)?.let { state.putPathData(op.id, it.clone()) }
        }
        op is PathCreate -> {
          op.updateVariables(context)
          op.apply(context)
        }
        op is PathAppend -> {
          op.updateVariables(context)
          op.apply(context)
        }
        op is PathCombine -> {
          op.updateVariables(context)
          applyPathCombine(op, state)
        }
        op is VariableSupport &&
          op !is PaintOperation &&
          op !is Component &&
          op !is ModifierOperation -> {
          op.updateVariables(context)
          op.apply(context)
        }
      }
      if (op is LayoutComponent) {
        val isCanvasLayout = op.javaClass.simpleName == "CanvasLayout"
        val content = layoutComponentContentField.get(op) as? Container
        val canvasOps = op.canvasOperations
        if (content != null || canvasOps != null) {
          if (content != null && visitedContainers.add(content)) {
            evaluateStateOperations(content.list, false)
          }
          if (canvasOps != null && visitedContainers.add(canvasOps)) {
            evaluateStateOperations(canvasOps.list, !isCanvasLayout)
          }
        } else {
          evaluateStateOperations(op.list, !isCanvasLayout)
        }
      } else if (
        op is Container &&
          op !is FloatFunctionDefine &&
          op !is LoopOperation &&
          op !is ConditionalOperations
      ) {
        if (visitedContainers.add(op)) {
          evaluateStateOperations(op.list, inLayoutCanvasOps)
        }
      }
    }
  }
  evaluateStateOperations(operations)
  try {
    val floatsField = state.javaClass.getDeclaredField("floats").apply { isAccessible = true }
    val overriddenFloatsField =
      state.javaClass.getDeclaredField("overriddenFloats").apply { isAccessible = true }
    @Suppress("UNCHECKED_CAST") val floatsMap = floatsField.get(state) as? Map<Int, Float>
    @Suppress("UNCHECKED_CAST")
    val overriddenFloatsMap = overriddenFloatsField.get(state) as? MutableMap<Int, Boolean>
    if (floatsMap != null && overriddenFloatsMap != null) {
      for (key in floatsMap.keys.toList()) {
        if (key in layoutDynamicIds) {
          overriddenFloatsMap.remove(key)
        } else {
          overriddenFloatsMap[key] = true
        }
      }
    }
  } catch (_: ReflectiveOperationException) {}
}

@SuppressLint("RestrictedApi")
private fun applyPathCombine(op: PathCombine, state: RemoteComposeState) {
  val outId = pathCombineOutIdField.getInt(op)
  val id1 = pathCombineId1Field.getInt(op)
  val id2 = pathCombineId2Field.getInt(op)
  val operation = pathCombineOpField.getByte(op).toInt()
  val path1 = resolveAndroidPath(state, id1)
  val path2 = resolveAndroidPath(state, id2)
  val pathOp =
    when (operation) {
      0 -> Path.Op.DIFFERENCE
      1 -> Path.Op.INTERSECT
      2 -> Path.Op.REVERSE_DIFFERENCE
      3 -> Path.Op.UNION
      4 -> Path.Op.XOR
      else -> Path.Op.UNION
    }
  val combined = Path(path1)
  combined.op(path2, pathOp)
  val winding =
    when (combined.fillType) {
      Path.FillType.EVEN_ODD -> 1
      Path.FillType.INVERSE_EVEN_ODD -> 2
      Path.FillType.INVERSE_WINDING -> 3
      else -> 0
    }
  state.putPathData(outId, androidPathToFloatArray(combined))
  state.putPathWinding(outId, winding)
  state.putPath(outId, combined)
}

@SuppressLint("RestrictedApi")
private fun resolveAndroidPath(state: RemoteComposeState, id: Int): Path {
  val existing = state.getPath(id) as? Path
  if (existing != null) return existing
  val path = Path()
  val data = state.getPathData(id)
  if (data != null) {
    FloatsToPath.genPath(path, data, 0f, 1f)
    path.fillType =
      when (state.getPathWinding(id)) {
        1 -> Path.FillType.EVEN_ODD
        2 -> Path.FillType.INVERSE_EVEN_ODD
        3 -> Path.FillType.INVERSE_WINDING
        else -> Path.FillType.WINDING
      }
  }
  return path
}

@SuppressLint("RestrictedApi")
private fun androidPathToFloatArray(path: Path): FloatArray {
  val out = ArrayList<Float>(64)
  if (Build.VERSION.SDK_INT >= 34) {
    val iterator = path.pathIterator
    val pts = FloatArray(8)
    while (iterator.hasNext()) {
      when (iterator.next(pts, 0)) {
        PathIterator.VERB_MOVE -> {
          out.add(PathData.MOVE_NAN)
          out.add(pts[0])
          out.add(pts[1])
        }
        PathIterator.VERB_LINE -> {
          out.add(PathData.LINE_NAN)
          out.add(pts[0])
          out.add(pts[1])
          out.add(pts[2])
          out.add(pts[3])
        }
        PathIterator.VERB_QUAD -> {
          out.add(PathData.QUADRATIC_NAN)
          out.add(pts[0])
          out.add(pts[1])
          out.add(pts[2])
          out.add(pts[3])
          out.add(pts[4])
          out.add(pts[5])
        }
        PathIterator.VERB_CONIC -> {
          out.add(PathData.CONIC_NAN)
          out.add(pts[0])
          out.add(pts[1])
          out.add(pts[2])
          out.add(pts[3])
          out.add(pts[4])
          out.add(pts[5])
          out.add(pts[6])
        }
        PathIterator.VERB_CUBIC -> {
          out.add(PathData.CUBIC_NAN)
          out.add(pts[0])
          out.add(pts[1])
          out.add(pts[2])
          out.add(pts[3])
          out.add(pts[4])
          out.add(pts[5])
          out.add(pts[6])
          out.add(pts[7])
        }
        PathIterator.VERB_CLOSE -> {
          out.add(PathData.CLOSE_NAN)
        }
      }
    }
  } else {
    val approx = path.approximate(0.25f)
    var i = 0
    while (i + 2 < approx.size) {
      val frac = approx[i]
      val x = approx[i + 1]
      val y = approx[i + 2]
      if (i == 0 || frac == 0f) {
        out.add(PathData.MOVE_NAN)
        out.add(x)
        out.add(y)
      } else {
        out.add(PathData.LINE_NAN)
        out.add(0f)
        out.add(0f)
        out.add(x)
        out.add(y)
      }
      i += 3
    }
  }
  return out.toFloatArray()
}
