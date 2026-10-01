/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.ai.core

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.Signature
import android.os.Process
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import io.grpc.Status
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class SameSignatureSecurityPolicyTest {
  private val context = ApplicationProvider.getApplicationContext<Context>()
  private val packageManager = context.packageManager
  private val myUid = Process.myUid()

  @Before
  fun setUp() {
    @Suppress("DEPRECATION")
    shadowOf(packageManager).getInternalMutablePackageInfo(context.packageName).signatures =
      arrayOf(MY_SIGNATURE)
    shadowOf(packageManager).setPackagesForUid(myUid, context.packageName)
    installPackage(SAME_SIGNED_PACKAGE, SAME_SIGNED_UID, MY_SIGNATURE)
    installPackage(OTHER_SIGNED_PACKAGE, OTHER_SIGNED_UID, OTHER_SIGNATURE)
  }

  @Test
  fun allowsOwnUid() {
    val policy = SameSignatureSecurityPolicy(packageManager, context.packageName)

    assertThat(policy.checkAuthorization(myUid).code).isEqualTo(Status.Code.OK)
  }

  @Test
  fun allowsOtherPackageWithSameSignature() {
    val policy = SameSignatureSecurityPolicy(packageManager, context.packageName)

    assertThat(policy.checkAuthorization(SAME_SIGNED_UID).code).isEqualTo(Status.Code.OK)
  }

  @Test
  fun rejectsPackageWithDifferentSignature() {
    val policy = SameSignatureSecurityPolicy(packageManager, context.packageName)

    assertThat(policy.checkAuthorization(OTHER_SIGNED_UID).code)
      .isEqualTo(Status.Code.PERMISSION_DENIED)
  }

  @Test
  fun requiredPackageMustMatchPeer() {
    val policy =
      SameSignatureSecurityPolicy(packageManager, context.packageName, SAME_SIGNED_PACKAGE)

    assertThat(policy.checkAuthorization(SAME_SIGNED_UID).code).isEqualTo(Status.Code.OK)
    assertThat(policy.checkAuthorization(myUid).code).isEqualTo(Status.Code.PERMISSION_DENIED)
  }

  @Test
  fun requiredPackageStillChecksSignature() {
    val policy =
      SameSignatureSecurityPolicy(packageManager, context.packageName, OTHER_SIGNED_PACKAGE)

    assertThat(policy.checkAuthorization(OTHER_SIGNED_UID).code)
      .isEqualTo(Status.Code.PERMISSION_DENIED)
  }

  private fun installPackage(packageName: String, uid: Int, signature: Signature) {
    @Suppress("DEPRECATION")
    val packageInfo =
      PackageInfo().apply {
        this.packageName = packageName
        signatures = arrayOf(signature)
      }
    shadowOf(packageManager).installPackage(packageInfo)
    shadowOf(packageManager).setPackagesForUid(uid, packageName)
  }

  companion object {
    private const val SAME_SIGNED_PACKAGE = "com.example.same"
    private const val OTHER_SIGNED_PACKAGE = "com.example.other"
    private const val SAME_SIGNED_UID = 20001
    private const val OTHER_SIGNED_UID = 20002
    private val MY_SIGNATURE = Signature("01020304")
    private val OTHER_SIGNATURE = Signature("05060708")
  }
}
