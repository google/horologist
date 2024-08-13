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
import androidx.compose.foundation.layout.fillMaxWidth
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
import java.time.LocalDateTime

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
    time: LocalDateTime,
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
                    onClick = {
                        navigateToRoute(Screen.Network.route)
                    },
                    label = "Networks",
                )
            }
            item {
                Chip(
                    onClick = { navigateToRoute(Screen.FillMaxRectangle.route) },
                    label = {
                        Text(modifier = Modifier.weight(1f), text = "Fill Max Rectangle")
                    },
                )
            }
            item {
                Chip(
                    onClick = { navigateToRoute(Screen.Volume.route) },
                    label = "Volume Screen",
                )
            }
            item {
                SecondaryTitle("Scroll Away")
            }
            item {
                SecondaryTitle("Composables")
            }
            item {
                Chip(
                    onClick = {
                        navigateToRoute(Screen.TimePicker.route)
                    },
                    label = "Time Picker",
                )
            }
            item {
                Chip(
                    onClick = {
                        navigateToRoute(Screen.DatePicker.route)
                    },
                    label = "Date Picker",
                )
            }
            item {
                Chip(
                    onClick = {
                        navigateToRoute(Screen.FromDatePicker.route)
                    },
                    label = "From Date Picker",
                )
            }
            item {
                Chip(
                    onClick = {
                        navigateToRoute(Screen.ToDatePicker.route)
                    },
                    label = "To Date Picker",
                )
            }
            item {
                Chip(
                    onClick = {
                        navigateToRoute(Screen.TimeWithSecondsPicker.route)
                    },
                    label = "Time With Seconds Picker",
                )
            }
            item {
                Chip(
                    onClick = {
                        navigateToRoute(Screen.TimeWithoutSecondsPicker.route)
                    },
                    label = "Time Without Seconds Picker",
                )
            }

            item {
                Chip(
                    label = stringResource(id = R.string.sectionedlist_samples_menu),
                    modifier = modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(Screen.SectionedListMenuScreen.route) },
                )
            }

            item {
                SecondaryTitle("Material Components")
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_alert_dialog),
                    modifier = modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(Screen.MaterialAlertDialog.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_animated_components),
                    modifier = modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(Screen.MaterialAnimatedComponents.route) },
                )
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
                    label = stringResource(id = R.string.sample_material_cards),
                    modifier = modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(Screen.MaterialCardsScreen.route) },
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
                    label = stringResource(id = R.string.sample_material_confirmation_screen),
                    modifier = modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(Screen.MaterialConfirmationScreen.route) },
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.sample_material_confirmation_launcher),
                    modifier = modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(Screen.MaterialConfirmationLauncher.route) },
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
                SecondaryTitle("Rotary Scrolling")
            }
            item {
                Chip(
                    onClick = {
                        navigateToRoute(Screen.Paging.route)
                    },
                    label = stringResource(R.string.paging_chip_label),
                )
            }
            item {
                Chip(
                    onClick = {
                        navigateToRoute(Screen.PagerScreen.route)
                    },
                    label = stringResource(R.string.pager_screen_chip_label),
                )
            }
            item {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navigateToRoute(Screen.VerticalPagerScreen.route)
                    },
                    label = stringResource(R.string.pager_screen_chip_label),
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
        time = LocalDateTime.now(),
    )
}
