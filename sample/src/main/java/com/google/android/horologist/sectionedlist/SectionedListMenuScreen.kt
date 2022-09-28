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

package com.google.android.horologist.sectionedlist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.sample.R
import com.google.android.horologist.sample.Screen
import com.google.android.horologist.sectionedlist.component.Title

@Composable
fun SectionedListMenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
    scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState(),
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    SectionedList(
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier
    ) {
        section(
            listOf(
                Pair(
                    R.string.sectionedlist_stateless_sections_menu,
                    Screen.SectionedListStatelessScreen.route
                ),
                Pair(
                    R.string.sectionedlist_stateful_sections_menu,
                    Screen.SectionedListStatefulScreen.route
                ),
                Pair(
                    R.string.sectionedlist_expandable_sections_menu,
                    Screen.SectionedListExpandableScreen.route
                )
            )
        ) {
            header {
                Title(stringResource(R.string.sectionedlist_samples_title))
            }

            loaded { item ->
                Chip(
                    label = {
                        Text(text = stringResource(id = item.first))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(item.second) },
                    colors = ChipDefaults.primaryChipColors()
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
