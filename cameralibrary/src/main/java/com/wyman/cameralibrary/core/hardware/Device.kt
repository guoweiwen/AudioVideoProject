package com.wyman.cameralibrary.core.hardware

import android.hardware.Camera
import com.wyman.cameralibrary.core.characteristic.LensPositionSelector
import com.wyman.cameralibrary.core.characteristic.getCharacteristics
import com.wyman.cameralibrary.core.concurrent.CameraExecutor
import com.wyman.cameralibrary.core.orientation.Orientation
import com.wyman.cameralibrary.core.orientation.OrientationSensor
import java.lang.IllegalArgumentException
import kotlinx.coroutines.CompletableDeferred
import java.lang.IllegalStateException

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

    open fun hasSelectedCamera() = selectedCameraDevice.isCompleted

    /**
     * 选择摄像头，如果摄像头不被选择什么都不做
     * */
    open fun selectCamera(){
//        selectCamera(
//                availableCameras = cameras,
//        )
    }

}

/**
 * 从空闲状态打开摄像头
 * */
internal fun Device.bootStart(
        orientationSensor: OrientationSensor
){
    check(!hasSelectedCamera()) { "Camera has already start" }

    try{
        start()
//        startOrientationMontioring(
//                orientationSensor = orientationSensor
//        )
    } catch (e : Exception){
        e.printStackTrace()
    }
}

/**
 * 打开摄像头
 * */
internal fun Device.start(){
    selectCamera()
}

/**
 * 从可用的摄像头选择摄像头
 * */
//internal fun selectCamera(
//        availableCameras : List<CameraDevice>,
//        lensPositionSelector: LensPositionSelector
//) : CameraDevice?{
//    val lensPositions = availableCameras.map{it.characteristics.lensPosition}.toSet()
//    //选择需要的摄像头
//    val desiredPosition = lensPositionSelector(lensPositions)
//    return availableCameras.find{
//
//    }
//}

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