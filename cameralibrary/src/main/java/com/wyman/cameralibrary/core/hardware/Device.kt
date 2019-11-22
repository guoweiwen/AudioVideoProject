package com.wyman.cameralibrary.core.hardware

import android.hardware.Camera
import com.wyman.cameralibrary.core.characteristic.LensPositionSelector
import com.wyman.cameralibrary.core.characteristic.getCharacteristics
import com.wyman.cameralibrary.core.concurrent.CameraExecutor
import com.wyman.cameralibrary.core.orientation.Orientation
import com.wyman.cameralibrary.core.orientation.OrientationSensor
import java.lang.IllegalArgumentException
import kotlinx.coroutines.CompletableDeferred

internal open class Device (
        private val display : Display,
        internal val executor : CameraExecutor,
        //Camera.getNumberOfCameras() Camera 本身的方法
        numberOfCameras : Int = Camera.getNumberOfCameras(),
        initialConfiguration : CameraConfiguration,
        initiaLensPositionSelector: LensPositionSelector//获得 迭代器

) {
    /**
     * 传入 CameraId 到 CamerDevice
     * * */
    private val cameras = (0 until numberOfCameras).map {
        //将每个摄像头Id存放到 CameraDevice
        cameraId -> CameraDevice(characteristics = getCharacteristics(cameraId))
    }

    private var lensPosition : LensPositionSelector = initiaLensPositionSelector


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