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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.Layer
import com.google.android.horologist.remotecompose.lottie.format.LayerType
import com.google.android.horologist.remotecompose.lottie.format.LottieDeserializer
import com.google.android.horologist.remotecompose.lottie.format.ShapeType
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParsingTest {

  private fun loadGeometryJson(): String {
    return ParsingTest::class.java.classLoader!!.getResourceAsStream("geometry.json")!!
      .bufferedReader()
      .readText()
  }

  @Test
  fun geometryTest() {
    val json = loadGeometryJson()
    val deserializer = LottieDeserializer.jsonAdapter

    val animation = deserializer.fromJson(json)!!

    assertThat(animation).isNotNull()
    assertThat(animation.name).isEqualTo("[lottie] geometry")
    assertThat(animation.version).isEqualTo("0.0.0")
    assertThat(animation.layers).hasSize(2)
  }

  @Test
  fun layerPolymorphism_deserializes() {
    val json = loadGeometryJson()
    val deserializer = LottieDeserializer.jsonAdapter

    val animation = deserializer.fromJson(json)!!

    assertThat(animation.layers[0].name).isEqualTo("Scale (Import Fix)")
    assertThat(animation.layers[0].type).isEqualTo(LayerType.Null)
    assertThat(animation.layers[0].index).isEqualTo(1)
  }

  @Test
  fun layerTypeEnum_deserializes() {
    val json = loadGeometryJson()
    val deserializer = LottieDeserializer.jsonAdapter

    val animation = deserializer.fromJson(json)!!

    assertThat(animation.layers[0].type).isEqualTo(LayerType.Null)
    assertThat(animation.layers[1].type).isEqualTo(LayerType.Shape)
  }

  @Test
  fun shapeTypePolymorphism_deserializes() {
    val json = loadGeometryJson()
    val deserializer = LottieDeserializer.jsonAdapter

    val animation = deserializer.fromJson(json)!!

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer
    val group = shapeLayer.shapes[0] as GraphicElement.Group

    assertThat(group.shapes.size).isEqualTo(3)
  }

  @Test
  fun shapeTypeEnum_deserializes() {
    val json = loadGeometryJson()
    val deserializer = LottieDeserializer.jsonAdapter

    val animation = deserializer.fromJson(json)!!

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer

    assertThat(shapeLayer.shapes[0].type).isEqualTo(ShapeType.Group)
  }

  @Test
  fun animatedBezierProperty_deserializes() {
    val json = loadGeometryJson()
    val deserializer = LottieDeserializer.jsonAdapter

    val animation = deserializer.fromJson(json)!!

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer
    val group = shapeLayer.shapes[0] as GraphicElement.Group
    val path = group.shapes[0] as GraphicElement.Path

    val animatedShape = path.shape as AnimatedBezierProperty

    assertThat(animatedShape.keyframes).hasSize(5)
    assertThat(animatedShape.keyframes[0].inTangent?.x).isEqualTo(0.999f)
    assertThat(animatedShape.keyframes[0].inTangent?.y).isEqualTo(1f)
  }

  @Test
  fun animatedVectorProperty_deserializes() {
    val json = loadGeometryJson()
    val deserializer = LottieDeserializer.jsonAdapter

    val animation = deserializer.fromJson(json)!!

    val shapeLayer = animation.layers[1] as Layer.ShapeLayer
    val transform = shapeLayer.transform!!

    assertThat(transform.scale.animated).isTrue()
    val animatedScale = transform.scale as AnimatedVectorProperty

    assertThat(animatedScale.keyframes).hasSize(5)
    assertThat(animatedScale.keyframes[0].inTangent?.x).isEqualTo(0.999f)
  }
}
