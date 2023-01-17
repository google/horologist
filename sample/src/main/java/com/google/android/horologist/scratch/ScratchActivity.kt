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

package com.google.android.horologist.scratch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.scrollAway

class ScratchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    var index by remember { mutableStateOf(0) }

    val item = remember(index) {
        styles[index]
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            ListHeader {
                Text(
                    text = item.title,
                    modifier = Modifier.clickable {
                        index = (index + 1) % styles.size
                    }
                )
            }
        }
        items(item.content) {
            Text(it)
        }
    }
}

data class Style(val title: String, val content: List<String>)

val oneBlock = Style(
    title = "Single",
    content = listOf(
        " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas pellentesque dui a nunc gravida, a consequat nisl ultrices. Nulla facilisi. Curabitur dui lacus, mattis vel mollis ac, hendrerit sit amet justo. Praesent suscipit aliquet libero, at hendrerit justo pharetra vehicula. Morbi cursus lacus molestie nisi varius ultrices. In sit amet cursus purus. Ut non quam fringilla, feugiat ipsum nec, pretium augue. Integer tincidunt nibh sed arcu pulvinar, eget dictum arcu tristique. Aliquam in orci a sem facilisis venenatis in at elit. Donec vel lobortis magna. Integer et nisl dapibus tellus gravida sodales quis sagittis augue. Morbi commodo, orci at congue aliquam, risus est fermentum sapien, eu sodales orci ex vel nibh. Fusce luctus erat ut nulla luctus euismod. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Morbi rhoncus, risus at tincidunt mattis, ex nibh congue lorem, vitae dictum mi urna non est. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
            "\n" +
            "Sed fermentum mollis neque non egestas. Maecenas lacinia nibh quis porttitor suscipit. Nullam bibendum ac turpis ut pharetra. Praesent faucibus posuere felis sit amet blandit. Sed id est malesuada dui cursus laoreet. Integer vel faucibus turpis, a iaculis tellus. Suspendisse condimentum nisi quis urna bibendum semper. Aliquam id semper sem, vel varius velit. Pellentesque risus dui, gravida vel risus ac, dignissim volutpat quam. Morbi convallis mauris et congue dapibus.\n" +
            "\n" +
            "Cras viverra finibus dolor. Quisque nisi erat, tincidunt quis rutrum at, laoreet in erat. Aliquam rutrum at leo molestie tristique. Quisque eleifend est ipsum, ac semper nulla aliquet sed. Donec sagittis risus est, quis pretium nibh venenatis at. Integer eu pharetra ante, eget pretium ipsum. Sed quis semper lectus, sit amet malesuada neque. Praesent tempus ullamcorper ligula, ut pellentesque ligula vestibulum a. Aenean non elementum purus. Maecenas vitae elit semper, elementum leo sed, condimentum ligula. Nunc bibendum quis nulla facilisis sodales. Morbi tempor dapibus leo sed fringilla. Duis at dui leo. Sed aliquet ipsum et dolor blandit consequat. Duis sagittis tortor nec nisl cursus finibus. Proin eu hendrerit ligula."
    )
)

val paragraphs = Style("Paragraphs", oneBlock.content.first().split("\n"))

val sentences = Style("Sentences", paragraphs.content.flatMap { it.split(". ") })

val styles = listOf(oneBlock, paragraphs, sentences)
