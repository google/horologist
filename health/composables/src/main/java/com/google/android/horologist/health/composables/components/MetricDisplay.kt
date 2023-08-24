/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.health.composables.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.health.composables.model.MetricUiModel

@Composable
public fun MetricDisplay(
    metric: MetricUiModel,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = metric.text,
            modifier = Modifier.alignBy(LastBaseline),
            color = metric.color,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.display3
        )

        if (!metric.topRightText.isNullOrEmpty() || !metric.bottomRightText.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignBy(LastBaseline)
            ) {
                Text(
                    text = metric.topRightText ?: "",
                    color = metric.color,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.body2

                )
                Text(
                    text = metric.bottomRightText ?: "",
                    color = metric.color,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}
