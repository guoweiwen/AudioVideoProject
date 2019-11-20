package com.wyman.cameralibrary.core.listener

import java.nio.ByteBuffer

/**
 * Created by wyman
 * on 2019-09-13.
 * 截帧监听器
 */
interface OnCaptureListener{
    // 截帧回调
    fun onCapture(buffer : ByteBuffer,width : Int,height : Int)
}
