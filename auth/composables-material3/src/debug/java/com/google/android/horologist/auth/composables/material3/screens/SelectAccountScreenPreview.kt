package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.runtime.Composable
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.auth.composables.material3.models.AccountUiModel
import com.google.android.horologist.auth.composables.material3.theme.HorologistMaterialTheme
import com.google.android.horologist.images.base.paintable.DrawableResPaintable

@WearPreviewDevices
@Composable
fun SelectAccountScreenPreview(){
    HorologistMaterialTheme {
        SelectAccountScreen(
            accounts = listOf(
                AccountUiModel(
                    email = "timandrews123@example.com",
                    name = "Timothy Andrews",
                    avatar = DrawableResPaintable(R.drawable.avatar_small_1)
                    ),
                AccountUiModel(
                    email = "thisisaverylongemailaccountsample@example.com",
                    name = "Kim Wong",
                    avatar = DrawableResPaintable(R.drawable.avatar_small_2)
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

@WearPreviewDevices
@Composable
fun SelectAccountScreenOneLineAccountsPreview(){
    HorologistMaterialTheme {
        SelectAccountScreen(
            accounts = listOf(
                AccountUiModel(
                    email = "timandrews123@example.com",
                    name = "Tim Andrews",
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
