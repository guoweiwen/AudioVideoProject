package com.wyman.cameralibrary.core.hardware

import com.wyman.cameralibrary.core.orientation.Orientation
import com.wyman.cameralibrary.core.orientation.OrientationSensor
import java.lang.IllegalArgumentException
import kotlinx.coroutines.CompletableDeferred

internal open class Device (
        private val display : Display
) {
    //CompleteleDeferred （协程相关类）通信原语表示未来可知（可传达）的单个值， 这里被用于此目的
    private var selectedCameraDevice = CompletableDeferred<CameraDevice>()

    open fun getScreenOrientation() : Orientation{
        return display.getOrientation()
    }

    /**
     * 如果找不到摄像头就抛出异常
     * */
    fun getSelectedCamera() : CameraDevice = try {
        //CameraDevice 执行完通知这里
        selectedCameraDevice.getCompleted()
    } catch (e : IllegalArgumentException){
        throw IllegalArgumentException("Camera has not started.")
    }

    open fun clearSelectedCamera(){
        selectedCameraDevice = CompletableDeferred()
    }
}


internal fun Device.shutDown(
        orientationSensor: OrientationSensor
){
    orientationSensor.stop()

    val cameraDevice = getSelectedCamera()

    stop(cameraDevice)
}

internal fun Device.stop(cameraDevice : CameraDevice){
    cameraDevice.stopPreview()

    cameraDevice.close()

    clearSelectedCamera()
}