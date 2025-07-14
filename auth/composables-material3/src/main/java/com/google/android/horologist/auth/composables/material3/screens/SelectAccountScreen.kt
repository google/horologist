package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.images.base.paintable.Paintable

@Composable
fun SelectAccountScreen(
    accounts: List<AccountUiModel>,
    onAccountClicked: (index: Int, account: AccountUiModel) -> Unit,
    modifier: Modifier = Modifier,
    title: String, //  = stringResource(id = R.string.horologist_select_account_title),
    // TODO common module for auth composables
    defaultAvatar: Paintable? = Icons.Default.AccountCircle.asPaintable(),
) {

    val state = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()
    ScreenScaffold(state) { contentPadding ->
        TransformingLazyColumn(state = state, contentPadding = contentPadding) {
            accounts.forEach { account ->
                item {
                    Button(
                        onClick = {},
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .transformedHeight(this@item, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(account.email)
                    }
                }
            }
        }
    }
}