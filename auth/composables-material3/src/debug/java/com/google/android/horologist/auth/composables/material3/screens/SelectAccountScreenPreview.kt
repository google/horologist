package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.runtime.Composable
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.auth.composables.material3.theme.HorologistMaterialTheme

@WearPreviewDevices
@Composable
fun SelectAccountScreenPreview(){
    HorologistMaterialTheme {
        SelectAccountScreen(
            accounts = listOf(
                AccountUiModel(
                    email = "timandrews123@example.com",
                    name = "Timothy Andrews",
                    ),
                AccountUiModel(
                    email = "thisisaverylongemailaccountsample@example.com",
                    name = "Kim Wong"
                ),
            ),
            onAccountClicked = { _, _ -> },
            title = "Select Account",
        )
    }
}


@WearPreviewDevices
@Composable
fun SelectAccountScreenManyAccountsPreview(){
    HorologistMaterialTheme {
        SelectAccountScreen(
            accounts = listOf(
                AccountUiModel(
                    email = "thisisaverylongemailaccountsample@example.com",
                    name = "Extenta Namuratus Hereditus III"
                ),
                AccountUiModel(
                    email = "timandrews123@example.com",
                    name = "Timothy Andrews",
                ),
                AccountUiModel(
                    email = "john@example.com",
                    name = "John Doe",
                ),
                AccountUiModel(
                    email = "john@example.com",
                    name = "John Doe",
                ),
            ),
            onAccountClicked = { _, _ -> },
            title = "Select Account",
        )
    }
}
