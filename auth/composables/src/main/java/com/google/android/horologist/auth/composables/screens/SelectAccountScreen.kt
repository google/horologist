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

package com.google.android.horologist.auth.composables.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ChipType
import com.google.android.horologist.compose.material.Title

private const val HORIZONTAL_PADDING_SCREEN_PERCENTAGE = 0.052

/**
 * A screen to display a list of available accounts and to allow the user select one of them.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/select_account_screen.png" height="120" width="120"/>
 */
@ExperimentalHorologistApi
@Composable
public fun SelectAccountScreen(
    accounts: List<AccountUiModel>,
    onAccountClicked: (index: Int, account: AccountUiModel) -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.horologist_select_account_title),
    defaultAvatar: Any? = Icons.Default.AccountCircle
) {
    val configuration = LocalConfiguration.current
    val horizontalPadding = (configuration.screenWidthDp * HORIZONTAL_PADDING_SCREEN_PERCENTAGE).dp

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding),
        columnState = columnState
    ) {
        item { Title(title, Modifier.padding(bottom = 8.dp)) }

        items(accounts.size) { index ->
            val account = accounts[index]

            Chip(
                label = account.email,
                icon = account.avatar ?: defaultAvatar,
                largeIcon = true,
                onClick = { onAccountClicked(index, account) },
                chipType = ChipType.Secondary
            )
        }
    }
}
