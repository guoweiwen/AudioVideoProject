package com.wyman.fakedouyin.common.callback

/**
 * Created by wyman
 * on 2019-07-03.
 * 权限确认或取消接口
 */
interface PermissionListener{

    fun onGranted()

    fun onDenied(deniedPermissions : List<String>)
}