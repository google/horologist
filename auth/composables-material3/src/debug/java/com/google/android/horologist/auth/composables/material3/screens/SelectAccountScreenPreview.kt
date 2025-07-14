package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.runtime.Composable
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.auth.composables.material3.theme.HorologistMaterialTheme

@WearPreviewDevices
@Composable
fun SelectAccountScreenPreview(){

    HorologistMaterialTheme {
        TransformingLazyColumn { }
        AppScaffold {
        }
        SelectAccountScreen(
            accounts = listOf(
                AccountUiModel(email = "maggie@example.com"),
                AccountUiModel(email = "thisisaverylongemailaccountsample@example.com"),
            ),
            onAccountClicked = { _, _ -> },
            title = "Select Account",
            defaultAvatar = null,
        )
    }
}