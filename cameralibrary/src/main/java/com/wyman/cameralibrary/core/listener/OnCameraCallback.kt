package com.wyman.cameralibrary.core.listener

/**
 * Created by wyman
 * on 2019-09-13.
 * 相机回调
 */
interface OnCameraCallback{
    //
    fun onCameraOpened()

    // 预览回调
    fun onPreviewCallback(data : ByteArray)
}

