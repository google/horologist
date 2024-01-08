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

package com.google.android.horologist.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Chip
import java.time.LocalDateTime

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
    time: LocalDateTime,
    columnState: ScalingLazyColumnState,
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier,
    ) {
        item {
            ListHeader {
                Text(text = "Samples")
            }
        }
        item {
            NetworkChip { navigateToRoute(Screen.Network.route) }
        }
        item {
            FillMaxRectangleChip(navigateToRoute)
        }
        item {
            VolumeScreenChip(navigateToRoute)
        }
        item {
            ListHeader {
                Text(text = "Scroll Away")
            }
        }
        item {
            ScrollAwayChip("Scroll Away") { navigateToRoute(Screen.ScrollAway.route) }
        }
        item {
            ScrollAwayChip("Scroll Away SLC") { navigateToRoute(Screen.ScrollAwaySLC.route) }
        }
        item {
            ScrollAwayChip("Scroll Away Column") { navigateToRoute(Screen.ScrollAwayColumn.route) }
        }
        item {
            ListHeader {
                Text(text = "Composables")
            }
        }
        item {
            TimePickerChip(time) { navigateToRoute(Screen.TimePicker.route) }
        }
        item {
            DatePickerChip(time) { navigateToRoute(Screen.DatePicker.route) }
        }
        item {
            TimeWithSecondsPickerChip(time) { navigateToRoute(Screen.TimeWithSecondsPicker.route) }
        }
        item {
            TimeWithoutSecondsPickerChip(time) { navigateToRoute(Screen.TimeWithoutSecondsPicker.route) }
        }

        item {
            Chip(
                label = stringResource(id = R.string.sectionedlist_samples_menu),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.SectionedListMenuScreen.route) },
            )
        }

        item {
            ListHeader {
                Text(text = "Material Components")
            }
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_buttons),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialButtonsScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_chips),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialChipsScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_chip_icon_with_progress),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialChipIconWithProgressScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_compact_chip),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialCompactChipsScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_confirmation_dialog),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialConfirmationScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_icon),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialIconScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_outlined_chips),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialOutlinedChipScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_outlined_compact_chips),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialOutlinedCompactChipScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_split_toggle_chips),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialSplitToggleChipScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_stepper),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialStepperScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_title),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialTitleScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_toggle_button),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialToggleButtonScreen.route) },
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.sample_material_toggle_chip),
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.MaterialToggleChipScreen.route) },
            )
        }
        item {
            ListHeader {
                Text(text = "Rotary Scrolling")
            }
        }
        item {
            androidx.wear.compose.material.Chip(
                label = {
                    Text(text = "Rotary scroll")
                },
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.RotaryMenuScreen.route) },
                colors = ChipDefaults.primaryChipColors(),
            )
        }
        item {
            PagingChip { navigateToRoute(Screen.Paging.route) }
        }
        item {
            PagerScreenChip { navigateToRoute(Screen.PagerScreen.route) }
        }
        item {
            VerticalPagerScreenChip { navigateToRoute(Screen.VerticalPagerScreen.route) }
        }
    }
}

@Composable
fun SampleChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String,
    content: (@Composable () -> Unit)? = null,
) {
    androidx.wear.compose.material.Chip(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        label = {
            Text(modifier = Modifier.weight(1f), text = label)
        },
        icon = {
            if (content != null) {
                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    content()
                }
            }
        },
    )
}

@WearPreviewDevices
@Composable
fun MenuScreenPreview() {
    MenuScreen(
        modifier = Modifier.fillMaxSize(),
        navigateToRoute = {},
        time = LocalDateTime.now(),
        columnState = belowTimeTextPreview(),
    )
}
