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

package com.google.android.horologist.sectionedlist.expandable

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.composables.Section
import com.google.android.horologist.composables.SectionContentScope
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.composables.SectionedListScope
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.sample.R
import com.google.android.horologist.sectionedlist.component.SingleLineChip
import com.google.android.horologist.sectionedlist.component.SingleLineNoIconChip
import com.google.android.horologist.sectionedlist.component.Title

private val todayTasks = listOf(
    Pair("Meet with Sarah", Color.Red),
    Pair("Pay internet bill", Color.Gray),
    Pair("Piano lessons", Color.Gray)
)

private val tomorrowTasks = listOf(
    Pair("Book holidays", Color.Gray),
    Pair("Water plants", Color.Green)
)

private val laterTasks = listOf(
    Pair("Hang paintings", Color.Gray),
    Pair("Call mom", Color.Blue),
    Pair("Buy new runners", Color.Blue)
)

@Composable
fun SectionedListExpandableScreen(
    modifier: Modifier = Modifier,
    scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState(),
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    var todaySectionExpanded by rememberSaveable { mutableStateOf(true) }
    var tomorrowSectionExpanded by rememberSaveable { mutableStateOf(true) }
    var laterSectionExpanded by rememberSaveable { mutableStateOf(false) }

    val todaySectionState = getState(todaySectionExpanded, todayTasks)
    val tomorrowSectionState = getState(tomorrowSectionExpanded, tomorrowTasks)
    val laterSectionState = getState(laterSectionExpanded, laterTasks)

    SectionedList(
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier
    ) {
        section {
            loaded { Title(stringResource(R.string.sectionedlist_my_tasks)) }
        }

        taskSection(
            titleId = R.string.sectionedlist_today,
            state = todaySectionState,
            expanded = todaySectionExpanded,
            onHeaderClick = { todaySectionExpanded = !todaySectionExpanded }
        )

        taskSection(
            titleId = R.string.sectionedlist_tomorrow,
            state = tomorrowSectionState,
            expanded = tomorrowSectionExpanded,
            onHeaderClick = { tomorrowSectionExpanded = !tomorrowSectionExpanded }
        )

        taskSection(
            titleId = R.string.sectionedlist_later_week,
            state = laterSectionState,
            expanded = laterSectionExpanded,
            onHeaderClick = { laterSectionExpanded = !laterSectionExpanded },
            footerContent = {
                SingleLineNoIconChip(
                    text = stringResource(R.string.sectionedlist_more_tasks),
                    colors = ChipDefaults.primaryChipColors()
                )
            }
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

private fun getState(expanded: Boolean, list: List<Pair<String, Color>>) = if (expanded) {
    Section.State.Loaded(list)
} else {
    Section.State.Loaded(emptyList())
}

private fun SectionedListScope.taskSection(
    @StringRes titleId: Int,
    state: Section.State<Pair<String, Color>>,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    footerContent: @Composable (SectionContentScope.() -> Unit)? = null
) {
    section(state = state) {
        header {
            SectionHeader(
                text = stringResource(titleId),
                expanded = expanded,
                onClick = onHeaderClick
            )
        }

        loaded { (text, iconTint) ->
            SingleLineChip(
                text = text,
                imageVector = Icons.Outlined.Circle,
                iconTint = iconTint
            )
        }

        footerContent?.let {
            footer {
                footerContent()
            }
        }
    }
}

@Composable
private fun SectionHeader(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (expanded) {
                Icons.Default.ExpandLess
            } else {
                Icons.Default.ExpandMore
            },
            contentDescription = null
        )

        Text(text = text)
    }
}

@WearPreviewDevices
@Composable
fun SectionedListExpandableScreenPreview() {
    SectionedListExpandableScreen()
}
