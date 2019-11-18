package com.bonade.cameralibrary.listener

import com.bonade.cameralibrary.model.GalleryType

/**
 * Created by wyman
on 2019-09-13.
 * 媒体拍摄回调
 */
interface OnPreviewCaptureListener{
    // 媒体选择
    fun onMediaSelectedListener(path : String,type : GalleryType)
}