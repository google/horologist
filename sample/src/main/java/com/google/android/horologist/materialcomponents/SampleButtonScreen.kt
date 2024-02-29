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

package com.google.android.horologist.materialcomponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ButtonDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ButtonSize

@Composable
internal fun SampleButtonScreen(
    modifier: Modifier = Modifier,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = modifier,
        ) {
            item {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "contentDescription",
                    onClick = { println("Click") },
                    onLongClick = { println("LongClick") },
                    colors = ButtonDefaults.iconButtonColors(),
                )
            }
            item {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "contentDescription",
                    onClick = { },
                    colors = ButtonDefaults.secondaryButtonColors(),
                )
            }
            item {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "contentDescription",
                    onClick = { },
                    enabled = false,
                )
            }

            item {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "contentDescription",
                    onClick = { },
                    buttonSize = ButtonSize.Custom(
                        customIconSize = ButtonDefaults.SmallIconSize,
                        customTapTargetSize = ButtonDefaults.LargeButtonSize,
                    ),
                )
            }

            item {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "contentDescription",
                    onClick = { },
                    buttonSize = ButtonSize.Small,
                )
            }

            item {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "contentDescription",
                    onClick = { },
                    buttonSize = ButtonSize.Large,
                )
            }

            item {
                Button(
                    imageVector = Icons.Default.Check,
                    contentDescription = "contentDescription",
                    onClick = { },
                )
            }
        }
    }
}
