package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
private fun defaultContentPadding(): PaddingValues {
    val (screenWidthDp, screenHeightDp) = LocalConfiguration.current.run {
        screenWidthDp.dp to screenHeightDp.dp
    }

    val horizontalPadding = (screenWidthDp * 0.052f)
    val topPadding = (screenHeightDp * 0.1f)
    return PaddingValues(
        start = horizontalPadding,
        top = topPadding,
        end = horizontalPadding,
    )
}

@Composable
fun SelectAccountScreen(
    accounts: List<AccountUiModel>,
    onAccountClicked: (index: Int, account: AccountUiModel) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.horologist_select_account_title),
    // TODO common module for auth composables
    defaultAvatar: Paintable? = Icons.Default.AccountCircle.asPaintable(),
    contentPadding: PaddingValues = defaultContentPadding()
) {
    val state = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    val nameTextStyle = // MaterialTheme.typography.copy(
        MaterialTheme.typography.titleMedium.copy(
            lineBreak = LineBreak(
                strategy = LineBreak.Strategy.Balanced,
                strictness = LineBreak.Strictness.Normal,
                wordBreak = LineBreak.WordBreak.Default,
            )
//        ),
    )


    TransformingLazyColumn(
        state = state, contentPadding = contentPadding
    ) {
        item {
            ListHeader(
            ) {
                Text(
                    text = title, style = MaterialTheme.typography.titleLarge, maxLines = 2
                )
            }
        }
        val nameLineBreak = LineBreak(
            strategy = LineBreak.Strategy.Balanced,
            strictness = LineBreak.Strictness.Normal,
            wordBreak = LineBreak.WordBreak.Default,
        )
        accounts.forEach { account ->
            item {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this@item, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                    icon = {
                        (account.avatar ?: defaultAvatar)?.rememberPainter()?.let { painter ->
                            Icon(painter, contentDescription = null)
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    secondaryLabel = {
                        Text(
                            account.email,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                ) {
                    Text(
                        account.name,
                        style = nameTextStyle,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
//    }
}