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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.SecondaryTitle

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )

    ScreenScaffold(scrollState = columnState) {
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
                Chip(
                    label = "Material3",
                    onClick = { navigateToRoute(Screen.Material3.route) },
                )
            }
            item {
                Chip(
                    label = "Networks",
                    onClick = { navigateToRoute(Screen.Network.route) },
                )
            }
            item {
                Chip(
                    label = "Fill Max Rectangle",
                    onClick = { navigateToRoute(Screen.FillMaxRectangle.route) },
                )
            }
            item {
                Chip(
                    label = "Volume Screen",
                    onClick = { navigateToRoute(Screen.Volume.route) },
                )
            }
            item {
                SecondaryTitle("Composables")
            }
            item {
                Chip(
                    label = "Time Picker",
                    onClick = { navigateToRoute(Screen.TimePicker.route) },
                )
            }
            item {
                Chip(
                    label = "Date Picker",
                    onClick = { navigateToRoute(Screen.DatePicker.route) },
                )
            }
            item {
                Chip(
                    label = "From Date Picker",
                    onClick = { navigateToRoute(Screen.FromDatePicker.route) },
                )
            }
            item {
                Chip(
                    label = "To Date Picker",
                    onClick = { navigateToRoute(Screen.ToDatePicker.route) },
                )
            }
            item {
                Chip(
                    label = "Time With Seconds Picker",
                    onClick = { navigateToRoute(Screen.TimeWithSecondsPicker.route) },
                )
            }
            item {
                Chip(
                    label = "Time Without Seconds Picker",
                    onClick = { navigateToRoute(Screen.TimeWithoutSecondsPicker.route) },
                )
            }

            item {
                Chip(
                    label = stringResource(id = R.string.sectionedlist_samples_menu),
                    onClick = { navigateToRoute(Screen.SectionedListMenuScreen.route) },
                )
            }

            item {
                SecondaryTitle("Material Components")
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_alert_dialog),
                    onClick = { navigateToRoute(Screen.MaterialAlertDialog.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_animated_components),
                    onClick = { navigateToRoute(Screen.MaterialAnimatedComponents.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_buttons),
                    onClick = { navigateToRoute(Screen.MaterialButtonsScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_cards),
                    onClick = { navigateToRoute(Screen.MaterialCardsScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_chips),
                    onClick = { navigateToRoute(Screen.MaterialChipsScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_chip_icon_with_progress),
                    onClick = { navigateToRoute(Screen.MaterialChipIconWithProgressScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_compact_chip),
                    onClick = { navigateToRoute(Screen.MaterialCompactChipsScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_confirmation_screen),
                    onClick = { navigateToRoute(Screen.MaterialConfirmationScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_confirmation_launcher),
                    onClick = { navigateToRoute(Screen.MaterialConfirmationLauncher.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_icon),
                    onClick = { navigateToRoute(Screen.MaterialIconScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_outlined_chips),
                    onClick = { navigateToRoute(Screen.MaterialOutlinedChipScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_outlined_compact_chips),
                    onClick = { navigateToRoute(Screen.MaterialOutlinedCompactChipScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_split_toggle_chips),
                    onClick = { navigateToRoute(Screen.MaterialSplitToggleChipScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_stepper),
                    onClick = { navigateToRoute(Screen.MaterialStepperScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_title),
                    onClick = { navigateToRoute(Screen.MaterialTitleScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_toggle_button),
                    onClick = { navigateToRoute(Screen.MaterialToggleButtonScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_toggle_chip),
                    onClick = { navigateToRoute(Screen.MaterialToggleChipScreen.route) },
                )
            }
            item {
                SecondaryTitle("Rotary Scrolling")
            }
            item {
                Chip(
                    label = stringResource(R.string.paging_chip_label),
                    onClick = { navigateToRoute(Screen.Paging.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(R.string.pager_screen_chip_label),
                    onClick = { navigateToRoute(Screen.PagerScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(R.string.pager_screen_chip_label),
                    onClick = { navigateToRoute(Screen.VerticalPagerScreen.route) },
                )
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun MenuScreenPreview() {
    MenuScreen(
        modifier = Modifier.fillMaxSize(),
        navigateToRoute = {},
    )
}
