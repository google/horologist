package com.google.android.horologist.auth.composables.material3.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.auth.composables.material3.R
import com.google.android.horologist.auth.composables.material3.theme.HorologistMaterialTheme
import com.google.android.horologist.images.base.paintable.DrawableResPaintable

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreview() {
    HorologistMaterialTheme {
        SignedInConfirmationDialogContent(
            modifier = Modifier.fillMaxSize(),
            name = "Maggie",

            avatar = DrawableResPaintable(R.drawable.avatar_small_3),
        )
    }
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationMMMScreenPreview() {
    HorologistMaterialTheme {
        SignedInConfirmationDialogContent(
            modifier = Modifier.fillMaxSize(),
            name = "MMMMMMMMM",
            email = "MMMMMMMMMMMMMMMMMMMMMMMM",
            avatar = DrawableResPaintable(R.drawable.avatar_small_3),
        )
    }
}
@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenContentPreview() {
    HorologistMaterialTheme {
        SignedInConfirmationDialogContent(
            modifier = Modifier.fillMaxSize(),
            name = "Maggie",
            email = "maggiesveryveryverylongworkemail@example.com",
            avatar = DrawableResPaintable(R.drawable.avatar_small_3),
        )
    }
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationNoAvatar() {
    HorologistMaterialTheme {
        SignedInConfirmationDialogContent(
            modifier = Modifier.fillMaxSize(),
            name = "Timothy",
            email = "timandrews123@example.com",
        )
    }
}
@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreviewNoName() {
    SignedInConfirmationScreen(
        onDismissOrTimeout = {},
        email = "timandrews123@example.com",
        avatar = DrawableResPaintable(R.drawable.avatar_small_3),
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreviewNoEmail() {
    SignedInConfirmationScreen(
        onDismissOrTimeout = {},
        name = "Maggie",
    )
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreviewNoInformation() {
    SignedInConfirmationScreen(onDismissOrTimeout = {})
}

@WearPreviewDevices
@Composable
fun SignedInConfirmationScreenPreviewTruncation() {
    SignedInConfirmationScreen(
        onDismissOrTimeout = {},
        name = "Wolfeschlegelsteinhausenbergerdorff",
        email = "wolfeschlegelsteinhausenbergerdorff@example.com",
    )
}
