/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.auth.sample.screens.common.streamline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.composables.dialogs.AVATAR_SIZE
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.sample.R
import com.google.android.horologist.auth.ui.common.screens.streamline.StreamlineSignInScreen
import com.google.android.horologist.auth.ui.common.screens.streamline.StreamlineSignInViewModel
import com.google.android.horologist.base.ui.components.ConfirmationDialog
import com.google.android.horologist.base.ui.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

@Composable
fun StreamlineSignInSampleScreen(
    navController: NavHostController,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: StreamlineSignInViewModel<AuthUser, AccountUiModel> = viewModel(
        factory = StreamlineSignInSampleViewModelFactory
    )
) {
    var showNoAccountsAvailableDialog by rememberSaveable { mutableStateOf(false) }

    StreamlineSignInScreen(
        accountName = { account -> account.name ?: "" },
        accountEmail = { account -> account.email },
        selectAccountAvatarContent = {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(ChipDefaults.LargeIconSize)
                    .clip(CircleShape)
            )
        },
        signedInConfirmationAvatarContent = { account ->
            Box(
                modifier = Modifier
                    .size(AVATAR_SIZE)
                    .background(color = Color(0xFF4ECDE6), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = account.name?.first()?.uppercase() ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF202124),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        onSignedInConfirmationDialogDismissOrTimeout = { navController.popBackStack() },
        onNoAccountsAvailable = { showNoAccountsAvailableDialog = true },
        viewModel = viewModel,
        columnState = columnState,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Default.Android,
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
            )
        }
    }

    if (showNoAccountsAvailableDialog) {
        ConfirmationDialog(
            onTimeout = navController::popBackStack
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.common_screens_streamline_no_accounts_message)
            )
        }
    }
}
