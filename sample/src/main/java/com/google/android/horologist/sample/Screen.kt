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

sealed class Screen(
    val route: String,
) {
    object Menu : Screen("menu")
    object FillMaxRectangle : Screen("fmr")
    object ScrollAway : Screen("scrollAway")
    object ScrollAwaySLC : Screen("scrollAwaySLC")
    object ScrollAwayColumn : Screen("scrollAwayColumn")
    object Volume : Screen("volume")
    object DatePicker : Screen("datePicker")
    object TimePicker : Screen("timePicker")
    object TimeWithSecondsPicker : Screen("timeWithSecondsPicker")
    object TimeWithoutSecondsPicker : Screen("timeWithoutSecondsPicker")
    object Network : Screen("network")

    object MaterialButtonsScreen : Screen("materialButtonsScreen")
    object MaterialChipsScreen : Screen("materialChipsScreen")
    object MaterialChipIconWithProgressScreen : Screen("materialChipIconWithProgressScreen")
    object MaterialCompactChipsScreen : Screen("materialCompactChips")
    object MaterialConfirmationScreen : Screen("materialConfirmationScreen")
    object MaterialIconScreen : Screen("materialIconScreen")
    object MaterialOutlinedChipScreen : Screen("materialOutlinedChipScreen")
    object MaterialOutlinedCompactChipScreen : Screen("materialOutlinedCompactChipScreen")
    object MaterialSplitToggleChipScreen : Screen("materialSplitToggleChipScreen")
    object MaterialStepperScreen : Screen("materialStepperScreen")
    object MaterialTitleScreen : Screen("materialTitleScreen")
    object MaterialToggleButtonScreen : Screen("materialToggleButtonScreen")
    object MaterialToggleChipScreen : Screen("materialToggleChipScreen")

    object DataLayerNodes : Screen("nodes")

    object SectionedListMenuScreen : Screen("sectionedListMenuScreen")
    object SectionedListStatelessScreen : Screen("sectionedListStatelessScreen")
    object SectionedListStatefulScreen : Screen("sectionedListStatefulScreen")
    object SectionedListExpandableScreen : Screen("sectionedListExpandableScreen")

    object RotaryMenuScreen : Screen("rotaryMenuScreen")

    object RotaryScrollScreen : Screen("rotaryScrollScreen")
    object RotaryScrollReversedScreen : Screen("rotaryScrollReversedScreen")
    object RotaryScrollWithFlingScreen : Screen("rotaryScrollWithFlingScreen")
    object RotarySnapListScreen : Screen("rotarySnapListScreen")

    object Paging : Screen("paging")
    object PagingItem : Screen("pagingItem?id={id}")

    object PagerScreen : Screen("pagerScreen")
    object VerticalPagerScreen : Screen("verticalPagerScreen")
}
