package com.wyman.cameralibrary.core.camera

import android.content.Context
import com.wyman.cameralibrary.core.selector.CameraSelector
import com.wyman.cameralibrary.core.selector.back
import com.wyman.cameralibrary.core.selector.front
import com.wyman.utillibrary.selector.firstAvailable

/**
 * 摄像头调用类
 * */
class CameraHelper @JvmOverloads constructor(
    context: Context,
    cameraType : CameraSelector = firstAvailable(//传入 front() 或 back()
            front(),
            back()
    ),
    private val exceutor : com.wyman.cameralibrary.core.concurrent.CameraExecutor = com.wyman.cameralibrary.core.camera.CameraHelper.Companion.EXECUTOR
){

    companion object{
        private val EXECUTOR = com.wyman.cameralibrary.core.concurrent.CameraExecutor()
    }

}