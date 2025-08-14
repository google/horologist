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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.R
import com.google.android.horologist.auth.composables.chips.AccountChip
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.images.base.paintable.Paintable

/**
 * A screen to display a list of available accounts and to allow the user select one of them.
 *
 * <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/select_account_screen.png" height="120" width="120"/>
 */
@Deprecated(
    message = "Please use SelectAccountScreen in the composables-material3 module",
)
@Composable
public fun SelectAccountScreen(
    accounts: List<AccountUiModel>,
    onAccountClicked: (index: Int, account: AccountUiModel) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.horologist_select_account_title),
    defaultAvatar: Paintable? = Icons.Default.AccountCircle.asPaintable(),
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            modifier = modifier,
            columnState = columnState,
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = title,
                        modifier = Modifier.listTextPadding(),
                        style = MaterialTheme.typography.button,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3,
                    )
                }
            }

            items(accounts.size) { index ->
                val account = accounts[index]
                MaterialTheme(
                    typography = MaterialTheme.typography.copy(
                        button = MaterialTheme.typography.button.copy(
                            lineBreak = LineBreak(
                                strategy = LineBreak.Strategy.Balanced,
                                strictness = LineBreak.Strictness.Normal,
                                wordBreak = LineBreak.WordBreak.Default,
                            ),
                        ),
                    ),
                ) {
                    AccountChip(
                        account = account,
                        onClick = { onAccountClicked(index, account) },
                        colors = ChipDefaults.secondaryChipColors(),
                        defaultAvatar = defaultAvatar,
                    )
                }
            }
        }
    }
}
