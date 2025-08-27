/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.images.base.paintable.Paintable

@Composable
public fun SelectAccountScreen(
    accounts: List<AccountUiModel>,
    onAccountClicked: (index: Int, account: AccountUiModel) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.horologist_select_account_title),
    defaultAvatar: Paintable = Icons.Outlined.AccountCircle.asPaintable(),
    contentPadding: PaddingValues = defaultContentPadding(),
) {
    val state = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    val emailTextStyle = MaterialTheme.typography.labelSmall.copy(
        lineBreak = LineBreak(
            strategy = LineBreak.Strategy.Balanced,
            strictness = LineBreak.Strictness.Normal,
            wordBreak = LineBreak.WordBreak.Default,
        ),
    )

    TransformingLazyColumn(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
    ) {
        item {
            ListHeader {
                Text(text = title, style = MaterialTheme.typography.titleLarge, maxLines = 2)
            }
        }
        accounts.forEachIndexed { index, account ->
            item {
                val hasAvatar = account.avatar != null
                Button(
                    onClick = { onAccountClicked(index, account) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this@item, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                    icon = {
                        if (hasAvatar) {
                            Image(
                                account.avatar.rememberPainter(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ButtonDefaults.LargeIconSize),
                            )
                        } else {
                            Icon(
                                defaultAvatar.rememberPainter(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ButtonDefaults.IconSize),
                            )
                        }
                    },
                    contentPadding = if (hasAvatar) {
                        ButtonDefaults.ButtonWithLargeIconContentPadding
                    } else {
                        ButtonDefaults.ContentPadding
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    secondaryLabel = {
                        Text(
                            account.email,
                            style = emailTextStyle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                ) {
                    Text(
                        account.name,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@Composable
private fun defaultContentPadding(): PaddingValues {
    val (screenWidthDp, screenHeightDp) = LocalConfiguration.current.run {
        screenWidthDp.dp to screenHeightDp.dp
    }

    val horizontalPadding = (screenWidthDp * 0.052f)
    val topPadding = (screenHeightDp * 0.1f)
    val bottomPadding = (screenHeightDp * 0.3646f)
    return PaddingValues(
        start = horizontalPadding,
        top = topPadding,
        end = horizontalPadding,
        bottom = bottomPadding,
    )
}
