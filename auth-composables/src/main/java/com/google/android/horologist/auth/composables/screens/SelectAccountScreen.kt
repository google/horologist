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
import com.google.android.horologist.auth.composables.ExperimentalHorologistAuthComposablesApi
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.base.ui.components.Title
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

private const val HORIZONTAL_PADDING_SCREEN_PERCENTAGE = 0.052

@ExperimentalHorologistAuthComposablesApi
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
        item { Title(text = title, modifier = Modifier.padding(bottom = 8.dp)) }

        items(accounts.size) { index ->
            val account = accounts[index]

            StandardChip(
                label = account.email,
                icon = account.avatar ?: defaultAvatar,
                largeIcon = true,
                onClick = { onAccountClicked(index, account) },
                chipType = StandardChipType.Secondary
            )
        }
    }
}

@ExperimentalHorologistAuthComposablesApi
public data class AccountUiModel(
    val email: String,
    val avatar: Any? = null
)
