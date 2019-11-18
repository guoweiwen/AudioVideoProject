package com.bonade.utillibrary.permission

import android.annotation.SuppressLint
import androidx.versionedparcelable.VersionedParcelize

@SuppressLint("ParcelCreator")
@VersionedParcelize
class PermissionConfig private constructor(check: PermissionUtil){
    var forceAllPermissionsGranted: Boolean? = null

    var forceDeniedPermissionTips: String? = null

    var check: PermissionUtil? = null


    init {
        this.check = check
    }

    companion object{
        fun generatePermissionConfig(check : PermissionUtil) : PermissionConfig{
            val instance = PermissionConfig(check)
            return instance
        }
    }
}




















