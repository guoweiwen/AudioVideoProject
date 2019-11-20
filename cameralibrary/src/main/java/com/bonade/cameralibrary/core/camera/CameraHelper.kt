package com.bonade.cameralibrary.core.camera

import android.content.Context
import com.bonade.cameralibrary.core.characteristic.LensPosition
import com.bonade.cameralibrary.core.concurrent.CameraExecutor
import com.bonade.cameralibrary.core.selector.CameraSelector
import com.bonade.cameralibrary.core.selector.back
import com.bonade.cameralibrary.core.selector.front
import com.bonade.utillibrary.selector.firstAvailable

/**
 * 摄像头调用类
 * */
class CameraHelper @JvmOverloads constructor(
    context: Context,
    cameraType : CameraSelector = firstAvailable(//传入 front() 或 back()
            front(),
            back()
    ),
    private val exceutor : CameraExecutor = EXECUTOR
){

    companion object{
        private val EXECUTOR = CameraExecutor()
    }

}