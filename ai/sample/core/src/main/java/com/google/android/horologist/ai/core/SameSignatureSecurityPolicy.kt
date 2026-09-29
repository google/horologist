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

import android.content.pm.PackageManager
import android.os.Process
import io.grpc.Status
import io.grpc.binder.SecurityPolicy

/**
 * gRPC Binder [SecurityPolicy] that only allows peers signed with the same certificate as this app.
 *
 * Unlike [io.grpc.binder.SecurityPolicies.hasSignature], which also requires the peer to be one
 * specific package, this allows any app from the same developer, so the inference service in one
 * sample app can be used by another.
 *
 * @param packageName this app's package name.
 * @param requiredPackageName if set, the peer must also own this package. Use this on the client to
 *   check that the bound service is the package that was requested.
 */
class SameSignatureSecurityPolicy(
  private val packageManager: PackageManager,
  private val packageName: String,
  private val requiredPackageName: String? = null,
) : SecurityPolicy() {
  override fun checkAuthorization(uid: Int): Status {
    val peerPackages = packageManager.getPackagesForUid(uid).orEmpty().toList()

    if (requiredPackageName != null && requiredPackageName !in peerPackages) {
      return Status.PERMISSION_DENIED.withDescription(
        "Peer uid $uid is not package $requiredPackageName"
      )
    }

    if (uid == Process.myUid()) {
      return Status.OK
    }

    val candidates = requiredPackageName?.let { listOf(it) } ?: peerPackages
    val sameSignature = candidates.any {
      packageManager.checkSignatures(packageName, it) == PackageManager.SIGNATURE_MATCH
    }
    if (sameSignature) {
      return Status.OK
    }

    return Status.PERMISSION_DENIED.withDescription(
      "Peer uid $uid is not signed with the same certificate"
    )
  }
}
